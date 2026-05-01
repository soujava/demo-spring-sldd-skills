# 05 — Minimal Implementation Report: Uniformizar Precisão BigDecimal

## Production Files Changed

| # | File | Change | AC |
|---|------|--------|-----|
| 1 | `src/main/java/com/example/demo/domain/expression/ExpressionContext.java` | **CREATED** — record `(int scale, RoundingMode roundingMode)` with `DEFAULT` constant and compact constructor validation | AC6, AC7 |
| 2 | `src/main/java/com/example/demo/domain/expression/ExpressionNode.java` | **UPDATED** — added `ExpressionContext context` as 4th field | AC6, AC7 |
| 3 | `src/main/java/com/example/demo/domain/CalculatorService.java` | **UPDATED** — `subtract()` migrated to BigDecimal; `divide()` signature changed to `(double, double, int, RoundingMode)` with validation; `evaluate()` dispatches `node.context()` on DIVIDE | AC1, AC2, AC3, AC6, AC7 |
| 4 | `src/main/java/com/example/demo/controller/api/DivideRequest.java` | **UPDATED** — added `Integer scale` and `String roundingMode` optional fields | AC4, AC5 |
| 5 | `src/main/java/com/example/demo/controller/CalculatorController.java` | **UPDATED** — `divide()` reads optional scale/roundingMode, applies defaults (10, HALF_UP), calls new service signature | AC4, AC5 |
| 6 | `src/main/java/com/example/demo/controller/ApiErrorHandler.java` | **UPDATED** — added `@ExceptionHandler(IllegalArgumentException.class)` returning 400 | — |
| 7 | `src/main/java/com/example/demo/controller/validation/ComposeExpressionValidator.java` | **UPDATED** — added `"context"` to `ALLOWED_FIELDS`; added `parseContext()`, `validateContextFields()`, `parseRoundingMode()`; constructs `ExpressionNode` with context | AC6, AC7 |

## Implementation Notes (Minimal Scope)

- `subtract()` migrated from `double` arithmetic to `BigDecimal.valueOf()` pattern, matching existing `sum()` and `multiply()`
- `divide()` signature expanded with `int scale` and `RoundingMode roundingMode` parameters; validation rejects scale outside 1–100 and null roundingMode
- `evaluate()` dispatches `node.context().scale()` and `node.context().roundingMode()` only on DIVIDE operation
- `DivideRequest` backward-compatible: `scale` and `roundingMode` are nullable `Integer`/`String`; null values trigger defaults in controller
- `ComposeExpressionValidator.parseContext()` returns `ExpressionContext.DEFAULT` when context is absent or null
- `ApiErrorHandler` handles `IllegalArgumentException` from both `CalculatorService.divide()` and `ExpressionContext` compact constructor

## Test Commands Executed

```bash
./mvnw test
```

## Passing Results Summary

```
Tests run: 142, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```

Breakdown by test class:

| Test Class | Tests | Result |
|------------|-------|--------|
| CalculatorService (sum) | 8 | PASS |
| ExpressionContext | 6 | PASS |
| CalculatorService.divide() | 12 | PASS |
| CalculatorService.evaluate() | 7 | PASS |
| CalculatorServicePowerTest | 3 | PASS |
| CalculatorService root | 5 | PASS |
| CalculatorService multiplicacao | 8 | PASS |
| CalculatorService subtracao | 7 | PASS |
| DemoApplicationTests | 1 | PASS |
| POST /calculator/compose | 13 | PASS |
| POST /calculator/divide | 19 | PASS |
| CalculatorPowerControllerTest | 3 | PASS |
| OpenAPI documentation | 3 | PASS |
| POST /calculator/root | 7 | PASS |
| POST /calculator/multiply | 13 | PASS |
| POST /calculator/subtract | 13 | PASS |
| POST /calculator/sum | 14 | PASS |

## Assumptions and Constraints

- No new dependencies introduced; all changes use existing JDK 25 and Spring Boot infrastructure
- `Expression.java` sealed interface did not require modification since `ExpressionNode` already implemented `Expression` and only added a record component
- `ExpressionContext` uses `RoundingMode` from `java.math` (JDK standard) — no custom enum created

## Test Integrity Confirmation

**No test files were modified during Step 05.** All test files remain exactly as written in Step 04. The transition from Red (32 compilation errors) to Green (142 tests passing) was achieved solely through production code changes.