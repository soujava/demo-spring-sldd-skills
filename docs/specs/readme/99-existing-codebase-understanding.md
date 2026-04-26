# Existing Codebase Understanding: README

## Repository Structure Overview

- Projeto Maven com wrapper (`mvnw`, `mvnw.cmd`).
- Aplicacao Spring Boot em `src/main/java/com/example/demo`.
- Controladores HTTP em `src/main/java/com/example/demo/controller`.
- DTOs em `src/main/java/com/example/demo/controller/api`.
- Logica de negocio em `src/main/java/com/example/demo/domain`.
- Testes unitarios e de integracao em `src/test/java/com/example/demo`.
- Documentos existentes em `docs/specs` e `TESTING_CONVENTIONS.md`.

## Architecture Summary

A aplicacao expoe uma API REST de calculadora sob `/calculator`. O controlador delega para `CalculatorService`, DTOs `record` representam payloads e respostas, `ApiErrorHandler` padroniza erros, e SpringDoc publica OpenAPI em `/v3/api-docs` e Swagger UI em `/swagger-ui.html`.

## Conventions to Preserve

- Documentar comandos com Maven Wrapper quando aplicavel.
- Usar o goal correto `spring-boot:run`.
- Manter explicacao de testes alinhada a `TESTING_CONVENTIONS.md`.
- Nao duplicar excessivamente a convencao completa de testes no README.
- Manter README objetivo e operacional.

## Integration Points

- `README.md` na raiz do repositorio.
- `pom.xml` para versoes e dependencias relevantes.
- `CalculatorController` e DTOs para listar endpoints e payloads.
- `TESTING_CONVENTIONS.md` para referencia das regras de teste.

## Risks and Unknowns

- O README pode ficar desatualizado se endpoints ou DTOs mudarem.
- Exemplos `curl` dependem da porta padrao `8080`.
- O comando sem wrapper `mvn spring-boot:run` tambem pode funcionar em ambientes com Maven instalado, mas o projeto documenta preferencialmente `./mvnw`.

## Context to Carry Into Steps 02-06

- Mudanca deve ser documental.
- Nao ha necessidade de alterar codigo de aplicacao ou testes.
- Verificacao deve confirmar existencia do README e presenca dos comandos/endpoints essenciais.
