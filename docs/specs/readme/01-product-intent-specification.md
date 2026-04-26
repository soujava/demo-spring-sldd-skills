# Product Intent Specification: README

## Problem Statement

O projeto nao possui `README.md`, dificultando descoberta rapida do objetivo da aplicacao, comandos corretos, endpoints disponiveis, documentacao OpenAPI e convencoes de teste.

## Target Users

- Desenvolvedores que vao executar, testar ou manter o projeto.
- Pessoas revisando a API de calculadora.
- Agentes ou automacoes que precisam identificar comandos e estrutura do projeto.

## Success Metrics

- O repositorio passa a ter um `README.md` na raiz.
- O README documenta requisitos, execucao, testes, OpenAPI, endpoints, exemplos e estrutura principal.
- O README usa comandos corretos do projeto, especialmente `./mvnw spring-boot:run`.
- O README referencia `TESTING_CONVENTIONS.md`.

## Out of Scope

- Alterar codigo de producao.
- Alterar testes automatizados.
- Criar documentacao extensa de arquitetura alem do necessario para uso inicial.
- Substituir `TESTING_CONVENTIONS.md`.

## Risks and Assumptions

- O README deve refletir o estado atual do projeto, incluindo a integracao OpenAPI ja implementada.
- Exemplos `curl` devem ser simples e alinhados aos DTOs existentes.
- Como a mudanca e documental, verificacao automatizada completa nao e obrigatoria, mas a existencia e o conteudo do arquivo devem ser conferidos.

## Acceptance Criteria (Given/When/Then)

### AC1 - README existe

Given o repositorio do projeto
When a raiz e inspecionada
Then deve existir um arquivo `README.md`.

### AC2 - Comandos principais documentados

Given o README
When a secao de execucao e testes e lida
Then deve conter `./mvnw spring-boot:run` e `./mvnw test`.

### AC3 - Endpoints documentados

Given o README
When a secao de endpoints e lida
Then deve listar os seis endpoints da calculadora e seus payloads.

### AC4 - OpenAPI documentado

Given o README
When a secao OpenAPI e lida
Then deve indicar `/v3/api-docs` e `/swagger-ui.html`.

### AC5 - Convencoes de teste referenciadas

Given o README
When a secao de testes e lida
Then deve apontar para `TESTING_CONVENTIONS.md`.
