# Existing Codebase Understanding

## Repository Structure Overview

- Spring Boot application with entry point at [DemoApplication.java](/home/dearrudam/Downloads/demo2/src/main/java/com/example/demo/DemoApplication.java).
- HTTP layer concentrated in [CalculatorController.java](/home/dearrudam/Downloads/demo2/src/main/java/com/example/demo/controller/CalculatorController.java).
- API DTOs live under `src/main/java/com/example/demo/controller/api/`.
- Domain logic lives in [CalculatorService.java](/home/dearrudam/Downloads/demo2/src/main/java/com/example/demo/domain/CalculatorService.java).
- Global error mapping lives in [ApiErrorHandler.java](/home/dearrudam/Downloads/demo2/src/main/java/com/example/demo/controller/ApiErrorHandler.java).
- Integration tests are under `src/test/java/com/example/demo/controller/`.
- Unit tests are under `src/test/java/com/example/demo/domain/`.

## Architecture Summary

- The codebase uses a conventional controller-service split.
- Each arithmetic operation currently has its own HTTP endpoint and its own request/response DTO pair.
- The controller delegates directly to `CalculatorService`, keeping HTTP concerns thin.
- Error handling is centralized with `@RestControllerAdvice`.
- Existing calculations already use `BigDecimal` in some operations to reduce floating-point issues.

## Conventions to Preserve

- Keep domain tests free of Spring context.
- Keep HTTP tests as full integration tests with `@SpringBootTest` and `@AutoConfigureMockMvc`.
- Preserve the `ErrorResponse` contract for bad requests and domain errors.
- Keep DTOs as small `record` types.
- Keep controller methods focused on request mapping and service delegation.
- Prefer explicit validation at the boundary instead of leaking transport concerns into the domain.

## Integration Points

- The new composed-expression endpoint should be added to the existing calculator controller namespace.
- Request validation can reuse the current `spring-boot-starter-validation` dependency.
- OpenAPI generation is already present through `springdoc-openapi-starter-webmvc-ui`.
- The new domain model should be evaluated by `CalculatorService` or a dedicated domain collaborator without exposing Jackson types.
- The new error cases should be routed through `ApiErrorHandler` to preserve the API's existing error shape.

## Risks and Unknowns

- The binary expression tree contract is new and requires a dedicated transport model.
- Recursive validation introduces more complexity than the existing flat DTOs.
- It is not yet defined whether the composed expression endpoint should live alongside the existing endpoints or replace part of them.
- Division by zero, overflow, `NaN`, and `Infinity` need explicit contract decisions.
- The exact mapping from JSON Schema validation failures to the current error response still needs to be designed.

## Context to Carry Into Steps 02-06

- The codebase is already organized in a way that supports a thin controller and a focused domain service.
- The new feature should follow the same testing split: unit tests for evaluation logic and integration tests for HTTP behavior.
- The composed expression feature should introduce dedicated domain types for the tree structure.
- Validation should stay at the edge, while arithmetic semantics should stay in the domain.
- Step 02 should focus on how to integrate the new recursive payload with the existing controller-service architecture without eroding the current conventions.
