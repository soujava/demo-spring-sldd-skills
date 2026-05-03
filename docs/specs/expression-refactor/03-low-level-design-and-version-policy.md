# Low-Level Design and Version Policy

## Step 03 — Low-Level Design and Version Policy

### Requirement-to-Design Traceability

| AC Step 01 | Decisão Step 02 | Contrato/Dados/Error/Test/Código |
|---|---|---|
| AC-1 Literal avalia para si mesmo | Literal implementa Expression | `Literal.evaluate()` retorna `this`; teste unitário `LiteralTest` |
| AC-2 Sum realiza adição | Tipo próprio com BigDecimal | `Sum` record com `firstAddend`, `secondAddend`; evaluate chama `add()` |
| AC-3 Divide lança exceção em divisor zero | Validação local no tipo | `Divide.evaluate()` checa `divisor == 0.0` antes do cálculo |
| AC-4 Power/Root detectam overflow/NaN | `roundedFinitePowerResult` move para `Power`/`Root` | Cada tipo valida `Double.isInfinite`/`NaN` |
| AC-5 Contexto respeitado | Propagação recursiva | Cada operação binária chama `left.evaluate(context)` e `right.evaluate(context)` |
| AC-6/7 Preservação de contrato REST | Controllers instanciam Expression via DSL | `CalculatorController` refatorado para não usar `CalculatorService`; mantém DTOs |
| AC-8 CalculatorService removido | Domínio elimina service | `CalculatorService.java` deletado; `BinaryOperation.java` deletado; `Operator.java` deletado |

### API Contracts

As APIs REST **não mudam**. O contrato existente é preservado.

**Endpoints preservados:**

| Método | Path | Request | Response | Status |
|---|---|---|---|---|
| POST | `/calculator/sum` | `SumRequest` | `SumResponse` | 200/400 |
| POST | `/calculator/subtract` | `SubtractRequest` | `SubtractResponse` | 200/400 |
| POST | `/calculator/multiply` | `MultiplyRequest` | `MultiplyResponse` | 200/400 |
| POST | `/calculator/divide` | `DivideRequest` | `DivideResponse` | 200/400 |
| POST | `/calculator/power` | `PowerRequest` | `PowerResponse` | 200/400 |
| POST | `/calculator/root` | `RootRequest` | `RootResponse` | 200/400/422 |
| POST | `/calculator/evaluate` | `EvaluateRequest` | `EvaluateResponse` | 200/400/422 |

**Sem mudanças nos DTOs de request/response.** O `EvaluateRequest` ainda usa `ExpressionDto` com `type="operation"` e `OperatorDto`.

### Data Models

**Domínio — Antes vs Depois:**

| Antes | Depois |
|---|---|
| `sealed interface Expression` | `sealed interface Expression` (comportamental) |
| `record Literal(double value)` | `record Literal(double value) implements Expression` |
| `record BinaryOperation(Operator, Expression, Expression)` | ~~REMOVIDO~~ |
| `enum Operator { SUM, ... }` | ~~REMOVIDO~~ |
| `class CalculatorService` | ~~REMOVIDO~~ |
| — | `record Sum(Expression firstAddend, Expression secondAddend) implements Expression` |
| — | `record Subtract(Expression minuend, Expression subtrahend) implements Expression` |
| — | `record Multiply(Expression multiplicand, Expression multiplier) implements Expression` |
| — | `record Divide(Expression dividend, Expression divisor) implements Expression` |
| — | `record Power(Expression base, Expression exponent) implements Expression` |
| — | `record Root(Expression radicand, Expression index) implements Expression` |

**Expression (interface):**

```java
public sealed interface Expression permits Literal, Sum, Subtract, Multiply, Divide, Power, Root {
    Literal evaluate(CalculationContext context);
    default Literal evaluate() { return evaluate(CalculationContext.defaults()); }

    static Expression literal(double value) { return new Literal(value); }
    static Expression sum(Expression firstAddend, Expression secondAddend) {
        return new Sum(firstAddend, secondAddend);
    }
    // ... etc para todas as operações
}
```

**Cada tipo operacional como record** (Java 25) com `implements Expression`.

**CalculationContext:** sem alterações.

### Error Model

**Sem alterações no mapeamento de exceções.** `ApiErrorHandler` continua centralizando:

| Exceção lançada por | HTTP Status | Motivo |
|---|---|---|
| `IllegalArgumentException` | 400 | profundidade excedida (REMOVIDA), mas mantido no handler |
| `ArithmeticException` | 400 | divisão por zero, índice zero, etc. |
| `NumericOverflowException` | 422 | resultado infinito/overflow |

**Fonte das exceções muda:** no lugar de `CalculatorService` métodos, os tipos `Expression` lançam diretamente.

- `Divide.evaluate()` → lança `ArithmeticException("Division by zero")`
- `Root.evaluate()` → lança `ArithmeticException("Invalid operation: result is undefined or imaginary")` quando índice zero
- `Power.evaluate()` / `Root.evaluate()` → lança `NumericOverflowException` quando `Double.isInfinite(result)`

### Test Strategy

**Convenções do projeto (AGENTS.md) que regem esta estratégia:**

> **Duas camadas de teste distintas, não misturadas:**

| Camada | Local | Tipo | Notas |
|--------|-------|------|-------|
| **Lógica de negócio** | `src/test/java/com/example/demo/domain/` | **JUnit puro, sem contexto Spring** | Instancie a classe diretamente. Zero anotações Spring. |
| **Borda** | `src/test/java/com/example/demo/controller/` | **Spring completo + MockMvcTester** | `@SpringBootTest` + `@AutoConfigureMockMvc`. Testam comportamento HTTP, não lógica. |

**Novos testes unitários (Step 04 — JUnit puro, sem Spring):**

| Teste | Tipo | Onde |
|---|---|---|
| `LiteralTest` | Unitário | `domain/LiteralTest.java` |
| `SumTest` | Unitário | `domain/SumTest.java` |
| `SubtractTest` | Unitário | `domain/SubtractTest.java` |
| `MultiplyTest` | Unitário | `domain/MultiplyTest.java` |
| `DivideTest` | Unitário | `domain/DivideTest.java` |
| `PowerTest` | Unitário | `domain/PowerTest.java` |
| `RootTest` | Unitário | `domain/RootTest.java` |
| `ExpressionTreeTest` | Unitário | `domain/ExpressionTreeTest.java` |

**Testes existentes a serem adaptados/removidos:**

| Teste | Tipo | Ação |
|---|---|---|
| `CalculatorServiceTest` et al. | Unitário (JUnit puro) | **REMOVER** — `CalculatorService` deixa de existir. Substituição: testes unitários dos novos tipos |
| `CalculationContextTest` | Unitário (JUnit puro) | **MANTER** — nenhuma alteração |

> **Importante:** os novos testes unitários **não usam `@SpringBootTest`**. Instanciam `Sum`, `Literal`, `Divide` diretamente via `new` ou DSL `Expression.sum(...)`.

**Testes de integração mantidos (Step 05 garante compatibilidade):**

| Teste | Tipo | Ação |
|---|---|---|
| `CalculatorControllerTest` | Integração | **MANTER** (mesmo JSON, mesmo status HTTP) |
| `CalculatorEvaluateControllerTest` | Integração | **MANTER** |
| `CalculatorSubtractControllerTest` | Integração | **MANTER** |
| `CalculatorMultiplyControllerTest` | Integração | **MANTER** |
| `CalculatorDivideControllerTest` | Integração | **MANTER** |
| `CalculatorPowerControllerTest` | Integração | **MANTER** |
| `CalculatorRootControllerTest` | Integração | **MANTER** |
| `OpenApiDocumentationTest` | Integração | **MANTER** |

> **Importante:** testes de integração não testam lógica de negócio — testam **comportamento HTTP** (status, formato JSON, roteamento).

### Test Scenario Catalog

**Unitário — Literal:**
- `evaluate_ReturnsSelf` → `Literal(10.0).evaluate()` retorna `Literal(10.0)`

**Unitário — Sum:**
- `evaluate_TwoLiterals_ReturnsSum` → `Sum(Literal(1.5), Literal(2.5)).evaluate()` = `Literal(4.0)`
- `evaluate_NestedExpression` → `Sum(Literal(1.0), Sum(Literal(2.0), Literal(3.0))).evaluate()` = `Literal(6.0)`

**Unitário — Subtract:**
- `evaluate_ReturnsDifference` → `Subtract(Literal(5.0), Literal(3.0)).evaluate()` = `Literal(2.0)`

**Unitário — Multiply:**
- `evaluate_TwoLiterals_ReturnsProduct` → `Multiply(Literal(2.0), Literal(3.0)).evaluate()` = `Literal(6.0)`
- `evaluate_WithBigDecimalPrecision` → valida uso de `BigDecimal.valueOf`

**Unitário — Divide:**
- `evaluate_ReturnsQuotient` → `Divide(Literal(6.0), Literal(2.0)).evaluate()` = `Literal(3.0)`
- `evaluate_WithContext_ScaleAndRounding` → `Divide(Literal(1.0), Literal(3.0)).evaluate(ctx(2))` = `Literal(0.33)`
- `evaluate_DivisorZero_ThrowsArithmeticException` → divisor=0.0

**Unitário — Power:**
- `evaluate_ReturnsPower` → `Power(Literal(2.0), Literal(3.0)).evaluate()` = `Literal(8.0)`
- `evaluate_Overflow_ThrowsNumericOverflowException` → `Power(Literal(2.0), Literal(1024.0))`
- `evaluate_WithContext_AppliesScale` → `Power(Literal(2.0), Literal(0.5)).evaluate(ctx(4))` = `Literal(1.4142)`

**Unitário — Root:**
- `evaluate_ReturnsRoot` → `Root(Literal(8.0), Literal(3.0)).evaluate()` = `Literal(2.0)`
- `evaluate_IndexZero_ThrowsArithmeticException`
- `evaluate_NegativeRadicandEvenIndex_NaN_ThrowsArithmeticException`

**Integração — Endpoints diretos:**
- Todos os cenários existentes preservados (mesmo JSON, mesmos status, mesmos códigos de erro)

**Integração — /evaluate:**
- Preservar todos os cenários existentes de JSON com `type="operation"`, `OperatorDto`

### Dependency and Version Policy

**Nenhuma nova dependência é necessária.** A mudança é estritamente interna ao código Java do domínio.

**Dependências existentes mantidas:**
- Spring Boot 4.0.5 (provêm controllers, exceções, testes)
- Jakarta Validation 3.x (DTOs)
- Jackson (deserialização JSON)
- JUnit 5 + `MockMvcTester` (testes)

**Impacto no version pinning:** zero. Todas as bibliotecas existentes continuam sendo usadas da mesma forma.

### Ordered Implementation Plan

**Convenções do projeto que regem a implementação:**

- Java 25: prefira `record`, `sealed interface`, `default` methods, construção via DSL fluente
- `JAVA25_CONVENTIONS.md`: remova imports não utilizados, use `var` para inferência quando óbvio, prefira method references
- Conventional Commits: cada commit deve seguir `tipo(escopo): descrição`
- Testes separados: nunca misture anotações Spring em testes de lógica de negócio

**Passo a passo:**

1. **Criar novos tipos `Expression`** (`Literal`, `Sum`, `Subtract`, `Multiply`, `Divide`, `Power`, `Root`)
   - Ordem de complexidade: `Literal` → `Sum`/`Subtract` → `Multiply` → `Divide` → `Power`/`Root`
   - Cada tipo lança suas próprias exceções conforme AC-3/4

2. **Adicionar métodos fábrica na interface `Expression`** com nomes ricos

3. **Adaptar `CalculatorController`**
   - Refatorar cada endpoint para construir `Expression` via DSL e chamar `evaluate()`
   - Refatorar `/evaluate` para mapear DTOs diretamente para tipos OO
   - **Remover lógica de `depth > 10`** (conforme exploração)

4. **Excluir arquivos obsoletos:**
   - `CalculatorService.java`
   - `BinaryOperation.java`
   - `Operator.java`

5. **Rodar `./mvnw test`** — todos os testes de integração devem passar sem modificação

6. **Criar novos testes unitários** (Step 04) para cada tipo — **JUnit puro, sem Spring**

7. **Commit com Conventional Commits** ao longo do processo

8. **Limpeza final:** remover imports não utilizados conforme `JAVA25_CONVENTIONS.md`
