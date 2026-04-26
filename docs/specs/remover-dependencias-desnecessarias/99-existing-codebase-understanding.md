# 99 - Existing Codebase Understanding

## Repository Structure Overview
O projeto é uma aplicação Spring Boot padrão em Java 25 (usando Maven).
- `src/main/java`: Contém a lógica de negócio (`domain`) e a camada de entrada REST (`controller`).
- `src/test/java`: Contém testes de unidade para o domínio e testes de integração usando `MockMvcTester` para os controllers.
- `pom.xml`: Gerenciador de dependências Maven.

## Architecture Summary
A aplicação segue uma arquitetura em camadas simples:
- **Web Layer:** `CalculatorController` expõe endpoints POST para operações matemáticas.
- **Domain Layer:** `CalculatorService` contém a lógica de cálculo.
- **API Models:** Records em `com.example.demo.controller.api` definem os contratos de entrada e saída.

## Conventions to Preserve
- Uso de `Records` para DTOs.
- Uso de `MockMvcTester` (introduzido no Spring Boot 3.4+) para testes de controller.
- Spring Boot 4.0.5 (versão bleeding edge configurada no projeto).
- Java 25.

## Integration Points
- Exposição de API REST via HTTP.
- Spring Boot Starters (`web`, `validation`, `test`).

## Risks and Unknowns
- **Dependência Redundante:** O `pom.xml` declara `spring-boot-starter-webmvc-test`, mas o projeto já usa `spring-boot-starter-test`. Além disso, `spring-boot-starter-webmvc-test` não é um starter oficial do Spring Boot (o oficial é apenas `spring-boot-starter-test` que já inclui MockMvc).
- **Impacto da Remoção:** Verificar se o `MockMvcTester` depende especificamente de alguma configuração trazida por dependências extras.

## Context to Carry Into Steps 02-06
- O foco principal de limpeza é o `pom.xml`.
- A validação será feita através da execução dos testes existentes (`mvn test`).
