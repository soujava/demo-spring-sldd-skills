# High-Level Technical Design

## Architecture Diagram

```text
Client
  -> POST /calculator/compose
    -> Controller
      -> Custom Bean Validation constraint
        -> JSON Schema validation
          -> Mapper / Assembler
            -> Domain Expression model
              -> CalculatorService.evaluate(Expression)
                -> Response DTO
```

## Component Responsibilities

- Controller: receives the request and returns the HTTP response.
- Custom Bean Validation constraint: validates the JSON tree shape before conversion.
- JSON Schema validator: enforces the binary-tree contract.
- Mapper/Assembler: converts the validated JSON payload into domain types.
- Domain model: represents the expression with a `sealed interface` and `records`.
- CalculatorService: evaluates the tree recursively using pattern matching.
- ApiErrorHandler: converts validation and arithmetic failures into standardized responses.

## Data Flow

1. The client sends a binary expression tree in JSON.
2. The controller receives the payload.
3. The custom constraint validates the structure with JSON Schema.
4. If validation fails, the request ends with a standardized HTTP error.
5. If validation passes, the mapper converts the JSON into the domain model.
6. The domain service evaluates the expression recursively.
7. The final numeric result is returned in a simple response DTO.

## Security and Observability Requirements

- Reject invalid structures at the boundary before any arithmetic execution.
- Do not expose `JsonNode` or serialization concerns to the domain.
- Preserve the current error shape so client integrations remain stable.
- Report validation and arithmetic failures consistently.
- Avoid silent partial evaluation when the tree is invalid.

## Trade-Offs and Alternatives

- Binary expression tree: simpler to validate, document, and test.
- Textual infix parsing: more flexible, but introduces ambiguity and parser complexity.
- Domain-only validation: simpler initially, but worse for error feedback and maintenance.
- Nested DTOs without schema: possible, but weaker for recursive structural validation.

## High-Level Test Scenario Map

- Structural validation:
  - root missing `operation`
  - internal node missing `left` or `right`
  - unknown operation
  - extra properties
- Happy paths:
  - `10 + (2 * 3)`
  - `(10 + 2) * 3`
  - combinations with division and subtraction
- Error paths:
  - division by zero
  - malformed payload
  - undefined or infinite result
- HTTP boundary:
  - status code
  - response JSON format
  - standardized error body
- Domain:
  - recursive evaluation of the tree
  - pattern matching over sealed types
