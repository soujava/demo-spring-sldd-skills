# Low-Level Design and Version Policy - HIS-202604270003

## API Contracts
* **Endpoint:** `POST /calculator/evaluate`
* **Request Body (`EvaluateRequest`):**
    ```json
    {
      "expression": {
        "type": "operation",
        "operator": "MULTIPLY",
        "left": { "type": "literal", "value": 10 },
        "right": { "type": "literal", "value": 5 }
      }
    }
    ```
* **Response Body (`EvaluateResponse`):**
    ```json
    { "result": 50.0 }
    ```

## Data Models
* **DTOs (`com.example.demo.controller.api`):**
    * `sealed interface ExpressionDto` com `@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")` e `@JsonSubTypes`.
    * `record LiteralDto(Double value) implements ExpressionDto`.
    * `record BinaryOperationDto(OperatorDto operator, ExpressionDto left, ExpressionDto right) implements ExpressionDto`.
    * `enum OperatorDto { SUM, SUBTRACT, MULTIPLY, DIVIDE, POWER, ROOT }`.
* **Domínio (`com.example.demo.domain`):**
    * `sealed interface Expression permits Literal, BinaryOperation`.
    * `record Literal(double value) implements Expression`.
    * `record BinaryOperation(Operator operator, Expression left, Expression right) implements Expression`.

## Error Model
* Uso do `ApiErrorHandler` existente para retornar `400 Bad Request` em caso de:
    * `ArithmeticException` (ex: divisão por zero).
    * `NumericOverflowException` (ex: resultado infinito).
    * `MethodArgumentNotValidException` (ex: JSON malformado ou campos nulos).
    * `IllegalArgumentException` (ex: árvore muito profunda).

## Test Strategy
* **TDD (Red Phase):** Criar `CalculatorEvaluateControllerTest` e `CalculatorServiceEvaluateTest` com cenários de falha antes da implementação.
* **Cenários:** Operações simples, aninhamento triplo, divisão por zero profunda, e limite de profundidade.

## Test Scenario Catalog
1. `evaluate_SimpleSum_ReturnsCorrectResult`
2. `evaluate_NestedOperations_RespectsOrder`
3. `evaluate_DivisionByZero_ThrowsException`
4. `evaluate_DeepTree_RespectsLimit`

## Dependency and Version Policy
* **Dependências Atuais:** Suficientes. O suporte a JSON polimórfico e Bean Validation já está incluso no `spring-boot-starter-web` e `spring-boot-starter-validation`. As features de Java 25 não exigem bibliotecas externas extras.
* **Impacto:** Zero novas dependências.

## Ordered Implementation Plan
1. Criar Enums e DTOs polimórficos (`ExpressionDto`).
2. Implementar `Expression` no domínio com logicade avaliação baseada em Java 25.
3. Adicionar lógica de avaliação no `CalculatorService` reaproveitando operações existentes.
4. Implementar mapeamento DTO -> Domínio com validação de profundidade.
5. Adicionar endpoint no `CalculatorController`.
