# Exploration Summary: Refatoração Expression-First

## Contexto

O projeto atual tem um modelo de expressões com `Expression` como `sealed interface` contendo `Literal` e `BinaryOperation`. A lógica de avaliação é procedural: `CalculatorService` faz pattern matching por `Operator` enum e delega para métodos imperativos.

## Decisões consolidadas

| # | Tópico | Decisão |
|---|---|---|
| 1 | **`Expression` comportamental** | Define `Literal evaluate(CalculationContext)` + `default Literal evaluate()`. Cada operação encapsula sua própria lógica. |
| 2 | **Limite de profundidade** | **Removido** — não faz mais sentido no novo modelo. |
| 3 | **Exceções de domínio** | Cada operação (`Divide`, `Root`, `Power`) lança suas próprias exceções no próprio `evaluate()`. |
| 4 | **Contrato JSON `/evaluate`** | **Preservado** — controller traduz `"type":"operation" + "operator":"DIVIDE"` → `Expression.divide(...)`. |
| 5 | **`Operator` enum (domínio)** | **Eliminado** — cada operação é um tipo `Expression` próprio. |
| 6 | **`OperatorDto`** | Mantido **temporariamente** apenas no DTO do endpoint `/evaluate` para preservar compatibilidade do JSON. |
| 7 | **`Literal.evaluate(context)`** | Retorna `this` (identidade). |
| 8 | **`BinaryOperation`** | **Deixa de existir** como tipo. |
| 9 | **Hierarquia** | `Expression` sealed com `Literal`, `Sum`, `Subtract`, `Multiply`, `Divide`, `Power`, `Root`. |
| 10 | **Fábricas/DSL** | Estáticos na interface `Expression` com **nomes ricos**: `sum(firstAddend, secondAddend)`, `divide(dividend, divisor)`, `power(base, exponent)`, `root(radicand, index)`, `literal(value)`. |
| 11 | **`CalculatorService`** | **Eliminado**. |
| 12 | **Endpoints REST** | **Mantidos** (`/sum`, `/subtract`, `/multiply`, `/divide`, `/power`, `/root`). Cada controller constrói `Expression` e chama `evaluate()`/ `evaluate(context)`. |
| 13 | **Contexto** | Quem recebe `CalculationContext` via request é responsável por passá-lo no `evaluate(context)`. Herança de contexto resolvida no `evaluate()` de cada operação binária. |
| 14 | **`Literal` como retorno** | Controllers usam `.value()` para compor as respostas dos DTOs. |

## Hierarquia de tipos proposta

```java
public sealed interface Expression { ... }
public record Literal(double value) implements Expression
public record Sum(Expression firstAddend, Expression secondAddend) implements Expression
public record Subtract(Expression minuend, Expression subtrahend) implements Expression
public record Multiply(Expression multiplicand, Expression multiplier) implements Expression
public record Divide(Expression dividend, Expression divisor) implements Expression
public record Power(Expression base, Expression exponent) implements Expression
public record Root(Expression radicand, Expression index) implements Expression
```

## Riscos identificados

- Exceções de domínio (divisão por zero, overflow, índice zero) ficam dispersas nos tipos — necessário validar cobertura de testes
- `OperatorDto` ainda existe no DTO de `/evaluate` — pode ser removido em futura iteração de API
- Fábricas com nomes ricos aumentam API da interface `Expression`, mas melhoram legibilidade do DSL

## Próximo passo

Formalizar em **Step 01 — Product Intent Specification**.
