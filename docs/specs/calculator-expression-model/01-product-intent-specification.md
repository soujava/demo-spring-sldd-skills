# Problem Statement
The current calculator implementation spreads arithmetic logic across multiple methods in `CalculatorService` and uses an enum-based expression node (`ExpressionOperation`) to represent operations in composed expressions. This design makes it difficult to add new operations without modifying existing code and scatters the evaluation logic, reducing cohesion and increasing the risk of inconsistencies.

# Target Users
- Developers maintaining and extending the calculator functionality.
- Users of the calculator API who expect reliable arithmetic operations.

# Success Metrics
- All existing API endpoints (`/sum`, `/subtract`, `/multiply`, `/divide`, `/power`, `/root`, `/compose`) continue to behave exactly as before (no breaking changes).
- Unit tests for the new expression model cover all arithmetic operations and error conditions.
- Integration tests for the controller layer pass without modification.
- The `CalculatorService` class is removed, and all arithmetic logic resides in the `Expression` implementations.
- The codebase adheres to the testing conventions: unit tests in `domain/` without Spring context, integration tests in `controller/` with Spring context.

# Out of Scope
- Changing the API contract (request/response JSON structure, HTTP status codes).
- Introducing new arithmetic operations beyond the existing set (add, subtract, multiply, divide, power, root).
- Modifying the validation logic in `ComposeExpressionValidator` beyond adapting it to build the new expression tree.

# Risks and Assumptions
- Risk: Incorrect implementation of expression evaluation could lead to numerical differences. Mitigated by ensuring the new implementation uses the same `BigDecimal`-based approach as the original service for addition and multiplication, and preserves the original behavior for other operations.
- Risk: Failure to properly propagate exceptions from `evaluate()` could alter error responses. Mitigated by preserving the exact exception types and messages.
- Assumption: The `ComposeExpressionValidator` currently produces a correct expression tree; adapting it to use the new factories will not change its parsing logic.
- Assumption: Java 25 features (`var`, static imports) are available and can be used to improve readability without affecting functionality.

# Acceptance Criteria
Given the calculator application is running
When a client sends a request to any of the arithmetic endpoints (`/sum`, `/subtract`, `/multiply`, `/divide`, `/power`, `/root`) with valid numeric payloads
Then the response status is 200 OK and the JSON body contains the correct arithmetic result (matching the original service's output).

Given the calculator application is running
When a client sends a request to any of the arithmetic endpoints with invalid input (e.g., division by zero, overflow)
Then the response status is 500 Internal Server Error (or appropriate error status) and the error message matches the original behavior.

Given the calculator application is running
When a client sends a request to `/compose` with a valid expression string
Then the response status is 200 OK and the JSON body contains the correct evaluated result.

Given the calculator application is running
When a client sends a request to `/compose` with an invalid expression string
Then the response status is 400 Bad Request (validation error) and the error message indicates invalid input.

Given the test suite
When running `./mvnw test`
Then all tests pass, confirming no regression and proper implementation of the new model.