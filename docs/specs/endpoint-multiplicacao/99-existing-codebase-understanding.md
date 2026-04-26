# 99 — Existing Codebase Understanding and Context Summary

## Repository Structure Overview

- Maven project, Spring Boot 4.0.5, Java 25
- Entry: `com.example.demo.DemoApplication`
- Controller: `CalculatorController`
- Service: `CalculatorService`
- API models: `com.example.demo.controller.api`

## Architecture Summary

- Layered: Controller -> Service -> Domain
- Existing sum/subtract endpoints
- Request/Response DTOs in `api` package

## Conventions to Preserve

- Unit tests: JUnit pure, no Spring context (domain logic)
- Integration tests: `@SpringBootTest` + `@AutoConfigureMockMvc` with `MockMvcTester`
- Sum endpoint uses `BigDecimal` for precision
- Error handling via `ApiErrorHandler` returning `ErrorResponse`

## Integration Points

- POST `/calculator/sum`
- POST `/calculator/subtract`

## Risks and Unknowns

- Multiplication should follow same precision pattern as sum