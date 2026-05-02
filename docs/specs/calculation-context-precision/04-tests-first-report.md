# 04 — Tests First Report

## Test Files Created

- `src/test/java/com/example/demo/domain/CalculationContextTest.java`

## Test Files Updated

- `src/test/java/com/example/demo/domain/CalculatorServiceDivideTest.java`
- `src/test/java/com/example/demo/domain/CalculatorServicePowerTest.java`
- `src/test/java/com/example/demo/domain/CalculatorServiceRootTest.java`
- `src/test/java/com/example/demo/controller/CalculatorDivideControllerTest.java`
- `src/test/java/com/example/demo/controller/CalculatorPowerControllerTest.java`
- `src/test/java/com/example/demo/controller/CalculatorRootControllerTest.java`
- `src/test/java/com/example/demo/controller/CalculatorEvaluateControllerTest.java`
- `src/test/java/com/example/demo/controller/OpenApiDocumentationTest.java`

## Acceptance Criteria -> Tests Mapping

- Defaults `scale = 10`, `HALF_UP`: `CalculationContextTest`.
- `/divide` com contexto e arredondamento: testes unitarios e HTTP.
- `scale = 0` e `scale = 17`: testes HTTP em `/divide`.
- `roundingMode` invalido: teste HTTP em `/divide`.
- `/power` e `/root` arredondam resultado final: testes unitarios e HTTP.
- `/evaluate` com contexto raiz/local/heranca: testes HTTP em `CalculatorEvaluateControllerTest`.
- OpenAPI com `context` e enum `roundingMode`: `OpenApiDocumentationTest`.

## Test Commands Executed

```bash
./mvnw -Dtest=CalculationContextTest,CalculatorServiceDivideTest,CalculatorServicePowerTest,CalculatorServiceRootTest test
```

Resultado: `BUILD FAILURE`, com `20` testes executados, `1` failure e `5` errors.

Falhas esperadas nos novos cenarios por stubs:

- `CalculationContext defaults are not implemented yet`
- `CalculationContext-aware divide is not implemented yet`
- `CalculationContext-aware power is not implemented yet`
- `CalculationContext-aware root is not implemented yet`

```bash
./mvnw -Dtest=CalculatorDivideControllerTest,CalculatorPowerControllerTest,CalculatorRootControllerTest,CalculatorEvaluateControllerTest,OpenApiDocumentationTest test
```

Resultado: `BUILD FAILURE`, com `40` testes executados e `10` failures.

Falhas esperadas por contrato ainda nao implementado:

- `/divide` com `context` retorna `400` em `1 / 3` em vez de `200`.
- `scale = 0`, `scale = 17` e `roundingMode = INVALID` ainda retornam `200`, porque `context` e ignorado.
- `/power` e `/root` retornam `1.4142135623730951` em vez de `1.4142`.
- `/evaluate` nao aplica contexto raiz/local.
- OpenAPI ainda nao contem `"context"` nem enum `roundingMode`.

## Failing Results Summary

- Testes de dominio compilaram e falharam nos stubs minimos com `UnsupportedOperationException`.
- Testes de integracao compilaram e falharam por ausencia de suporte HTTP ao `context`, validacao de `scale`, propagacao em `/evaluate`, arredondamento em `power`/`root` e documentacao OpenAPI.

## Red-Phase Confirmation

Red phase confirmada: os testes foram escritos antes da implementacao, os comandos falharam de forma auditavel, e nenhuma logica de producao foi implementada alem dos stubs minimos autorizados para compilacao.
