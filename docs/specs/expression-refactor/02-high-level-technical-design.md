# High-Level Technical Design

## Step 02 — High-Level Technical Design

### Requirements Traceability

| AC do Step 01 | Componente(s) responsáveis |
|---|---|
| AC-1: Literal avalia para si mesmo | `Literal` record implementando `Expression` |
| AC-2/3/4: Tipos de operação encapsulam lógica | `Sum`, `Subtract`, `Multiply`, `Divide`, `Power`, `Root` records |
| AC-3/4: Exceções de domínio lançadas localmente | Cada tipo lança `ArithmeticException` ou `NumericOverflowException` em seu `evaluate()` |
| AC-5: Contexto respeitado | Cada operação binária resolve `CalculationContext` e propaga para sub-expressões |
| AC-6/7: Contratos REST preservados | Controllers constroem `Expression` via DSL e usam `.evaluate()` / `.evaluate(context)` |
| AC-8: `CalculatorService` removido | Domínio elimina service; controllers orquestram diretamente |

### Architecture Diagram

```
Antes (procedural):                  Depois (OO/Expression-first):
                                      
   ┌──────────────┐                 ┌──────────────┐
   │  REST JSON   │                 │  REST JSON   │
   └──────┬───────┘                 └──────┬───────┘
          │                                │
   ┌──────▼───────┐                 ┌──────▼───────┐
   │ Controllers   │                 │ Controllers   │
   │ (DTOs in/out) │                 │ (DTOs in/out) │
   └──┬────────┬───┘                 └──┬────────┬───┘
      │        │                         │        │
   ┌──▼──┐  ┌─▼─────────────┐      ┌───▼────────▼───┐
   │/sum │  │  /evaluate    │      │  Expression    │
   │/sub │  │  (recursivo)  │      │  (sealed)      │
   │...  │  │               │      │  evaluate(ctx) │
   └──┬──┘  └──────┬────────┘      └───────┬────────┘
      │            │                         │
   ┌──▼───────────▼──┐            ┌──────────▼──────────┐
   │ CalculatorService│            │ ┌──┬──┬──┬──┬──┬──┐│
   │ sum(), sub()...  │            │ │Su│Su│Mu│Di│Po│Ro││
   │ evaluate()switch │            │ │m │bt│lt│vi│we│ot││
   └────────┬─────────┘            │ │  │ra│ip│de│r │  │
            │                    │ └──┴──┴──┴──┴──┴──┘│
       ┌────▼────┐               └─────────────────────┘
       │ Operator│
       │ enum    │
       └─────────┘
```

### Component Responsibilities

| Componente | Responsabilidade |
|---|---|
| `Expression` (interface) | Define contrato `evaluate(CalculationContext)`, `default evaluate()`. Fornece métodos fábrica estáticos com nomes ricos. |
| `Literal` | Encapsula um valor literal; `evaluate()` retorna `this`. |
| `Sum`, `Subtract`, `Multiply`, `Divide`, `Power`, `Root` | Cada um implementa sua lógica matemática com `BigDecimal`/`Math.pow` + lança exceções específicas. Recebe sub-expressões `Expression` e `CalculationContext`. |
| `CalculationContext` | Imutável; definido pelo usuário ou herdado; passado recursivamente na avaliação. |
| `CalculatorController` | Recebe DTOs, constrói `Expression` via DSL, chama `evaluate()`, empacota resultado em Response DTO. |
| `ApiErrorHandler` | Preservado — continua mapeando exceções de domínio para HTTP. |
| `*Dto` (REST) | Preservados inalterados — JSON de entrada/saída não muda. |

### Data Flow

**Endpoint direto (`POST /sum`):**
```
Request JSON → SumRequest DTO → Controller
                                    │
                                    ▼
                            Expression.sum(
                                Literal(firstAddend),
                                Literal(secondAddend)
                            ).evaluate()
                                    │
                                    ▼
                            Literal result → SumResponse(result.value())
```

**Endpoint `/evaluate`:**
```
Request JSON → EvaluateRequest DTO → Controller
                                          │
                                          ▼
                               mapToDomain(ExpressionDto)
                              constrói Expression via DSL
                                          │
                                          ▼
                              expression.evaluate(context)
                                          │
                                          ▼
                              Literal result → EvaluateResponse(result.value())
```

### Security and Observability Requirements

- **Nenhuma mudança de segurança** — a refatoração não expõe novos dados nem altera autenticação.
- **Observabilidade não é alterada** — todos os endpoints, códigos HTTP e mensagens de erro permanecem os mesmos.

### Trade-Offs and Alternatives

| Decisão | Alternativa | Por que descartada |
|---|---|---|
| Cada operação como tipo independente | Preservar `BinaryOperation` com `Operator` enum dentro | Não daria o encapsulamento OO buscado |
| `evaluate()` retorna `Literal` | `evaluate()` retorna `double` | `Literal` permite encadeamento funcional e preserva semântica de "resultado de expressão" |
| `Expression.sum(firstAddend, secondAddend)` | `Expression.of(Operator.SUM, left, right)` | Nomes genéricos quebram DSL legível; nomes ricos melhoram expressividade |
| Manter `CalculatorService` como adapter | Eliminar completamente | Service já não tem lógica própria; apenas delegava; adapter seria camada sem valor |
| `OperatorDto` removido já agora | Manter `OperatorDto` temporariamente | Preserva contrato JSON de `/evaluate`; remoção é evolução de API separada |

### High-Level Test Scenario Map

| Cenário | Tipo | Onde |
|---|---|---|
| Literal avalia para si mesmo | Unitário | `domain/LiteralTest.java` |
| Sum com dois literais | Unitário | `domain/SumTest.java` |
| Subtract, multiply com dois literais | Unitário | `domain/SubtractTest.java`, `domain/MultiplyTest.java` |
| Divide com divisor zero lança ArithmeticException | Unitário | `domain/DivideTest.java` |
| Divide com contexto (scale, roundingMode) | Unitário | `domain/DivideTest.java` |
| Power com overflow lança NumericOverflowException | Unitário | `domain/PowerTest.java` |
| Root com índice zero lança ArithmeticException | Unitário | `domain/RootTest.java` |
| Evaluação aninhada (expression tree) | Unitário | `domain/ExpressionTreeTest.java` |
| Preservação de contexto local/herdado | Unitário | `domain/ExpressionContextTest.java` |
| POST /sum retorna 200 com resultado correto | Integração | `controller/CalculatorControllerTest.java` |
| POST /evaluate com JSON antigo funciona | Integração | `controller/CalculatorEvaluateControllerTest.java` |
| POST /divide com divisor zero retorna 400 | Integração | `controller/CalculatorDivideControllerTest.java` |
