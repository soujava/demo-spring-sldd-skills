# 99 — Existing Codebase Understanding

## Repository Structure Overview

O projeto e uma aplicacao Spring Boot 4.0.5 com Java 25 e Maven.

Estrutura relevante:

- `src/main/java/com/example/demo/domain`: regras de dominio da calculadora.
- `src/main/java/com/example/demo/controller`: controlador REST e tratamento global de erros.
- `src/main/java/com/example/demo/controller/api`: DTOs de request/response e tipos de API.
- `src/test/java/com/example/demo/domain`: testes unitarios de logica de negocio.
- `src/test/java/com/example/demo/controller`: testes de integracao HTTP com `@SpringBootTest`, `@AutoConfigureMockMvc` e `MockMvcTester`.
- `docs/specs`: artefatos SLDD por workflow.

## Architecture Summary

A aplicacao possui uma separacao simples entre borda HTTP e dominio.

`CalculatorController` recebe payloads HTTP, valida DTOs com Bean Validation e delega para `CalculatorService`.

`CalculatorService` concentra as operacoes matematicas:

- `sum`
- `subtract`
- `multiply`
- `divide`
- `power`
- `root`
- `evaluate`

O modelo de expressao atual usa:

- `Expression` como sealed interface.
- `Literal` como record.
- `BinaryOperation` como record.
- `Operator` como enum de dominio.

Na API, `/calculator/evaluate` usa DTOs polimorficos Jackson:

- `ExpressionDto`
- `LiteralDto`
- `BinaryOperationDto`
- `OperatorDto`

O mapeamento de DTO para dominio acontece no controller via pattern matching.

## Conventions to Preserve

- Testes de dominio devem ser JUnit puro, sem contexto Spring.
- Testes de borda devem usar contexto Spring completo com `MockMvcTester`.
- Erros HTTP usam `ErrorResponse(error, message)`.
- Validacao de request invalido hoje retorna `400 Bad Request`.
- `ArithmeticException` retorna `400 Bad Request`.
- `NumericOverflowException` retorna `422 Unprocessable Entity`.
- Payloads antigos devem continuar validos quando nao enviarem `context`.
- DTOs devem favorecer inferencia OpenAPI, especialmente com enum para `roundingMode`.

## Integration Points

- `DivideRequest`, `PowerRequest`, `RootRequest` devem aceitar `context` opcional.
- `EvaluateRequest` deve aceitar `context` raiz opcional.
- `BinaryOperationDto` deve aceitar `context` local opcional.
- Novo DTO de contexto deve ficar em `controller/api`.
- `CalculationContext` deve ficar em `com.example.demo.domain`.
- `CalculatorService` deve receber contexto nas operacoes `divide`, `power`, `root` e na avaliacao de expressoes.
- `ApiErrorHandler` pode precisar melhorar mensagens de validacao/desserializacao para contexto invalido.
- `OpenApiDocumentationTest` pode precisar cobrir enum de `roundingMode` se o Step 03 definir esse contrato como verificavel.

## Risks and Unknowns

- `BigDecimal.divide` com `RoundingMode.UNNECESSARY` pode lancar `ArithmeticException` quando arredondamento for necessario.
- `power` e `root` usam `Math.pow`; o contexto deve arredondar o resultado final, nao substituir o calculo base.
- O handler atual retorna `"Invalid request body"` para validacao generica, mas o Step 01 exige mensagem clara para contexto invalido.
- `/evaluate` hoje converte a arvore inteira para dominio antes de avaliar; contexto local exigira carregar contexto junto da expressao ou alterar a assinatura de avaliacao.
- Campos extras sao ignorados atualmente em pelo menos `/divide`; o novo `context` nao deve quebrar esse comportamento.
- `scale` precisa de validacao `1..16` em todos os pontos onde contexto pode aparecer.

## Context to Carry Into Steps 02-06

- Nao refatorar `Expression` para records de operacao neste workflow.
- Nao introduzir Strategy pattern.
- Preservar os endpoints e payloads existentes sem `context`.
- Contexto so entra nos contratos de `/divide`, `/power`, `/root` e `/evaluate`.
- Em `/evaluate`, o contexto deve ter dois niveis: raiz e local por operacao.
- Heranca de contexto e campo a campo e segue o contexto mais proximo.
- Testes devem cobrir dominio e borda separadamente conforme `TESTING_CONVENTIONS.md`.
- A implementacao deve manter OpenAPI inferivel para `roundingMode` como enum.
