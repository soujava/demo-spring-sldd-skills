# Test Files Created
- src/test/java/com/example/demo/domain/CalculatorServiceTest.java (Add expression tests)
- src/test/java/com/example/demo/domain/CalculatorServiceSubtractionTest.java
- src/test/java/com/example/demo/domain/CalculatorServiceMultiplyTest.java
- src/test/java/com/example/demo/domain/CalculatorServiceDivideTest.java
- src/test/java/com/example/demo/domain/CalculatorServicePowerTest.java
- src/test/java/com/example/demo/domain/CalculatorServiceRootTest.java

# Acceptance Criteria -> Tests Mapping
From Step 01 (01-product-intent-specification.md):
- Given valid numeric payloads to arithmetic endpoints -> response 200 with correct result.
  - Covered by tests for each operation (add, subtract, multiply, divide, power, root) with typical values.
- Given invalid input (e.g., division by zero) -> response 500 with appropriate error.
  - Covered by tests that assert exceptions are thrown (e.g., divideByZero, throwsOnZeroIndex, etc.).
- Given valid expression string to /compose -> 200 with correct result.
  - Not directly unit tested here; covered by integration tests (unchanged) and will be verified in later steps.
- Given invalid expression string to /compose -> 400 Bad Request.
  - Not directly unit tested here; covered by integration tests.

# Test Commands Executed
- ./mvnw test

# Failing Results Summary
Compilation fails because the Expression interface and its factories (literal, add, etc.) and the operation records (Add, Subtract, etc.) are not implemented.
All test files fail to compile with "cannot find symbol" errors for Expression and its static methods.

# Red-Phase Confirmation
✅ The tests are failing (Red phase) as expected. No production code for the new expression model exists yet.