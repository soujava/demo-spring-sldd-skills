# 06 — Verification and Feedback Report: Uniformizar Precisão BigDecimal

## Compliance Matrix

| AC | Requirement | Implementation | Test Coverage | Status |
|----|-------------|----------------|---------------|--------|
| AC1 | `subtract()` with BigDecimal precision | `CalculatorService.subtract()` uses `BigDecimal.valueOf()` | `CalculatorServiceSubtractionTest.subtractWithBigDecimalPrecision` → `0.3 - 0.1 = 0.2` | ✅ PASS |
| AC2 | `divide()` with defaults (scale=10, HALF_UP) | `CalculatorService.divide(d,d,10,HALF_UP)` via controller defaults | `CalculatorServiceDivideTest.returnsNonTerminatingDecimalWithDefaults`, `returnsNonTerminatingFractionWithDefaults` + 6 updated existing tests | ✅ PASS |
| AC3 | `divide()` with explicit scale/rounding | `CalculatorService.divide(d,d,s,rm)` with validation | `CalculatorServiceDivideTest.returnsNonTerminatingDecimalWithExplicitScale` (scale=5) | ✅ PASS |
| AC4 | `/divide` endpoint with optional params | `DivideRequest` + controller reads `scale`/`roundingMode` | `CalculatorDivideControllerTest.returnsDivideWithExplicitScaleAndRoundingMode` | ✅ PASS |
| AC5 | `/divide` backward-compatible | Null `scale`/`roundingMode` → defaults (10, HALF_UP) | `CalculatorDivideControllerTest.returnsDivideWithDefaultScaleAndRoundingMode` | ✅ PASS |
| AC6 | `ExpressionContext` in compose | `ComposeExpressionValidator.parseContext()` + `ExpressionNode.context` | `CalculatorComposeControllerTest.returnsDivideWithExplicitContext`, `returnsNestedDivideWithContext` | ✅ PASS |
| AC7 | Compose without context uses defaults | `parseContext(null)` → `ExpressionContext.DEFAULT` | `CalculatorComposeControllerTest.returnsDivideWithoutContext`, `CalculatorServiceEvaluateTest.evaluateDivideWithDefaultContext` | ✅ PASS |
| AC8 | Existing tests adapted | All existing divide/subtract tests updated for new signatures | 142 tests pass, 0 failures | ✅ PASS |

## Version and Dependency Validation

| Dependency | Version | Status | Notes |
|------------|---------|--------|-------|
| `java.math.BigDecimal` | JDK 25 | Already present | Used in `subtract()`, `divide()` |
| `java.math.RoundingMode` | JDK 25 | Already present | Used in `ExpressionContext`, `divide()` |
| Spring Boot | 4.0.5 | No change | — |
| Jakarta Validation | via starter | No change | — |
| Jackson | via starter | No change | — |
| JUnit 5 | via starter | No change | — |
| AssertJ | via starter | No change | — |

**No new dependencies introduced.**

## Test Convention Compliance

| Convention | Status |
|------------|--------|
| Domain tests: JUnit 5 pure, no Spring context | ✅ `CalculatorServiceDivideTest`, `CalculatorServiceSubtractionTest`, `CalculatorServiceEvaluateTest`, `ExpressionContextTest` |
| Controller tests: `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvcTester` + AssertJ | ✅ `CalculatorDivideControllerTest`, `CalculatorComposeControllerTest` |
| DTOs are records | ✅ `DivideRequest` updated as record |
| `ALLOWED_FIELDS` strict validation in compose | ✅ `"context"` added to set |
| Backward-compatible optional fields | ✅ `Integer scale`, `String roundingMode` nullable |

## Risks by Severity

| Risk | Severity | Mitigation |
|------|----------|------------|
| Breaking change: `divide()` now returns rounded result instead of `ArithmeticException` for non-terminating decimals | Medium | Defaults (scale=10, HALF_UP) are reasonable; documented in Step 01 |
| `ExpressionNode` constructor changed from 3 to 4 args | Low | All usages updated in production and tests |
| `IllegalArgumentException` handler may catch unintended exceptions | Low | Error messages are specific; `ExpressionContext` validation messages are precise |

## Remediation Steps

No remediation required. All ACs pass.

## Go/No-Go Decision and Rationale

**GO** ✅

All 8 acceptance criteria are met. 142 tests pass with 0 failures. No new dependencies introduced. Test conventions are respected. No risks requiring remediation.