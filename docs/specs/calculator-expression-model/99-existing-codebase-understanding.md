# Repository Structure Overview
The project is a Maven-based Spring Boot 4.0.5 application using Java 25.
- Source code: `src/main/java/com/example/demo/`
  - `domain/` – contains `CalculatorService` and expression model (`Expression`, `ExpressionLiteral`, `ExpressionNode`, `ExpressionOperation`).
  - `controller/` – REST controllers (`CalculatorController`) and DTOs (`api/` package).
  - `controller/validation/` – `ComposeExpressionValidator` and exception classes.
- Test code: `src/test/java/com/example/demo/`
  - Mirrors source structure: `domain/` for unit tests, `controller/` for integration tests.
  - Test classes follow `{ClassName}Test.java` naming.

# Architecture Summary
- Entry point: `DemoApplication`.
- Core domain: `CalculatorService` provides arithmetic operations (sum, subtract, multiply, divide, power, root) and an `evaluate(Expression)` method for expression trees.
- Expression model: 
  - `Expression` (sealed interface) with implementations `ExpressionLiteral` (leaf) and `ExpressionNode` (internal node holding left/right and an `ExpressionOperation` enum).
  - `ExpressionOperation` enum defines ADD, SUBTRACT, MULTIPLY, DIVIDE.
- Controller layer: `CalculatorController` exposes endpoints for each operation and a `/compose` endpoint that uses `ComposeExpressionValidator` to parse a string into an `Expression` tree, then calls `CalculatorService.evaluate()`.
- Validation: `ComposeExpressionValidator` parses infix expressions (assuming) and builds the expression tree; throws exceptions on invalid input.
- Error handling: `ApiErrorHandler` (@ControllerAdvice) returns consistent error responses.

# Conventions to Preserve
- Testing conventions (see `TESTING_CONVENTIONS.md` and `AGENTS.md`):
  - Unit tests (logic of business): instantiate classes directly, no Spring context, use JUnit 5 Assertions.
  - Integration tests (border): `@SpringBootTest` + `@AutoConfigureMockMvc`, use `MockMvcTester` and AssertJ, test only HTTP status and JSON structure, not business logic.
  - Keep unit tests in `src/test/java/com/example/demo/domain/` and integration tests in `src/test/java/com/example/demo/controller/`.
- API contracts: request/response DTOs are `record` classes in `controller/api/` (e.g., `SumRequest`, `SumResponse`). Endpoints must maintain exact JSON field names and types.
- Exception handling: arithmetic exceptions (e.g., division by zero, overflow) are propagated from service layer and turned into HTTP 500 responses via `@ControllerAdvice`.
- Use `var` and static imports where appropriate (Java 25) to reduce verbosity, but only after confirming readability.
- Maintain existing package structure; new files should fit naturally.

# Integration Points
- `CalculatorController` depends on `CalculatorService` (for arithmetic) and `ComposeExpressionValidator` (for `/compose`).
- `ComposeExpressionValidator` currently returns an `Expression` (the sealed interface) that is passed to `CalculatorService.evaluate()`.
- The expression tree built by the validator uses `ExpressionNode` and `ExpressionOperation` enum.
- Controller endpoints extract values from DTOs, delegate to service, wrap results in DTO responses.
- No external dependencies beyond Spring Boot core and testing libraries.

# Risks and Unknowns
- The exact parsing strategy of `ComposeExpressionValidator` is not fully inspected; we assume it correctly builds an expression tree using the existing `ExpressionNode`/`ExpressionOperation` model. When replacing the expression model, we must ensure the validator can build equivalent trees using the new factories.
- The current service uses `BigDecimal` for addition and multiplication to avoid floating‑point precision issues; subtraction, division, power, and root use primitive double operations. The new expression model must replicate this behavior exactly.
- There may be hidden dependencies on the specific class names (`CalculatorService`, `ExpressionNode`, etc.) in tests or configuration; a full text search will be needed before deletion.
- The project may have additional expression-related utilities not yet discovered (e.g., visitors, optimizers); we should verify no other references to the expression model exist.
- Java 25 features are available, but we must ensure the target runtime supports them (the project already uses Java 25).

# Context to Carry Into Steps 02-06
- Keep the same API contracts (request/response JSON, status codes).
- Preserve the existing exception types and messages for error cases.
- Ensure the new expression model (`Expression` implementations) can be built by the existing `ComposeExpressionValidator` (or a modified version) and produce numerically identical results to the current service.
- Maintain the separation: unit tests for domain logic (no Spring), integration tests for HTTP layer.
- When removing `CalculatorService`, ensure its responsibilities are fully absorbed by the new expression model and controller logic.
- Use static imports and `var` in new code to improve readability, following the project's adoption of Java 25.
- Verify that all existing tests pass after refactoring (unit tests may need to be rewritten to use the new model; integration tests should remain unchanged).