# Tests First Report

## Test Files Created

- `src/test/java/com/example/demo/controller/CalculatorComposeControllerTest.java`

## Acceptance Criteria -> Tests Mapping

- `10 + (2 * 3)` returns `16`
  - `returnsResultForNestedMultiplyExpression`
- `(10 + 2) * 3` returns `36`
  - `returnsResultForNestedAddExpression`
- root missing `operation` returns `400`
  - `returnsBadRequestWhenRootOperationIsMissing`
- internal node missing `left` or `right` returns `400`
  - `returnsBadRequestWhenInternalNodeIsIncomplete`
- unknown `operation` returns `400`
  - `returnsBadRequestWhenOperationIsUnknown`
- division by zero returns `400`
  - `returnsBadRequestWhenDivisionByZeroOccurs`
- malformed JSON returns `400`
  - `returnsBadRequestWhenJsonIsMalformed`
- extra JSON properties are rejected
  - `returnsBadRequestWhenPayloadHasExtraFields`

## Test Commands Executed

- `./mvnw -Dtest=CalculatorComposeControllerTest test`

## Failing Results Summary

- 8 tests ran
- 2 tests failed
- 0 errors
- 0 skipped
- The two success-path tests failed because `/calculator/compose` is not implemented yet.
- Spring resolved the request as a static resource lookup and returned `404 No static resource calculator/compose`.

## Red-Phase Confirmation

- The Step 04 suite is red for the expected reason.
- No production logic was added.
- The failing output confirms the endpoint still needs implementation in Step 05.
