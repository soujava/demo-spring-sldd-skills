# 04 — Tests First Report (Red Phase)

## Test Files Created

- `CalculatorServiceMultiplyTest.java` (8 unit tests)
- `CalculatorMultiplyControllerTest.java` (13 integration tests)

## Acceptance Criteria -> Tests Mapping

| Criterion | Test |
|-----------|------|
| Valid payload returns 200 with result | `returnsMultiplyForValidPayload`, `returnsMultiplyForDecimalPayload` |
| Zero handling | `returnsMultiplyWhenMultiplierIsZero` |
| Negative results | `returnsMultiplyWhenResultIsNegative` |
| Missing field returns 400 | `returnsBadRequestWhenMultiplicandIsMissing`, `returnsBadRequestWhenMultiplierIsMissing` |
| Invalid type returns 400 | `returnsBadRequestWhenMultiplicandIsNotNumeric` |
| Malformed JSON returns 400 | `returnsBadRequestWhenJsonIsMalformed`, `returnsBadRequestWhenBodyIsEmpty` |
| NaN/Infinity rejected | `rejectsNaNAsUnsupported`, `rejectsInfinityAsUnsupported`, `rejectsNegativeInfinityAsUnsupported` |
| Extra fields ignored | `ignoresExtraFieldsInPayload` |

## Test Commands Executed

```bash
./mvnw test -Dtest="CalculatorServiceMultiplyTest,CalculatorMultiplyControllerTest"
```

## Failing Results Summary

Compilation errors — `multiply(double,double)` method not found in `CalculatorService`:
```
cannot find symbol: method multiply(double,double)
location: variable service of type com.example.demo.domain.CalculatorService
```

8 errors in `CalculatorServiceMultiplyTest.java`

## Red-Phase Confirmation

Tests fail at compile time due to missing `CalculatorService.multiply()` method.
This confirms the Red phase requirement: no production logic exists yet.