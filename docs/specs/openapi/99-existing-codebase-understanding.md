# Existing Codebase Understanding: OpenAPI

## Repository Structure Overview

- Maven project with Spring Boot 4.0.5 and Java 25.
- Application entry point: `com.example.demo.DemoApplication`.
- Main HTTP boundary:
  - `src/main/java/com/example/demo/controller/CalculatorController.java`
  - `src/main/java/com/example/demo/controller/ApiErrorHandler.java`
- HTTP DTOs are Java `record`s under `src/main/java/com/example/demo/controller/api/`.
- Business logic is centralized in `src/main/java/com/example/demo/domain/CalculatorService.java`.
- Tests are split between:
  - Integration tests under `src/test/java/com/example/demo/controller/`.
  - Unit tests under `src/test/java/com/example/demo/domain/`.
  - Smoke/context test in `DemoApplicationTests`.

## Architecture Summary

The application is a compact Spring MVC API. `CalculatorController` exposes POST endpoints under `/calculator`, delegates arithmetic behavior to `CalculatorService`, and returns typed response DTOs. Request DTOs use Jakarta Validation, mainly `@NotNull`, and `ApiErrorHandler` standardizes validation, parsing, arithmetic and overflow errors.

OpenAPI should integrate at the HTTP boundary through Spring MVC metadata and should not require changes to domain logic.

## Conventions to Preserve

- Keep business logic tests as pure JUnit tests without Spring context.
- Keep HTTP behavior tests as integration tests with:
  - `@SpringBootTest`
  - `@AutoConfigureMockMvc` from `org.springframework.boot.webmvc.test.autoconfigure`
  - `MockMvcTester`
- Do not mix API documentation verification with domain behavior assertions.
- Preserve existing endpoint paths and JSON contracts.
- Prefer minimal configuration and dependency changes.

## Integration Points

- `pom.xml`: likely integration point for adding an OpenAPI/SpringDoc dependency compatible with Spring Boot 4.0.5.
- `CalculatorController`: source of generated endpoint operations for `/calculator/sum`, `/calculator/subtract`, `/calculator/multiply`, `/calculator/divide`, `/calculator/power`, and `/calculator/root`.
- DTO records in `controller/api`: source of request/response schemas.
- `ApiErrorHandler`: source of standardized error response behavior; may need documentation later, but should not be behaviorally changed for initial OpenAPI enablement.
- `application.properties`: optional location for minimal OpenAPI/Swagger UI metadata if needed.
- New integration test may validate `/v3/api-docs` and Swagger UI availability.

## Risks and Unknowns

- The correct OpenAPI dependency version for Spring Boot 4.0.5 must be validated before implementation.
- Spring Boot 4 package changes can make older SpringDoc artifacts incompatible.
- Auto-generated docs may expose schema names and operation IDs that are acceptable initially but not fully curated.
- Swagger UI route behavior may return either direct 200 or redirect depending on library defaults.
- OpenAPI tests must avoid depending on excessive generated JSON details beyond acceptance criteria.

## Context to Carry Into Steps 02-06

- The target implementation should be dependency-first and minimal.
- Primary verification should be an integration test against the generated docs endpoint and Swagger UI route.
- Existing calculator endpoint tests should remain unchanged unless later specs explicitly require changes.
- Final verification should run `./mvnw test`.
- Any version choice must be recorded in Step 03 dependency/version policy and validated during Step 06.
