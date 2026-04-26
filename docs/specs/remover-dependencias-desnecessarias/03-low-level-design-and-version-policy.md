# 03 - Low-Level Design and Version Policy

## API Contracts
N/A (Nenhuma alteração em contratos de API REST).

## Data Models
N/A (Nenhuma alteração em modelos de dados).

## Error Model
N/A (Nenhuma alteração no modelo de erro).

## Test Strategy
A estratégia baseia-se em testes de regressão automatizados. Como o objetivo é remover dependências sem alterar o comportamento, os testes existentes são a nossa rede de segurança.
- Execução de `mvn test` antes da mudança (Baseline).
- Execução de `mvn test` após a remoção (Validation).

## Test Scenario Catalog
- **TS1:** Compilação do projeto (`mvn clean compile`).
- **TS2:** Execução de testes de unidade do domínio (`CalculatorServiceTest`).
- **TS3:** Execução de testes de integração de controller (`CalculatorControllerTest`).

## Dependency and Version Policy
- **Remoção:** `spring-boot-starter-webmvc-test`.
- **Justificativa:** Esta dependência não é um starter oficial e suas funcionalidades (MockMvc) já são providas pelo `spring-boot-starter-test` (que é o padrão do Spring Boot).
- **Versão:** Manter Spring Boot 4.0.5 e Java 25 conforme definido no `pom.xml`.

## Ordered Implementation Plan
1. Executar `mvn test` para garantir que o estado atual está estável.
2. Abrir o `pom.xml`.
3. Localizar e remover o bloco `<dependency>` referente ao `spring-boot-starter-webmvc-test`.
4. Executar `mvn clean test`.
5. Verificar se houve falhas de compilação ou de testes.
