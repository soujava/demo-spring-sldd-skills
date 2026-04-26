# 04 — Tests First Report (Red phase, failing tests required)

## Test Files Created

- `src/test/java/com/example/demo/domain/CalculatorServiceDivideTest.java` (unit tests)
- `src/test/java/com/example/demo/controller/CalculatorDivideControllerTest.java` (integration tests)

## Acceptance Criteria -> Tests Mapping

| Acceptance Criterion | Test Method |
|---------------------|-------------|
| Valid division returns 200 with result | `returnsDivideForValidPayload`, `returnsDivideForDecimalPayload`, `returnsDivideWhenDividendIsZero`, `returnsDivideWhenResultIsNegative` |
| Division by zero returns 400 | `returnsBadRequestWhenDivisorIsZero`, `throwsArithmeticExceptionWhenDivisorIsZero` |
| Missing field returns 400 | `returnsBadRequestWhenDivisorIsMissing`, `returnsBadRequestWhenDividendIsMissing` |
| Invalid JSON returns 400 | `returnsBadRequestWhenJsonIsMalformed`, `returnsBadRequestWhenBodyIsEmpty` |
| NaN returns 400 | `rejectsNaNAsUnsupported` |
| Infinity returns 400 | `rejectsInfinityAsUnsupported`, `rejectsNegativeInfinityAsUnsupported` |
| Extra fields ignored | `ignoresExtraFieldsInPayload` |

## Test Commands Executed

```bash
./mvnw test -Dtest=CalculatorServiceDivideTest,CalculatorDivideControllerTest
```

## Failing Results Summary

Compilation errors (12 errors):
- `DivideResponse` class not found in `com.example.demo.controller.api`
- `CalculatorService.divide()` method not found (6 occurrences)
- Test compilation failed due to missing API models and service method

## Red-Phase Confirmation

Tests fail to compile because the following do not exist:
- `DivideRequest` (record)
- `DivideResponse` (record)
- `CalculatorService.divide()` method

This confirms Red phase: no production code exists for the divide endpoint.