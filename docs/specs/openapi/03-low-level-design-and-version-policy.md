# Low-Level Design and Version Policy: OpenAPI

## API Contracts

- New documentation endpoints provided by SpringDoc:
  - `GET /v3/api-docs`
    - Expected status: `200`
    - Expected content type: JSON-compatible
    - Expected body: OpenAPI document with `openapi` field and `paths`.
  - `GET /swagger-ui.html` or `GET /swagger-ui/index.html`
    - Expected status: `200` or documented redirect to Swagger UI.
- Existing calculator endpoints remain unchanged:
  - `POST /calculator/sum`
  - `POST /calculator/subtract`
  - `POST /calculator/multiply`
  - `POST /calculator/divide`
  - `POST /calculator/power`
  - `POST /calculator/root`

## Data Models

- No new application DTOs are required.
- OpenAPI schemas should be generated from existing request/response records under `com.example.demo.controller.api`.
- Existing validation annotations such as `@NotNull` should be reflected where supported by the OpenAPI integration.
- No domain model changes are planned.

## Error Model

- Runtime error behavior remains controlled by `ApiErrorHandler`.
- Initial OpenAPI enablement does not require custom error annotations.
- Existing error response shape remains:
  - `error`
  - `message`
- Tests for OpenAPI should verify documentation availability and path presence, not full error-schema documentation.

## Test Strategy

- Add a new HTTP-boundary integration test, likely `OpenApiDocumentationTest`.
- Use existing integration conventions:
  - `@SpringBootTest`
  - `@AutoConfigureMockMvc`
  - `@Autowired MockMvcTester`
  - AssertJ assertions
- Test `/v3/api-docs` for:
  - status 200
  - body contains an OpenAPI version field
  - body contains every calculator path
- Test Swagger UI route for availability:
  - prefer `/swagger-ui.html`
  - accept redirect or successful response if that is the library behavior
- Run full suite with `./mvnw test`.

## Test Scenario Catalog

- `returnsOpenApiJsonDocument`
  - GET `/v3/api-docs`
  - expects 200
  - expects JSON containing `openapi`.

- `documentsCalculatorPaths`
  - GET `/v3/api-docs`
  - expects paths for all six calculator endpoints.

- `servesSwaggerUi`
  - GET `/swagger-ui.html`
  - expects 2xx or 3xx response, depending on SpringDoc route behavior.

- Existing tests remain unchanged and must pass.

## Dependency and Version Policy

- Add:
  - `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3`
- Rationale:
  - The project uses Spring Boot 4.0.5 and `spring-boot-starter-web`.
  - SpringDoc v3 documentation states support for Spring Boot v4.
  - SpringDoc getting-started documentation lists `springdoc-openapi-starter-webmvc-ui` version `3.0.3` for Spring Boot + Swagger UI integration.
  - Maven Central shows `3.0.3` is available for `springdoc-openapi-starter-webmvc-ui`.
- Do not add `springdoc-openapi-starter-webmvc-api` separately because `webmvc-ui` is the intended starter when Swagger UI is required.
- Do not add explicit Swagger UI or OpenAPI transitive dependencies unless Maven resolution requires it.
- Version must be pinned explicitly in `pom.xml`.

References:
- SpringDoc v3 docs: https://springdoc.org/v4/
- Maven Central artifact listing: https://repo.maven.apache.org/maven2/org/springdoc/springdoc-openapi-starter-webmvc-ui/

## Ordered Implementation Plan

1. Add SpringDoc dependency to `pom.xml`:
   - `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3`.

2. Add integration test file under `src/test/java/com/example/demo/controller/`:
   - `OpenApiDocumentationTest.java`.

3. In the test, verify `GET /v3/api-docs`:
   - status 200
   - body JSON contains `openapi`
   - body JSON contains expected calculator paths.

4. In the test, verify Swagger UI route:
   - `GET /swagger-ui.html`
   - status is successful or redirection.

5. Run the red phase in Step 04 before production changes:
   - create tests first
   - execute targeted test command
   - record failing result caused by missing OpenAPI dependency/endpoints.

6. In Step 05, apply the minimal implementation:
   - add only the dependency needed to satisfy tests
   - avoid controller/domain changes unless tests reveal a concrete integration issue.

7. Run full verification:
   - `./mvnw test`.

8. In Step 06, confirm:
   - acceptance criteria coverage
   - dependency version used
   - test convention compliance
   - Go/No-Go decision.
