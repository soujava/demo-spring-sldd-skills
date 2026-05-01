# 04 — Tests First Report: Uniformizar Precisão BigDecimal

## Test Files Created

| # | File | Action | Tests |
|---|------|--------|-------|
| 1 | `src/test/java/com/example/demo/domain/CalculatorServiceSubtractionTest.java` | UPDATED | +1 (BigDecimal precision) — total 7 |
| 2 | `src/test/java/com/example/demo/domain/CalculatorServiceDivideTest.java` | UPDATED | 6 updated + 6 new = 12 |
| 3 | `src/test/java/com/example/demo/domain/CalculatorServiceEvaluateTest.java` | CREATED | 7 new |
| 4 | `src/test/java/com/example/demo/domain/expression/ExpressionContextTest.java` | CREATED | 6 new |
| 5 | `src/test/java/com/example/demo/controller/CalculatorDivideControllerTest.java` | UPDATED | 14 existing + 5 new = 19 |
| 6 | `src/test/java/com/example/demo/controller/CalculatorComposeControllerTest.java` | UPDATED | 8 existing + 5 new = 13 |

## Acceptance Criteria → Tests Mapping

| AC | Test File | Test Method | Type |
|----|-----------|-------------|------|
| AC1 — Subtract com BigDecimal | CalculatorServiceSubtractionTest | `subtractWithBigDecimalPrecision` | Unitário |
| AC2 — Divide com defaults | CalculatorServiceDivideTest | `returnsNonTerminatingDecimalWithDefaults` | Unitário |
| AC2 — Divide com defaults | CalculatorServiceDivideTest | `returnsNonTerminatingFractionWithDefaults` | Unitário |
| AC2 — Divide com defaults | CalculatorServiceDivideTest | 6 existing tests updated to `divide(d,d,s,rm)` | Unitário |
| AC3 — Divide com scale/rounding explícitos | CalculatorServiceDivideTest | `returnsNonTerminatingDecimalWithExplicitScale` | Unitário |
| AC4 — Endpoint /divide com params opcionais | CalculatorDivideControllerTest | `returnsDivideWithExplicitScaleAndRoundingMode` | Integração |
| AC5 — Endpoint /divide backward-compatible | CalculatorDivideControllerTest | `returnsDivideWithDefaultScaleAndRoundingMode` | Integração |
| AC6 — ExpressionContext no compose | CalculatorComposeControllerTest | `returnsDivideWithExplicitContext` | Integração |
| AC6 — ExpressionContext no compose | CalculatorComposeControllerTest | `returnsNestedDivideWithContext` | Integração |
| AC7 — Compose sem context usa defaults | CalculatorComposeControllerTest | `returnsDivideWithoutContext` | Integração |
| AC7 — Compose sem context usa defaults | CalculatorServiceEvaluateTest | `evaluateDivideWithDefaultContext` | Unitário |
| AC8 — Testes adaptados | All files | All existing tests updated to new signatures | Ambos |
| — Edge: scale=0 | CalculatorServiceDivideTest | `throwsIllegalArgumentExceptionWhenScaleIsZero` | Unitário |
| — Edge: scale negativo | CalculatorServiceDivideTest | `throwsIllegalArgumentExceptionWhenScaleIsNegative` | Unitário |
| — Edge: roundingMode null | CalculatorServiceDivideTest | `throwsIllegalArgumentExceptionWhenRoundingModeIsNull` | Unitário |
| — Edge: roundingMode inválido no endpoint | CalculatorDivideControllerTest | `returnsBadRequestWhenRoundingModeIsInvalid` | Integração |
| — Edge: scale=0 no endpoint | CalculatorDivideControllerTest | `returnsBadRequestWhenScaleIsZero` | Integração |
| — Edge: scale negativo no endpoint | CalculatorDivideControllerTest | `returnsBadRequestWhenScaleIsNegative` | Integração |
| — Edge: context com roundingMode inválido | CalculatorComposeControllerTest | `returnsBadRequestWhenContextHasInvalidRoundingMode` | Integração |
| — Edge: context com campos extras | CalculatorComposeControllerTest | `returnsBadRequestWhenContextHasExtraFields` | Integração |
| — ExpressionContext validation | ExpressionContextTest | 6 tests (DEFAULT, valid, scale=0, scale<0, scale>100, null roundingMode) | Unitário |

## Test Commands Executed

```bash
./mvnw test
```

## Failing Results Summary

Build failed with **32 compilation errors** — all caused by missing production code:

- **13 errors**: `ExpressionContext` class not found (CalculatorServiceEvaluateTest, ExpressionContextTest)
- **12 errors**: `CalculatorService.divide(double,double,int,RoundingMode)` signature mismatch (CalculatorServiceDivideTest)
- **7 errors**: `ExpressionNode` 4-arg constructor with `ExpressionContext` not found (CalculatorServiceEvaluateTest)

All errors are expected Red-phase outcomes. No production code has been written.

### Key Compilation Errors

```
[ERROR] cannot find symbol: class ExpressionContext
  location: package com.example.demo.domain.expression

[ERROR] method divide in class CalculatorService cannot be applied to given types;
  required: double,double
  found: double,double,int,java.math.RoundingMode
  reason: actual and formal argument lists differ in length

[ERROR] cannot find symbol: constructor ExpressionNode(ExpressionOperation,ExpressionLiteral,ExpressionLiteral,ExpressionContext)
```

## Red-Phase Confirmation

**All new and updated tests fail.** No production code has been modified. The tests correctly encode the implementation contracts from Step 03:

1. `ExpressionContext` record must be created with `DEFAULT` constant and compact constructor validation
2. `CalculatorService.divide()` must accept `(double, double, int scale, RoundingMode roundingMode)`
3. `ExpressionNode` must accept a 4th `context` parameter of type `ExpressionContext`
4. `DivideRequest` must include optional `Integer scale` and `String roundingMode`
5. `CalculatorController.divide()` must read optional scale/roundingMode and apply defaults
6. `ComposeExpressionValidator` must parse `context` field and add it to `ALLOWED_FIELDS`
7. `ApiErrorHandler` must handle `IllegalArgumentException`