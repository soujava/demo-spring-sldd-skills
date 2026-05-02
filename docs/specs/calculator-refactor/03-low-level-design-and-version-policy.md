# Step 03 — Low-Level Design and Version Policy: Calculator Operations Refactor

## Requirement-to-Design Traceability

| Step 01 AC | Step 02 Design | Contrato/Modelo | Teste |
|---|---|---|---|
| AC1 — subtract BigDecimal | `SubtractOperation` | `SubtractOperation.apply()` | `CalculatorServiceSubtractionTest` regressão |
| AC2 — divide default | `DivideOperation` default `10, HALF_UP` | `DivideOperation.apply(left, right)` sem ctx | `CalculatorServiceDivideTest` + novo |
| AC3 — divide com scale explícito | `DivideOperation.apply(left, right, ctx)` | `DivideRequest.scale`, `DivideRequest.roundingMode` | Novo teste integração |
| AC4 — divisão por zero → 400 | `DivideOperation` valida divisor | `ArithmeticException` → `ApiErrorHandler` | `CalculatorServiceDivideTest` existente |
| AC5 — evaluate divide sem ctx | `BinaryOperation.context` opcional | `BinaryOperationDto.context` opcional | Novo teste evaluate |
| AC6 — regressão zero | Demais Operations encapsulam lógica | Sem contrato novo | Testes unitários existentes |
| AC7 — nova operação sem modificar service | `Operation` interface + `OperationRegistry` | `Operation.apply(double, double)` | Teste de aceitação de extensibilidade |

---

## API Contracts

### POST /calculator/subtract

Request:
```json
{ "minuend": 10.0, "subtrahend": 3.3 }
```

Response (200):
```json
{ "result": 6.7 }
```

### POST /calculator/divide (sem scale)

Request:
```json
{ "dividend": 1, "divisor": 3 }
```

Response (200):
```json
{ "result": 0.3333333333 }
```

### POST /calculator/divide (com scale explícito)

Request:
```json
{ "dividend": 1, "divisor": 3, "scale": 4, "roundingMode": "HALF_DOWN" }
```

Response (200):
```json
{ "result": 0.3333 }
```

Erro (400) — divisão por zero:
```json
{ "error": "Bad Request", "message": "Division by zero" }
```

### POST /calculator/evaluate (com DIVIDE + context)

Request:
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

Response (200):
```json
{ "result": 0.3333 }
```

### POST /calculator/evaluate (com DIVIDE sem context)

Request:
```json
{
  "expression": {
    "type": "operation",
    "operator": "DIVIDE",
    "left": { "type": "literal", "value": 1 },
    "right": { "type": "literal", "value": 3 }
  }
}
```

Response (200):
```json
{ "result": 0.3333333333 }
```

---

## Data Models

### `CalculationContext` (novo record — pacote `domain`)

```java
public record CalculationContext(int scale, RoundingMode roundingMode) {
    public static final CalculationContext DEFAULT = new CalculationContext(10, RoundingMode.HALF_UP);
}
```

### `BinaryOperation` (alterado — pacote `domain`)

```java
public record BinaryOperation(Operator operator, Expression left, Expression right, CalculationContext context)
        implements Expression {

    public BinaryOperation(Operator operator, Expression left, Expression right) {
        this(operator, left, right, null);
    }
}
```

### `DivideRequest` (alterado — pacote `controller.api`)

```java
public record DivideRequest(double dividend, double divisor,
                            @Nullable Integer scale,
                            @Nullable String roundingMode) {
}
```

### `BinaryOperationDto` (alterado — pacote `controller.api`)

```java
public record BinaryOperationDto(
    @NotNull OperatorDto operator,
    @NotNull ExpressionDto left,
    @NotNull ExpressionDto right,
    @Nullable CalculationContextDto context
) implements ExpressionDto {}
```

### `CalculationContextDto` (novo record — pacote `controller.api`)

```java
public record CalculationContextDto(int scale, @NotNull String roundingMode) {}
```

### `OperatorDto` (inalterado — pacote `controller.api`)

```java
public enum OperatorDto {
    SUM, SUBTRACT, MULTIPLY, DIVIDE, POWER, ROOT
}
```

---

## Error Model

| Condição | Exceção | HTTP Status | Response |
|---|---|---|---|
| Divisão por zero | `ArithmeticException` | 400 | `ErrorResponse("Bad Request", "Division by zero")` |
| Overflow numérico | `NumericOverflowException` | 422 | `ErrorResponse("Unprocessable Entity", "...")` |
| Operação inválida (NaN, índice zero) | `ArithmeticException` | 400 | `ErrorResponse("Bad Request", "...")` |
| JSON malformado | `HttpMessageNotReadableException` | 400 | `ErrorResponse("Bad Request", "...")` |
| Campo obrigatório ausente | `MethodArgumentNotValidException` | 400 | `ErrorResponse("Bad Request", "...")` |

Nenhuma mudança no `ApiErrorHandler` existente.

---

## Test Strategy

**Camada de domínio (unitários — JUnit 5 puro, sem Spring):**
- Cada `Operation` implementada tem sua própria classe de teste
- `DivideOperationTest` cobre: default scale, scale explícito, roundingMode explícito, divisão por zero
- `SubtractOperationTest` cobre: precisão BigDecimal (regressão)
- `OperationRegistryTest` cobre: lookup por Operator, registro de beans
- `CalculatorServiceEvaluateTest` cobre: evaluate com DIVIDE com/sem context

**Camada de borda (integração — @SpringBootTest + MockMvcTester):**
- `CalculatorDivideControllerTest` ganha cenários com scale/roundingMode no request
- `CalculatorEvaluateControllerTest` ganha cenário com context no nó DIVIDE
- `CalculatorSubtractControllerTest` mantém cenários existentes (regressão)

---

## Test Scenario Catalog

### Unitários

| Classe de Teste | Cenário | Expected |
|---|---|---|
| `DivideOperationTest` | `apply(1, 3)` | `0.3333333333` |
| `DivideOperationTest` | `apply(1, 3, 4, HALF_DOWN)` | `0.3333` |
| `DivideOperationTest` | `apply(1, 0)` | `ArithmeticException` |
| `DivideOperationTest` | `apply(1, 3, 0, HALF_UP)` | `0.0` |
| `SubtractOperationTest` | `apply(10, 3.3)` | `6.7` |
| `SubtractOperationTest` | `apply(0.1, 0.2)` | `-0.1` |
| `OperationRegistryTest` | registry contém todas 6 operações | `true` |
| `OperationRegistryTest` | `lookup(DIVIDE)` | `DivideOperation` |

### Integração

| Classe de Teste | Cenário | Expected |
|---|---|---|
| `CalculatorDivideControllerTest` | `POST /calculator/divide { dividend:1, divisor:3 }` | 200 + `0.3333333333` |
| `CalculatorDivideControllerTest` | `POST /calculator/divide { ..., scale:4, roundingMode:"HALF_DOWN" }` | 200 + `0.3333` |
| `CalculatorDivideControllerTest` | `POST /calculator/divide { dividend:1, divisor:0 }` | 400 |
| `CalculatorSubtractControllerTest` | `POST /calculator/subtract { minuend:10, subtrahend:3.3 }` | 200 + `6.7` |
| `CalculatorEvaluateControllerTest` | `POST /calculator/evaluate` com DIVIDE + context | 200 + arredondado |
| `CalculatorEvaluateControllerTest` | `POST /calculator/evaluate` com DIVIDE sem context | 200 + default |

---

## Dependency and Version Policy

- **Novas dependências**: nenhuma. O projeto já tem `spring-boot-starter-web`, `spring-boot-starter-validation`, Jackson, e JUnit 5.
- `BigDecimal`, `RoundingMode`, `Math` são da JDK (Java 25).
- Impacto zero em runtime behavior, testes, e manutenção.

---

## Ordered Implementation Plan

1. **Criar `CalculationContext` record** no pacote `domain`
2. **Criar `Operation` interface** no pacote `domain`
3. **Criar** `SumOperation`, `SubtractOperation`, `MultiplyOperation`, `DivideOperation`, `PowerOperation`, `RootOperation` — cada uma implementando `Operation`
4. **Criar `OperationRegistry`** — `@Component` que reúne os beans via injeção
5. **Alterar `BinaryOperation`** — adicionar campo `CalculationContext context` opcional + construtor sem ctx
6. **Alterar `CalculatorService`** — delegar para `OperationRegistry`; `evaluate()` passar ctx para DIVIDE
7. **Criar `CalculationContextDto`** no pacote `controller.api`
8. **Alterar `BinaryOperationDto`** — adicionar campo `CalculationContextDto context` opcional
9. **Alterar `DivideRequest`** — adicionar `scale` e `roundingMode` opcionais
10. **Alterar `CalculatorController`** — mapear request → ctx; mapear DTO → domínio
11. **Escrever testes unitários**: `DivideOperationTest`, `SubtractOperationTest`, `OperationRegistryTest`
12. **Escrever testes de integração**: novos cenários
13. **Executar bateria completa de testes** — validar regressão zero
