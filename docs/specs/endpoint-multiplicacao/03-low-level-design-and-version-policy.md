# 03 — Low-Level Design and Version Policy

## API Contracts

```
POST /calculator/multiply
Content-Type: application/json
Body: {"multiplicand": double, "multiplier": double}
Response 200: {"result": double}
Response 400: {"error": "Bad Request", "message": "Invalid request body"}
```

## Data Models

- `MultiplyRequest` (record): `multiplicand`, `multiplier` as double
- `MultiplyResponse` (record): `result` as double

## Error Model

- Same `ApiErrorHandler` pattern from existing endpoints
- Validation: `@NotNull` on both fields

## Test Strategy

- Unit tests in `CalculatorServiceMultiplyTest` (JUnit pure)
- Integration tests in `CalculatorMultiplyControllerTest` (MockMvcTester)

## Test Scenario Catalog

| Scenario | Expected |
|----------|----------|
| 3.0 * 2.0 | 6.0 |
| 1.5 * 2.5 | 3.75 |
| 0.1 * 0.2 | 0.02 (with delta) |
| NaN * 2.0 | 400 |
| Infinity * 2.0 | 400 |
| missing field | 400 |
| extra field ignored | 200 |

## Dependency and Version Policy

- Spring Boot 4.0.5, Java 25 (existing)
- No new dependencies

## Ordered Implementation Plan

1. Create `MultiplyRequest.java`
2. Create `MultiplyResponse.java`
3. Add `multiply()` to `CalculatorService.java`
4. Add endpoint to `CalculatorController.java`
5. Write unit tests
6. Write integration tests