# Step 02 — High-Level Technical Design: Calculator Operations Refactor

## Requirements Traceability

| Acceptance Criteria (Step 01) | Artefato Técnico |
|---|---|
| AC1 — subtract com BigDecimal consistente | `SubtractOperation` implementa `Operation` usando `BigDecimal` |
| AC2 — divide default scale/rounding | `DivideOperation` usa `BigDecimal.divide()` com default `scale=10, HALF_UP` |
| AC3 — divide com scale/roundingMode explícito | `DivideRequest` com campos opcionais; `DivideOperation` recebe `CalculationContext` |
| AC4 — divisão por zero → 400 | `DivideOperation` valida divisor == 0 e lança `ArithmeticException` |
| AC5 — evaluate com Divide sem ctx → default | `BinaryOperation` carrega `CalculationContext` opcional; `evaluate()` aplica default se ausente |
| AC6 — regressão zero nas demais operações | `SumOperation`, `MultiplyOperation`, `PowerOperation`, `RootOperation` encapsulam lógica existente |
| AC7 — nova operação sem modificar CalculatorService | `Operation` interface + registry permite adicionar classe sem tocar em código existente |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────┐
│                       HTTP                               │
│  POST /calculator/{sum,subtract,multiply,divide,         │
│                    power,root,evaluate}                  │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│              CalculatorController                        │
│  • Converte DTOs → domínio                              │
│  • Valida profundidade máxima (10 níveis)                │
│  • Encaminha para CalculatorService                     │
│  • DivideRequest: extrai scale/roundingMode → ctx       │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│              CalculatorService                           │
│  • Delega para OperationRegistry                        │
│  • evaluate(): percorre árvore, dispatch via registry    │
│  • Métodos específicos delegam para Operation           │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│              OperationRegistry                           │
│  • Map<Operator, Operation>                              │
│  • Registro automático via Spring (beans)                │
│  • lookup(Operator) → Operation                          │
└────────────────────────┬─────────────────────────────────┘
                         │
    ┌────────────────────┼────────────────────┐
    ▼                    ▼                    ▼
┌──────────┐     ┌──────────────┐     ┌──────────────┐
│ SumOp    │     │ DivideOp     │     │ PowerOp      │
│ MultiplyOp│    │ (usa ctx)    │     │ RootOp       │
│ SubtractOp│    └──────────────┘     └──────────────┘
└──────────┘
```

---

## Component Responsibilities

| Component | Responsabilidade |
|---|---|
| `Operation` (interface) | Contrato: `double apply(double left, double right)` |
| `SumOperation` | `BigDecimal.valueOf(a).add(BigDecimal.valueOf(b))` |
| `SubtractOperation` | `BigDecimal.valueOf(a).subtract(BigDecimal.valueOf(b))` |
| `MultiplyOperation` | `BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(b))` |
| `DivideOperation` | `BigDecimal.divide(b, scale, roundingMode)`; default `10, HALF_UP` |
| `PowerOperation` | `Math.pow(base, exponent)` + validação overflow/NaN |
| `RootOperation` | `Math.pow(radicand, 1/index)` + validação overflow/NaN |
| `OperationRegistry` | `@Component` com `Map<Operator, Operation>` populado por injeção de dependência |
| `CalculationContext` | `record CalculationContext(int scale, RoundingMode roundingMode)` |
| `BinaryOperation` | Novo campo opcional: `CalculationContext context` |
| `CalculatorService` | Delega para `OperationRegistry`; `evaluate()` percorre árvore e passa ctx para `DIVIDE` |
| `DivideRequest` | Novos campos opcionais: `Integer scale`, `String roundingMode` |
| `EvaluateRequest` / `ExpressionDto` | Suporta `context` opcional no nó `DIVIDE` |
| `ApiErrorHandler` | Sem mudanças |

---

## Data Flow

### Fluxo POST /calculator/divide (com scale explícito)

```
DivideRequest { dividend: 1, divisor: 3, scale: 4, roundingMode: "HALF_DOWN" }
  → Controller: extrai scale=4, roundingMode=HALF_DOWN → CalculationContext(4, HALF_DOWN)
  → CalculatorService.divide(1, 3, ctx)
  → DivideOperation.apply(1, 3, ctx)
```

### Fluxo POST /calculator/evaluate (com BinaryOperation DIVIDE + context)

```json
{
  "expression": {
    "type": "operation",
    "operator": "DIVIDE",
    "left": { "type": "literal", "value": 1 },
    "right": { "type": "literal", "value": 3 },
    "context": { "scale": 4, "roundingMode": "HALF_DOWN" }
  }
}
```

```
Controller: mapeia JSON → BinaryOperationDto → BinaryOperation(..., ctx)
Service.evaluate(): ao encontrar Operator.DIVIDE, extrai ctx do nó
  → se ctx presente: divide(left, right, ctx)
  → se ausente: divide(left, right) com default
```

---

## Security and Observability Requirements

- **Segurança**: sem mudanças — operações já expostas via REST, sem autenticação no escopo
- **Observabilidade**: logs das operações (atuais) mantidos; adicionar log no `OperationRegistry` para rastrear qual implementação foi selecionada
- **Tratamento de erros**: mantido via `ApiErrorHandler` existente (400, 422)

---

## Trade-Offs and Alternatives

| Alternativa | Prós | Contras | Decisão |
|---|---|---|---|
| `Operation.apply(double, double, CalculationContext)` | Interface única para todas | Contexto irrelevante para 4/6 ops | **Rejeitado** |
| `CalculationContext` global via `@RequestScope` | Não polui domínio | Acoplamento com web scope | **Rejeitado** |
| `DivideOperation` com método separado `applyWithContext` | Interface pura mantida | Dispatch condicional no service | ✅ **Aceito** |
| `divide()` em `CalculatorService` recebe `CalculationContext` opcional | Coerente com a árvore de expressão | Service precisa saber qual operação usa ctx | ✅ **Aceito** |

---

## High-Level Test Scenario Map

| Cenário | Tipo de Teste | Operação |
|---|---|---|
| subtract com BigDecimal consistente | Unitário | `SubtractOperation` |
| divide com default scale/rounding | Unitário | `DivideOperation` |
| divide com scale/rounding explícito | Unitário | `DivideOperation` |
| divisão por zero → ArithmeticException | Unitário | `DivideOperation` |
| evaluate com Divide sem ctx → default | Unitário | `CalculatorService.evaluate()` |
| regressão sum/multiply/power/root | Unitário | Cada Operation |
| POST /calculator/divide com scale → 200 | Integração | Controller |
| POST /calculator/divide sem scale → 200 default | Integração | Controller |
| POST /calculator/subtract → resultado BigDecimal | Integração | Controller |
| POST /calculator/evaluate com DIVIDE + context | Integração | Controller |
| POST /calculator/evaluate com DIVIDE sem context | Integração | Controller |
| POST /calculator/divide com divisor=0 → 400 | Integração | Controller |
