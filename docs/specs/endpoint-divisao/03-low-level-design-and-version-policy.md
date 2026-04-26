# 03 — Low-Level Design and Version Policy

## API Contracts

```
POST /calculator/divide
Content-Type: application/json
Body: {"dividend": double, "divisor": double}
Response 200: {"result": double}
Response 400: {"error": "Bad Request", "message": "Invalid request body"}
Response 400: {"error": "Bad Request", "message": "Division by zero"} (when divisor is 0)
```

## Data Models

- `DivideRequest` (record): `dividend`, `divisor` as double with `@NotNull`
- `DivideResponse` (record): `result` as double

## Error Model

- Validation: `@NotNull` on both fields
- Division by zero: `CalculatorService.divide()` throws `ArithmeticException`
- `ApiErrorHandler` handles `ArithmeticException` returning 400

## Test Strategy

- Unit tests in `CalculatorServiceDivideTest` (JUnit pure)
- Integration tests in `CalculatorDivideControllerTest` (MockMvcTester)

## Test Scenario Catalog

| Scenario | Expected |
|----------|----------|
| 6.0 / 2.0 | 3.0 |
| 7.5 / 2.5 | 3.0 |
| 0.0 / 5.0 | 0.0 |
| -10.0 / 2.0 | -5.0 |
| 10.0 / -2.0 | -5.0 |
| 1.0 / 0.0 | 400 (division by zero) |
| NaN / 2.0 | 400 |
| 2.0 / NaN | 400 |
| Infinity / 2.0 | 400 |
| 2.0 / Infinity | 400 |
| missing field | 400 |
| extra field ignored | 200 |

## Dependency and Version Policy

- Spring Boot 4.0.5, Java 25 (existing)
- No new dependencies

## Ordered Implementation Plan

1. Create `DivideRequest.java`
2. Create `DivideResponse.java`
3. Add `ArithmeticException` handler to `ApiErrorHandler.java`
4. Add `divide()` to `CalculatorService.java`
5. Add endpoint to `CalculatorController.java`
6. Write unit tests
7. Write integration tests