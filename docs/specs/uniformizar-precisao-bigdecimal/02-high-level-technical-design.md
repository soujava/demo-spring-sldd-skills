# 02 — High-Level Technical Design: Uniformizar Precisão BigDecimal

## Requirements Traceability

| AC | Exploration Decision | Impacted Component |
|----|----------------------|---------------------|
| AC1 — Subtract com BigDecimal | #1: `subtract()` migra para BigDecimal | `CalculatorService` |
| AC2 — Divide com defaults | #2: `divide()` com scale=10, HALF_UP | `CalculatorService` |
| AC3 — Divide com parâmetros explícitos | #2: `divide(scale, roundingMode)` | `CalculatorService` |
| AC4 — Endpoint /divide com parâmetros opcionais | #8: `DivideRequest` + controller | `DivideRequest`, `CalculatorController` |
| AC5 — Endpoint /divide backward-compatible | #8: campos opcionais nulos = defaults | `DivideRequest`, `CalculatorController` |
| AC6 — ExpressionContext no compose | #4, #5: novo record + atributo em ExpressionNode | `ExpressionContext`, `ExpressionNode`, `ComposeExpressionValidator` |
| AC7 — Compose sem context usa defaults | #6, #7: context opcional | `ComposeExpressionValidator`, `CalculatorService.evaluate()` |
| AC8 — Testes adaptados | Todos | Camada de testes |

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│ Controller Layer                                            │
│                                                             │
│ POST /calculator/divide                                     │
│  ┌──────────────┐     ┌──────────────────────┐              │
│  │ DivideRequest │────▶│ CalculatorController │              │
│  │ +scale (opt)  │     │ .divide()           │              │
│  │ +roundingMode │     └──────────┬───────────┘              │
│  └──────────────┘                │                          │
│                          service.divide(d,d,s,rm)           │
│                                                             │
│ POST /calculator/compose                                    │
│  ┌──────────────────────┐                                   │
│  │ String (raw JSON)    │────▶  ┌────────────────────┐      │
│  └──────────────────────┘  ┌─── │ ComposeExpression  │      │
│                             │    │ Validator          │      │
│                             │    │ .parse()           │      │
│                             │    └────────┬───────────┘      │
│                             │             │ Expression       │
└─────────────────────────────┼─────────────┼──────────────────┘
                              │             │
                              ▼             ▼
┌─────────────────────────────────────────────────────────────┐
│ Domain Layer                                                │
│                                                             │
│ ┌─────────────────────┐    ┌─────────────────────────────┐  │
│ │ CalculatorService   │    │ Expression (sealed)         │  │
│ │ .subtract() ← BIG  │    │ ├── ExpressionLiteral       │  │
│ │ .divide(d,d,s,rm)  │◀───│ └── ExpressionNode          │  │
│ │ .evaluate()        │        ├── operation              │  │
│ └─────────────────────┘        ├── left                  │  │
│                                ├── right                 │  │
│ ┌─────────────────────┐        └── context ←── NEW       │  │
│ │ ExpressionContext   │◀─────┘    │                      │  │
│ │ ├── scale: int      │           │                      │  │
│ │ └── roundingMode    │           │                      │  │
│ │     : RoundingMode  │                                  │  │
│ │ DEFAULT = (10, HU) │◀─────────────────────────────────┘  │
│ └─────────────────────┘                                     │
└─────────────────────────────────────────────────────────────┘
```

## Component Responsibilities

| Component | Responsibility | Change |
|-----------|---------------|--------|
| `CalculatorService.subtract()` | Subtração com precisão BigDecimal | Implementação muda de `double` para `BigDecimal.valueOf()` |
| `CalculatorService.divide()` | Divisão com scale e rounding explícitos | Assinatura muda; adiciona `int scale`, `RoundingMode roundingMode` |
| `CalculatorService.evaluate()` | Avaliação recursiva de Expression | Despacha `scale`/`roundingMode` do `node.context()` ao chamar `divide()` |
| `ExpressionContext` | **NOVO** — encapsula scale e roundingMode | Record imutável com constante `DEFAULT` |
| `ExpressionNode` | Nó de operação na árvore de expressões | Adiciona campo `context` do tipo `ExpressionContext` |
| `DivideRequest` | DTO do endpoint /divide | Adiciona `Integer scale` e `String roundingMode` opcionais |
| `CalculatorController.divide()` | Handler do endpoint /divide | Lê scale/roundingMode do request; usa defaults se nulos |
| `ComposeExpressionValidator` | Parser + validação de JSON → Expression | Adiciona `context` ao `ALLOWED_FIELDS`; faz parse de `context` |
| `ApiErrorHandler` | Tratamento centralizado de erros | Sem mudança (já trata `ArithmeticException` e `IllegalArgumentException`) |

## Data Flow

### Flow 1: POST /calculator/divide (sem parâmetros opcionais)

```
Cliente → DivideRequest(dividend=10, divisor=3, scale=null, roundingMode=null)
  → CalculatorController.divide()
    → scale=null → default 10
    → roundingMode=null → default HALF_UP
    → CalculatorService.divide(10, 3, 10, HALF_UP)
      → BigDecimal.valueOf(10).divide(BigDecimal.valueOf(3), 10, HALF_UP)
      → 3.3333333333
    → DivideResponse(result=3.3333333333)
  → Cliente
```

### Flow 2: POST /calculator/divide (com parâmetros explícitos)

```
Cliente → DivideRequest(dividend=10, divisor=3, scale=5, roundingMode="HALF_UP")
  → CalculatorController.divide()
    → scale=5, roundingMode=HALF_UP
    → CalculatorService.divide(10, 3, 5, HALF_UP)
      → BigDecimal.valueOf(10).divide(BigDecimal.valueOf(3), 5, HALF_UP)
      → 3.33333
    → DivideResponse(result=3.33333)
  → Cliente
```

### Flow 3: POST /calculator/compose (DIVIDE com context)

```
Cliente → {"operation":"DIVIDE","left":10,"right":3,"context":{"scale":5,"roundingMode":"HALF_UP"}}
  → ComposeExpressionValidator.parse()
    → ExpressionNode(DIVIDE, Literal(10), Literal(3), ExpressionContext(5, HALF_UP))
  → CalculatorService.evaluate(node)
    → DIVIDE → divide(10, 3, 5, HALF_UP) → 3.33333
  → ComposeResponse(result=3.33333)
  → Cliente
```

### Flow 4: POST /calculator/compose (DIVIDE sem context — usa defaults)

```
Cliente → {"operation":"DIVIDE","left":10,"right":3}
  → ComposeExpressionValidator.parse()
    → ExpressionNode(DIVIDE, Literal(10), Literal(3), ExpressionContext.DEFAULT)
  → CalculatorService.evaluate(node)
    → DIVIDE → divide(10, 3, 10, HALF_UP) → 3.3333333333
  → ComposeResponse(result=3.3333333333)
  → Cliente
```

## Security and Observability Requirements

| Aspecto | Detalhe |
|---------|---------|
| Validação de `scale` | Rejeitar valores negativos ou zero; intervalo válido: 1–100 |
| Validação de `roundingMode` | Aceitar apenas valores válidos de `RoundingMode` (string → enum); rejeitar inválidos com 400 |
| Backward-compatibility | Campos opcionais nulos = defaults; requisições existentes continuam funcionando |
| Sem novos endpoints | Apenas extensão de endpoints existentes |

## Trade-Offs and Alternatives

| Decisão | Alternativa considerada | Motivo da escolha |
|---------|------------------------|-------------------|
| `ExpressionContext` como record separado | Campos `scale`/`roundingMode` direto em `ExpressionNode` | Extensibilidade futura; separação de concerns |
| `context` preservado em todos os nós | `context` permitido apenas em DIVIDE | Permite serialização round-trip; não quebra parsing |
| `scale`/`roundingMode` opcionais no `DivideRequest` | Sempre obrigatório | Backward-compatible; defaults são seguros |
| `RoundingMode` do `java.math` | Enum customizado | Reutiliza padrão JDK; sem inventar rodinha |
| `divide(BigDecimal, int, RoundingMode)` | `divide(BigDecimal, MathContext)` | Scale explícito é mais intuitivo e alinhado ao AC |

## High-Level Test Scenario Map

| Cenário | AC | Tipo | Camada |
|---------|-----|------|--------|
| Subtract com precisão BigDecimal (0.3-0.1=0.2) | AC1 | Unitário | Domínio |
| Divide com defaults (10÷3=3.3333333333) | AC2 | Unitário | Domínio |
| Divide com scale/rounding explícitos (10÷3 s=5) | AC3 | Unitário | Domínio |
| Divide com scale negativo → erro | — | Unitário | Domínio |
| Divide com roundingMode inválido → erro | — | Unitário | Domínio |
| Endpoint /divide com scale/rounding → 200 | AC4 | Integração | Controller |
| Endpoint /divide sem scale/rounding → 200 com defaults | AC5 | Integração | Controller |
| Endpoint /divide com roundingMode inválido → 400 | — | Integração | Controller |
| Endpoint /divide com scale negativo → 400 | — | Integração | Controller |
| Compose DIVIDE com context → resultado correto | AC6 | Integração | Controller |
| Compose DIVIDE sem context → defaults | AC7 | Integração | Controller |
| Compose com context + roundingMode inválido → 400 | — | Integração | Controller |
| evaluate() com DIVIDE usa context do nó | AC6, AC7 | Unitário | Domínio |
| evaluate() com SUBTRACT usa BigDecimal | AC1 | Unitário | Domínio |
| Testes existentes adaptados passam | AC8 | Ambos | Ambas |
