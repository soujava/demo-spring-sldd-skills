# Product Intent Specification: OpenAPI

## Problem Statement

O projeto expoe endpoints HTTP de calculadora, mas nao oferece uma especificacao OpenAPI acessivel para descoberta, documentacao e validacao basica do contrato da API.

## Target Users

- Desenvolvedores que consomem ou mantem os endpoints `/calculator/*`.
- Pessoas revisando contratos HTTP do projeto.
- Ferramentas compativeis com OpenAPI, como Swagger UI, geradores de clientes e validadores de contrato.

## Success Metrics

- A aplicacao expoe a especificacao OpenAPI em endpoint padrao.
- A aplicacao expoe uma UI navegavel para documentacao da API.
- Os endpoints existentes da calculadora aparecem na documentacao gerada.
- A inclusao do OpenAPI nao altera o comportamento dos endpoints existentes.
- `./mvnw test` continua passando.

## Out of Scope

- Reescrever DTOs ou endpoints existentes.
- Introduzir autenticacao, versionamento de API ou gateway.
- Customizar profundamente tema, layout ou branding da documentacao.
- Gerar clientes OpenAPI.
- Alterar logica de negocio da calculadora.

## Risks and Assumptions

- Assumimos compatibilidade entre Spring Boot 4.0.5 e a versao escolhida da biblioteca OpenAPI.
- A documentacao inicial podera ser baseada em metadados gerados automaticamente, com customizacoes minimas.
- Mudancas de dependencia podem afetar inicializacao do contexto Spring se a versao escolhida nao for compativel.
- O comportamento esperado sera validado na camada de borda com contexto Spring completo, sem misturar logica de negocio.

## Acceptance Criteria (Given/When/Then)

### AC1 - Especificacao OpenAPI disponivel

Given a aplicacao esta iniciada
When uma requisicao GET e feita para o endpoint da especificacao OpenAPI
Then a resposta deve ter status 200
And o corpo deve conter um documento OpenAPI valido em JSON.

### AC2 - Swagger UI disponivel

Given a aplicacao esta iniciada
When uma requisicao GET e feita para a UI de documentacao
Then a resposta deve ter status 200 ou redirecionamento esperado
And a UI deve estar acessivel sem exigir configuracao adicional.

### AC3 - Endpoints da calculadora documentados

Given a especificacao OpenAPI e consultada
When o documento e inspecionado
Then deve listar os endpoints HTTP existentes da calculadora, incluindo `/calculator/sum`, `/calculator/subtract`, `/calculator/multiply`, `/calculator/divide`, `/calculator/power` e `/calculator/root`.

### AC4 - Comportamento existente preservado

Given os testes existentes da aplicacao
When `./mvnw test` e executado
Then todos os testes existentes devem continuar passando.

### AC5 - Convencoes de teste preservadas

Given novos testes sejam necessarios para validar a borda HTTP
When esses testes forem implementados
Then devem usar `@SpringBootTest`, `@AutoConfigureMockMvc` de `org.springframework.boot.webmvc.test.autoconfigure` e `MockMvcTester`, conforme `TESTING_CONVENTIONS.md`.
