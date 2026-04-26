# High-Level Technical Design

## Architecture diagram

```text
Maintainer / Client
    |
    v
Build and Runtime Platform
    - Java 25
    - Spring Boot 4
    |
    v
Project Documentation
    - AGENTS.md
    - TESTING_CONVENTIONS.md
    |
    v
Application Layer
    - CalculatorController
    - SumRequest / SumResponse
    - Error handler for invalid requests
    |
    v
Domain Layer
    - CalculatorService
    |
    v
HTTP Responses
    - 200 com SumResponse
    - 400 com ErrorResponse padronizado

Fluxo de remediacao:
pom.xml atualizado
    |
    v
Stack oficial do projeto
    |
    v
Codigo + testes + documentacao alinhados
```

## Component responsibilities

- `pom.xml`
  - Atualizar o projeto para Java `25` e Spring Boot `4`
  - Servir como fonte de verdade da stack oficial

- `AGENTS.md`
  - Refletir Java `25`, Spring Boot `4` e os comandos reais do projeto
  - Remover divergencias com a stack efetiva

- `TESTING_CONVENTIONS.md`
  - Refletir Spring Boot `4`
  - Atualizar exemplos para `firstAddend` e `secondAddend`
  - Formalizar `MockMvcTester` como abordagem oficial de teste HTTP

- `CalculatorController`
  - Manter `POST /calculator/sum`
  - Continuar delegando a soma ao service
  - Integrar-se ao tratamento padronizado de erro

- `ErrorResponse`
  - Formalizar o contrato minimo de erro HTTP `400`
  - Ser reutilizado para falhas de validacao e parsing

- Error handler minimo
  - Interceptar erros de desserializacao e validacao
  - Retornar `400` com corpo padronizado

- `CalculatorService`
  - Permanecer responsavel apenas pela soma
  - Nao absorver logica de borda HTTP

## Data flow

- O projeto passa a compilar e executar com Java `25` e Spring Boot `4`
- O cliente envia `POST /calculator/sum`
- O Spring desserializa o JSON em `SumRequest`
- Se a entrada for valida, o controller chama `CalculatorService`
- O service calcula a soma
- A API retorna `200` com `SumResponse`
- Se houver corpo ausente, JSON malformado, campo ausente ou tipo invalido:
  - o Spring gera excecao de binding ou validacao
  - o handler converte isso para `ErrorResponse`
  - a API retorna `400 Bad Request` com corpo padronizado
- Em paralelo, `AGENTS.md` e `TESTING_CONVENTIONS.md` passam a refletir a stack nova oficial

## Security and observability requirements

- Nao introduzir autenticacao ou autorizacao nesta remediacao
- O corpo de erro nao deve expor stack trace ou detalhes internos
- A migracao para Spring Boot `4` deve preservar o comportamento funcional do endpoint existente
- Os logs padrao do framework continuam suficientes para esta feature
- A principal confianca operacional continua sendo a suite automatizada de testes
- A migracao de stack deve ser validada por regressao completa

## Key trade-offs and alternatives considered

- Corrigir documentacao para a stack atual vs migrar stack para a desejada
  - Escolha proposta: migrar para Java `25` e Spring Boot `4`
  - Motivo: esta e a direcao explicitamente escolhida para a remediacao

- `MockMvc` vs `MockMvcTester`
  - Escolha proposta: adotar `MockMvcTester`
  - Motivo: alinhamento com Spring Boot `4` e com a documentacao alvo

- Handler customizado minimo vs resposta padrao do framework
  - Escolha proposta: handler minimo padronizando `400`
  - Motivo: fecha o gap de contrato sem ampliar a arquitetura

- Suportar `NaN`/`Infinity` vs declarar fora do contrato
  - Escolha proposta: declarar como fora do contrato suportado
  - Motivo: mantem a remediacao enxuta mesmo com a migracao de stack

- Risco principal
  - A migracao para Spring Boot `4` pode exigir pequenos ajustes de imports, dependencias e testes
  - Esse risco deve ser tratado explicitamente no Step 03

## High-level test scenario map

- Happy path
  - O endpoint continua retornando `200` com `result` para payload valido apos a migracao
  - A logica de soma continua inalterada

- Failure paths
  - Campo `firstAddend` ausente retorna `400` com corpo padronizado
  - Campo `secondAddend` ausente retorna `400` com corpo padronizado
  - JSON malformado retorna `400` com corpo padronizado
  - Tipo invalido retorna `400` com corpo padronizado

- Migration validation paths
  - O projeto compila com Java `25`
  - O projeto sobe com Spring Boot `4`
  - Os testes de integracao usam a abordagem oficial da stack nova
  - A documentacao reflete a stack migrada e os exemplos corretos

- Edge-case families
  - `NaN`, `Infinity` e `-Infinity` ficam explicitamente fora do contrato suportado
  - O contrato de erro permanece consistente apos a migracao
  - A remediacao nao deve alterar o contrato de sucesso do endpoint
