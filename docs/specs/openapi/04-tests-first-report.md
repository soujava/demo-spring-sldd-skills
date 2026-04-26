# Tests First Report: OpenAPI

## Test Files Created

- `src/test/java/com/example/demo/controller/OpenApiDocumentationTest.java`

## Acceptance Criteria -> Tests Mapping

- AC1 - Especificacao OpenAPI disponivel
  - `returnsOpenApiJsonDocument`
  - Verifica `GET /v3/api-docs`, status 200, content type JSON e campo `"openapi"`.

- AC2 - Swagger UI disponivel
  - `servesSwaggerUi`
  - Verifica `GET /swagger-ui.html`, aceitando status entre 200 e 399.

- AC3 - Endpoints da calculadora documentados
  - `documentsCalculatorPaths`
  - Verifica que `/v3/api-docs` contem os paths:
    - `/calculator/sum`
    - `/calculator/subtract`
    - `/calculator/multiply`
    - `/calculator/divide`
    - `/calculator/power`
    - `/calculator/root`

- AC4 - Comportamento existente preservado
  - Sera coberto na Step 05/06 com `./mvnw test` completo apos implementacao.

- AC5 - Convencoes de teste preservadas
  - O teste usa `@SpringBootTest`, `@AutoConfigureMockMvc` e `MockMvcTester`.

## Test Commands Executed

- `./mvnw -Dtest=OpenApiDocumentationTest test`

## Failing Results Summary

Resultado final da Red phase:

- Tests run: 3
- Failures: 3
- Errors: 0
- Skipped: 0
- Build result: `BUILD FAILURE`

Falhas relevantes:

- `returnsOpenApiJsonDocument`
  - Esperado: HTTP 200
  - Atual: HTTP 404
  - Motivo: `No static resource v3/api-docs`

- `documentsCalculatorPaths`
  - Esperado: HTTP 200
  - Atual: HTTP 404
  - Motivo: `No static resource v3/api-docs`

- `servesSwaggerUi`
  - Esperado: status entre 200 e 399
  - Atual: HTTP 404
  - Motivo: `No static resource swagger-ui.html`

Observacao: houve uma primeira execucao com erro de compilacao no teste por `UnsupportedEncodingException`; corrigi apenas o teste recem-criado e reexecutei ate obter a falha Red funcional esperada.

## Red-Phase Confirmation

Red phase confirmada. Os testes foram escritos antes da implementacao, nenhum codigo de producao foi alterado, nenhuma dependencia foi adicionada, e a falha atual demonstra a ausencia da integracao OpenAPI/Swagger UI.
