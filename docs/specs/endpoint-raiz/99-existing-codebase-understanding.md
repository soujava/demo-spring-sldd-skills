# 99 — Existing Codebase Understanding and Context Summary

## Repository Structure Overview
- Projeto Maven com Spring Boot 4.0.5 e Java 25.
- Entrada da aplicação: `com.example.demo.DemoApplication`.
- Controller principal: `com.example.demo.controller.CalculatorController`.
- Serviço de domínio: `com.example.demo.domain.CalculatorService`.
- DTOs HTTP em `com.example.demo.controller.api`.
- Testes de integração em `src/test/java/com/example/demo/controller/`.
- Testes unitários de domínio em `src/test/java/com/example/demo/domain/`.

## Architecture Summary
- Arquitetura em camadas simples: Controller -> Service -> Domain.
- Endpoints existentes seguem `POST /calculator/<operation>`.
- Requests e responses são `record`s separados por operação.
- Validação de campos obrigatórios usa `@NotNull` nos DTOs.
- Erros HTTP são centralizados em `ApiErrorHandler`.

## Conventions to Preserve
- Testes unitários de lógica de negócio usam JUnit puro, sem contexto Spring.
- Testes de integração HTTP usam `@SpringBootTest` + `@AutoConfigureMockMvc` de `org.springframework.boot.webmvc.test.autoconfigure`.
- Testes de integração usam `MockMvcTester`.
- Endpoint de soma é a principal referência de contrato HTTP básico.
- Endpoint de potenciação é referência para `Math.pow`, `NaN`, `Infinity`, `ArithmeticException` e `NumericOverflowException`.

## Integration Points
- Adicionar operação em `CalculatorService`.
- Adicionar método `POST /calculator/root` em `CalculatorController`.
- Criar DTOs `RootRequest` e `RootResponse`.
- Usar `ApiErrorHandler` existente para erros de validação, parsing, aritmética e overflow.
- Adicionar testes unitários para domínio e testes de integração para borda HTTP.

## Risks and Unknowns
- `Math.pow(radicand, 1.0 / index)` retorna `NaN` para alguns casos matematicamente inválidos.
- Raiz de radicando negativo com índice inteiro ímpar exigirá cuidado se for suportada, porque `Math.pow(-27.0, 1.0 / 3.0)` pode produzir `NaN`.
- É necessário definir se o escopo inicial suporta apenas os casos básicos ou também raiz ímpar de negativos.
- Comparações de `double` podem exigir delta nos testes unitários.

## Context to Carry Into Steps 02-06
- Preservar separação entre lógica de negócio e contrato HTTP.
- Não misturar asserts de lógica de negócio nos testes de integração.
- Seguir nomes e estrutura dos endpoints existentes.
- Reutilizar o modelo de erro já existente em vez de criar handler novo.
