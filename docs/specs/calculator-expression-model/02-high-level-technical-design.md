# Architecture Diagram
```
[Controller Layer]        [Domain Layer]
+---------------------+   +---------------------------+
| CalculatorController|   | Expression (sealed interface)|
| - endpoints: /sum,  |   |   |                       |
|   /subtract, etc.   |   |   +-----------+-----------+
| - uses Expression   |   |           |               |
|   factories and     |   |           |               |
|   ComposeExpression |   |           |               |
|   Validator         |   |           |               |
+---------------------+   |           |               |
                          |           |               |
                    +-----+-----+ +---+---+     +-----+-----+
                    |Expression| |Add      |     |Subtract  |
                    |literal() | |         |     |          |
                    +-----+-----+ +---------+     +----------+
                          |           |               |
                    +-----+-----+ +---------+     +----------+
                    |          | |Multiply |     |Divide    |
                    |          | |         |     |          |
                    +----------+ +---------+     +----------+
```
Note: Power and Root operations are also Expression implementations but are not shown for brevity.

# Component Responsibilities
- **CalculatorController**: 
  - Receives HTTP requests, delegates to Expression factories and ComposeExpressionValidator.
  - Uses static imports and `var` for brevity (Java 25).
  - Does not contain business logic; only orchestrates the expression tree creation and evaluation.
- **Expression (sealed interface)**: 
  - Defines the contract `ExpressionLiteral evaluate()`.
  - Provides static factories: `literal(double)`, `add(Expression, Expression)`, `subtract(...)`, `multiply(...)`, `divide(...)`, `power(Expression, Expression)`, `root(Expression, Expression)`.
- **ExpressionLiteral**: 
  - Immutable leaf node holding a double value.
  - `evaluate()` returns itself.
- **Add, Subtract, Multiply, Divide, Power, Root**: 
  - Each is a record implementing Expression with two Expression children.
  - `evaluate()` computes the result using the same precision-safe approach as the original CalculatorService (BigDecimal for add/multiply, double operations for others) and throws appropriate exceptions (ArithmeticException, NumericOverflowException).
- **ComposeExpressionValidator**: 
  - Unchanged in responsibility: parses a string expression and returns an Expression tree.
  - Now uses the Expression factories (`literal`, `add`, etc.) to build nodes.
- **ApiErrorHandler** (@ControllerAdvice): 
  - Catches exceptions from evaluate() and returns appropriate error responses (unchanged).

# Data Flow
1. HTTP request arrives at CalculatorController endpoint (e.g., /sum).
2. Controller extracts values from DTO, calls `Expression.literal()` for each operand, then `Expression.add()` to build the tree.
3. Controller calls `expr.evaluate().value()` to get the result.
4. Result wrapped in DTO and returned as ResponseEntity.
5. For /compose:
   - Controller passes string to `ComposeExpressionValidator.parse()`.
   - Validator returns an Expression tree built using the same factories.
   - Controller calls `expr.evaluate().value()` and wraps in ComposeResponse.

# Security and Observability Requirements
- No new security requirements introduced; the refactor does not change authentication, authorization, or data exposure.
- Observability: 
  - Logging levels remain unchanged.
  - No new metrics or tracing are added; existing error propagation (via exceptions) ensures error logs are produced as before.
- Input validation: 
  - Bean Validation on DTOs (for arithmetic endpoints) and ComposeExpressionValidator (for /compose) continue to validate input.
  - Exception handling ensures malformed input results in 4xx responses, arithmetic errors in 5xx responses.

# Trade-Offs and Alternatives
- Trade-off: 
  - Increased number of classes (one per operation) vs. single service with enum-based operations.
  - Mitigation: The new model is more extensible (adding a new operation requires adding a new record, not modifying existing code) and adheres to OCP.
- Alternative considered: 
  - Keeping CalculatorService and refactoring its internal methods to use the expression model for composed expressions only. 
  - Rejected because it would leave a hybrid model and not fully eliminate the anemic service.
- Alternative: 
  - Using the expression model for all operations but keeping CalculatorService as a thin facade that builds trees and calls evaluate(). 
  - Rejected because it adds an unnecessary layer; the controller can directly use the factories.

# High-Level Test Scenario Map
- Unit tests (domain):
  - Test each operation record (Add, Subtract, etc.) for correct evaluation and error propagation.
  - Test Expression.literal factory.
  - Test that the expression tree evaluates to the same result as the original service for a variety of inputs (including edge cases).
  - Test that exceptions thrown by evaluate() match those thrown by the original service.
- Integration tests (controller):
  - Existing tests in `src/test/java/com/example/demo/controller/` should pass without modification because:
    - They send the same JSON payloads.
    - They expect the same JSON responses and status codes.
    - They do not assert internal business logic (only HTTP and JSON structure).
  - Specifically verify:
    - Happy path for each arithmetic endpoint.
    - Error cases (division by zero, overflow, invalid input) produce the same status codes and error messages.
    - /compose endpoint with valid and invalid expressions.

# Notes
- All existing API contracts are preserved.
- The refactor is strictly limited to the domain layer and the controller's use of the domain layer.
- No changes to DTOs, validation annotations, or exception handling mechanisms.