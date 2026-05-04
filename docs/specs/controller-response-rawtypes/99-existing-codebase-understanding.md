# Existing Codebase Understanding

## Repository Structure Overview

- Projeto Spring Boot Java 25/Maven.
- Controller alvo em `src/main/java/com/example/demo/controller/CalculatorController.java`.
- DTOs de API em `src/main/java/com/example/demo/controller/api`.
- Testes HTTP em `src/test/java/com/example/demo/controller`.

## Architecture Summary

`CalculatorController` recebe DTOs de request, converte entradas para expressoes de dominio e retorna DTOs de response via `ResponseEntity.ok(...)`. `ApiErrorHandler` ja usa `ResponseEntity<ErrorResponse>` tipado para respostas de erro.

## Conventions to Preserve

- Manter testes de borda com Spring Boot e MockMvcTester.
- Nao misturar teste unitario de dominio com contexto Spring.
- Manter implementacao minima e Java moderno.

## Integration Points

- Endpoints: `/calculator/evaluate`, `/calculator/sum`, `/calculator/subtract`, `/calculator/multiply`, `/calculator/divide`, `/calculator/power`, `/calculator/root`.
- DTOs de resposta: `EvaluateResponse`, `SumResponse`, `SubtractResponse`, `MultiplyResponse`, `DivideResponse`, `PowerResponse`, `RootResponse`.

## Risks and Unknowns

- A mudanca deve ser puramente de assinatura generica.
- Qualquer alteracao no JSON ou status HTTP seria regressao.

## Context to Carry Into Steps 02-06

- Corrigir somente os retornos raw em `CalculatorController`.
- Verificar com busca por `ResponseEntity` raw e `./mvnw test`.
