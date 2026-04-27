# Low-Level Design and Version Policy

## API Contracts

- Endpoint: `POST /calculator/compose`
- Request body: a binary arithmetic expression tree in JSON.
- Each operation node must contain:
  - `operation`: one of `ADD`, `SUBTRACT`, `MULTIPLY`, `DIVIDE`
  - `left`: a numeric leaf or another expression node
  - `right`: a numeric leaf or another expression node
- The root node must be an operation node.
- Extra properties are rejected.
- Response body on success:
  - `{"result": <number>}`
- Error responses reuse the existing `ErrorResponse` contract.

## Data Models

- Transport layer:
  - `ComposeExpressionRequest` as the recursive request payload.
  - `ExpressionOperation` enum for request parsing.
- Domain layer:
  - `sealed interface Expression`
  - `ExpressionNode(ExpressionOperation operation, Expression left, Expression right)`
  - `ExpressionLiteral(double value)`
  - `ExpressionOperation` enum for domain evaluation, or a shared enum if the transport and domain model remain aligned.
- Mapping:
  - Validate structure at the boundary before converting to domain types.
  - Keep JSON-specific concerns out of the calculator service.

## Error Model

- Invalid structure, malformed JSON, or unsupported payload shape:
  - HTTP `400 Bad Request`
  - `ErrorResponse("Bad Request", "Invalid request body")`
- Arithmetic invalidity such as division by zero:
  - HTTP `400 Bad Request`
  - `ErrorResponse("Bad Request", <domain message>)`
- Overflow or non-finite results:
  - preserve the existing controlled failure policy already used by the application.
  - if overflow occurs, continue returning `422 Unprocessable Entity` with `ErrorResponse("Unprocessable Entity", <domain message>)`.

## Test Strategy

- Unit tests:
  - instantiate the domain evaluator directly.
  - verify recursive evaluation, nested expressions, and arithmetic error handling.
- Integration tests:
  - use `@SpringBootTest` and `@AutoConfigureMockMvc`.
  - validate HTTP status, response shape, and standardized error bodies.
  - ensure malformed or invalid trees fail at the edge.

## Test Scenario Catalog

- `10 + (2 * 3)` returns `16`.
- `(10 + 2) * 3` returns `36`.
- nested subtrees evaluate recursively.
- root missing `operation` returns `400`.
- internal node missing `left` or `right` returns `400`.
- unknown `operation` returns `400`.
- division by zero returns `400`.
- undefined or infinite result returns a controlled error.
- extra JSON properties are rejected.

## Dependency and Version Policy

- Current dependency set is sufficient.
- No new runtime dependency is required for this step.
- Structural validation will be implemented with the existing Jakarta Validation stack plus explicit recursive validation in application code.
- This keeps the implementation aligned with the current Spring Boot `4.0.5` baseline and avoids adding a schema engine that would increase maintenance overhead.
- Versioning policy:
  - keep Spring Boot-managed dependency versions unchanged unless a later step proves a real compatibility need.
  - do not introduce ad hoc library versions for this feature without a demonstrated benefit.

## Ordered Implementation Plan

1. Add recursive transport and domain types for the composed expression tree.
2. Implement boundary validation for tree shape and allowed operations.
3. Add a mapper from the validated request model to the domain model.
4. Implement recursive expression evaluation in the domain service or a dedicated collaborator.
5. Expose `POST /calculator/compose` through the existing calculator controller namespace.
6. Map validation and arithmetic failures through `ApiErrorHandler` using the current error shape.
7. Add unit tests for evaluation logic and integration tests for HTTP behavior.
