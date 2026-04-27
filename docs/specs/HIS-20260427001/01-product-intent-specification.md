# Product Intent Specification

## Problem Statement

The application needs to expose an HTTP endpoint that evaluates composed arithmetic expressions represented as a binary tree in JSON. The contract must allow nested combinations of `ADD`, `SUBTRACT`, `MULTIPLY`, and `DIVIDE`, where mathematical precedence is expressed by the tree structure itself. The payload root must be an operation node, leaves may be `double` values or nested subtrees, and the domain layer must work with specialized domain types rather than raw JSON.

## Target Users

- Developers consuming the API to evaluate composed arithmetic expressions.
- Client systems that generate binary expression trees programmatically.
- Engineering teams that need a strongly typed, testable, and explicit calculation contract.
- Automation tools that validate or produce calculator payloads.

## Success Metrics

- The endpoint returns HTTP `200` for valid expression trees.
- The response body contains the final numeric result.
- Structural validation rejects malformed trees before domain evaluation.
- Invalid payloads return HTTP `400` with a standardized error body.
- Division by zero is handled deterministically and returns a controlled error.
- The domain receives a dedicated expression model, not `JsonNode`.
- The expression evaluator is implemented with sealed types and pattern matching.
- Automated tests cover simple trees, nested trees, and error cases.

## Out of Scope

- Persisting calculation history.
- Authentication or authorization.
- Support for non-arithmetic operations.
- Support for infix text parsing with operator precedence inferred from a raw string.
- N-ary operations; all operations are binary.
- Non-numeric leaves.
- Advanced rounding policies or finance-specific behavior.
- Internationalization of validation and error messages.

## Risks and Assumptions

- I assume structural validation will be implemented with a JSON Schema wrapped in a custom Bean Validation constraint.
- I assume the root of the payload must always be an operation node.
- There is a risk of ambiguity if extra JSON properties are allowed; the recommendation is to reject them explicitly.
- There is a risk of division by zero inside nested subtrees.
- There is a risk of `NaN`, `Infinity`, and numeric overflow in extreme cases.
- There is a risk of coupling the domain to HTTP or Jackson concerns if the conversion boundary is not kept explicit.
- There is a risk of future operation growth requiring a clear versioning policy.

## Acceptance Criteria

- Given a valid payload representing `10 + (2 * 3)`
  When the composed-expression endpoint is called
  Then the API responds with HTTP `200`
  And the body returns the result `16`

- Given a valid payload representing `(10 + 2) * 3`
  When the composed-expression endpoint is called
  Then the API responds with HTTP `200`
  And the body returns the result `36`

- Given a valid payload representing `(20 / 4) / 2`
  When the composed-expression endpoint is called
  Then the API responds with HTTP `200`
  And the body returns the result `2.5`

- Given a payload whose root node is missing the `operation` field
  When the composed-expression endpoint is called
  Then the API responds with HTTP `400`
  And the body indicates a validation error

- Given a payload with an internal node missing `left` or `right`
  When the composed-expression endpoint is called
  Then the API responds with HTTP `400`
  And the body indicates a validation error

- Given a payload with an unknown operation
  When the composed-expression endpoint is called
  Then the API responds with HTTP `400`
  And the body indicates a validation error

- Given a payload that causes division by zero
  When the composed-expression endpoint is called
  Then the API responds with HTTP `400`
  And the body indicates an invalid arithmetic operation

- Given a structurally valid payload that produces an undefined or infinite mathematical result
  When the composed-expression endpoint is called
  Then the API responds with a standardized error
  And the behavior is explicitly defined in the technical design and tests
