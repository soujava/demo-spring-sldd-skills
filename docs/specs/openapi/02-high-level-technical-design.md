# High-Level Technical Design: OpenAPI

## Architecture Diagram

```text
HTTP client / browser
        |
        | GET /v3/api-docs
        | GET /swagger-ui/index.html or /swagger-ui.html
        v
Spring MVC application
        |
        +-- OpenAPI integration library
        |     |
        |     +-- scans Spring MVC mappings
        |     +-- derives schemas from request/response records
        |     +-- exposes OpenAPI JSON and Swagger UI resources
        |
        +-- CalculatorController
        |     |
        |     +-- SumRequest/SumResponse
        |     +-- SubtractRequest/SubtractResponse
        |     +-- MultiplyRequest/MultiplyResponse
        |     +-- DivideRequest/DivideResponse
        |     +-- PowerRequest/PowerResponse
        |     +-- RootRequest/RootResponse
        |
        +-- ApiErrorHandler
        |
        +-- CalculatorService
```

## Component Responsibilities

- `pom.xml`: add the compatible OpenAPI/Spring MVC starter dependency.
- OpenAPI integration library: expose generated OpenAPI JSON and Swagger UI endpoints using Spring MVC metadata.
- `CalculatorController`: remain the source of HTTP endpoint mappings and generated operation paths.
- DTO records in `controller/api`: remain the source for generated request and response schemas.
- `ApiErrorHandler`: continue handling runtime HTTP error behavior; detailed error response documentation can be kept minimal unless Step 03 requires explicit annotations.
- Integration tests: verify the OpenAPI boundary without asserting domain arithmetic behavior.

## Data Flow

1. Application starts through `DemoApplication`.
2. Spring Boot auto-configuration loads MVC controllers and the OpenAPI integration.
3. The OpenAPI integration scans request mappings and DTO types.
4. A client requests `/v3/api-docs`.
5. The application returns generated OpenAPI JSON containing known calculator paths and schema metadata.
6. A browser/client requests the Swagger UI route.
7. The application serves the documentation UI backed by the generated OpenAPI document.
8. Existing calculator requests continue to flow through `CalculatorController -> CalculatorService -> response DTO` unchanged.

## Security and Observability Requirements

- No authentication or authorization changes are introduced.
- OpenAPI and Swagger UI endpoints are public by default, matching the current unauthenticated application.
- No new logging, metrics or tracing requirements are introduced.
- The implementation should avoid exposing sensitive data; current calculator DTOs contain only numeric fields and standardized error fields.
- Test evidence must include successful execution of `./mvnw test`.

## Trade-Offs and Alternatives

- Preferred approach: use an OpenAPI/Spring MVC starter dependency with auto-configuration.
  - Pros: minimal code, aligns with Spring MVC metadata, low maintenance.
  - Cons: generated operation details may be generic without explicit annotations.
- Alternative: manually write and maintain a static OpenAPI YAML/JSON file.
  - Pros: full control over generated contract.
  - Cons: easy to drift from actual controllers and higher maintenance.
- Alternative: add detailed OpenAPI annotations to every controller method and DTO.
  - Pros: richer documentation.
  - Cons: larger change surface and not required for initial enablement.
- Initial scope should favor generated documentation plus focused tests. Rich annotation work can be a later feature.

## High-Level Test Scenario Map

- AC1: integration test GET `/v3/api-docs` returns 200 and JSON containing an OpenAPI version field.
- AC2: integration test GET Swagger UI route returns a successful response or expected redirect.
- AC3: integration test inspects `/v3/api-docs` and confirms calculator paths are present:
  - `/calculator/sum`
  - `/calculator/subtract`
  - `/calculator/multiply`
  - `/calculator/divide`
  - `/calculator/power`
  - `/calculator/root`
- AC4: full test suite `./mvnw test` remains green.
- AC5: any new HTTP-boundary tests use `@SpringBootTest`, `@AutoConfigureMockMvc`, and `MockMvcTester`.
