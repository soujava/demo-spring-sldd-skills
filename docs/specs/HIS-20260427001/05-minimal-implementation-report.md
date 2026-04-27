# Minimal Implementation Report

## Production Files Changed

- `src/main/java/com/example/demo/controller/api/ComposeResponse.java`
- `src/main/java/com/example/demo/controller/validation/InvalidExpressionPayloadException.java`
- `src/main/java/com/example/demo/controller/validation/ComposeExpressionValidator.java`
- `src/main/java/com/example/demo/controller/CalculatorController.java`
- `src/main/java/com/example/demo/controller/ApiErrorHandler.java`
- `src/main/java/com/example/demo/domain/expression/Expression.java`
- `src/main/java/com/example/demo/domain/expression/ExpressionLiteral.java`
- `src/main/java/com/example/demo/domain/expression/ExpressionNode.java`
- `src/main/java/com/example/demo/domain/expression/ExpressionOperation.java`
- `src/main/java/com/example/demo/domain/CalculatorService.java`

## Implementation Notes (Minimal Scope)

- Added `POST /calculator/compose` to the existing calculator controller namespace.
- Kept the request boundary simple by accepting the raw JSON body as `String`.
- Parsed and validated the composed-expression payload explicitly with a small recursive validator.
- Rejected malformed JSON, missing required fields, unknown operations, and extra properties with `InvalidExpressionPayloadException`.
- Modeled expressions with a sealed domain hierarchy and evaluated them recursively in `CalculatorService`.
- Reused the existing `ErrorResponse` contract and `ApiErrorHandler` for invalid payloads and arithmetic failures.
- Preserved the current arithmetic semantics for divide-by-zero and numeric overflow.

## Test Commands Executed

- `./mvnw -Dtest=CalculatorComposeControllerTest test`
- `./mvnw test`

## Passing Results Summary

- `CalculatorComposeControllerTest`: 8 tests, 0 failures, 0 errors, 0 skipped
- Full project suite: 112 tests, 0 failures, 0 errors, 0 skipped
- Build result: `BUILD SUCCESS`

## Assumptions and Constraints

- I assumed the feature should stay within the current dependency set and avoid a new JSON Schema library.
- I used explicit recursive validation to keep the implementation small and maintainable.
- I preserved the existing API error shape rather than introducing a new response contract.
- I did not modify any test files.

## Test Integrity Confirmation (No Test Modifications)

- No existing test was changed.
- The Step 04 Red-phase tests remain intact and now pass against the new implementation.
