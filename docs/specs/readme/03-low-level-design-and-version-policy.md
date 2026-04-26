# Low-Level Design and Version Policy: README

## API Contracts

- Nao ha novos contratos HTTP.
- O README deve documentar os contratos existentes em nivel de uso:
  - `POST /calculator/sum`
  - `POST /calculator/subtract`
  - `POST /calculator/multiply`
  - `POST /calculator/divide`
  - `POST /calculator/power`
  - `POST /calculator/root`
  - `GET /v3/api-docs`
  - `GET /swagger-ui.html`

## Data Models

- O README deve listar os campos de payload de cada endpoint.
- Respostas de sucesso devem ser descritas como JSON com `result`.
- Erros devem ser descritos como JSON com `error` e `message`.

## Error Model

- O README deve apresentar exemplo de erro `Bad Request`.
- O README deve apresentar exemplo de erro `Unprocessable Entity` para overflow numerico.
- Nao deve alterar `ApiErrorHandler`.

## Test Strategy

- Como a alteracao e documental, a verificacao primaria e inspecao de conteudo.
- Usar comandos shell simples para conferir:
  - existencia de `README.md`
  - presenca de comandos principais
  - presenca dos endpoints
  - presenca das rotas OpenAPI
  - referencia a `TESTING_CONVENTIONS.md`
- Nao criar testes automatizados Java para documentacao.

## Test Scenario Catalog

- `README.md` existe na raiz.
- README contem `./mvnw spring-boot:run`.
- README contem `./mvnw test`.
- README contem `/calculator/sum`, `/calculator/subtract`, `/calculator/multiply`, `/calculator/divide`, `/calculator/power`, `/calculator/root`.
- README contem `/v3/api-docs` e `/swagger-ui.html`.
- README contem `TESTING_CONVENTIONS.md`.

## Dependency and Version Policy

- Nenhuma nova dependencia.
- O README deve registrar stack atual:
  - Spring Boot 4.0.5
  - Java 25
  - Maven
  - SpringDoc OpenAPI

## Ordered Implementation Plan

1. Criar `README.md` na raiz.
2. Documentar requisitos, execucao e testes.
3. Documentar OpenAPI.
4. Documentar endpoints e exemplos `curl`.
5. Documentar erros e estrutura principal.
6. Verificar conteudo com `test` e `rg`.
7. Salvar relatorios SLDD e marcar checklist.
