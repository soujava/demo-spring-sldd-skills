# 05 — Minimal Implementation Report

## Production Files Changed

- `src/main/java/com/example/demo/domain/CalculationContext.java`
- `src/main/java/com/example/demo/domain/CalculatorService.java`
- `src/main/java/com/example/demo/controller/api/CalculationContextDto.java`
- `src/main/java/com/example/demo/controller/api/DivideRequest.java`
- `src/main/java/com/example/demo/controller/api/PowerRequest.java`
- `src/main/java/com/example/demo/controller/api/RootRequest.java`
- `src/main/java/com/example/demo/controller/api/EvaluateRequest.java`
- `src/main/java/com/example/demo/controller/api/BinaryOperationDto.java`
- `src/main/java/com/example/demo/controller/CalculatorController.java`
- `src/main/java/com/example/demo/controller/ApiErrorHandler.java`

## Implementation Notes (Minimal Scope)

- `CalculationContext.defaults()` retorna `scale = 10` e `HALF_UP`.
- `CalculationContextDto` usa `@Min(1)`, `@Max(16)` e `RoundingMode` enum.
- `/divide`, `/power`, `/root` aceitam `context` opcional.
- `/evaluate` aceita contexto raiz e contexto local por operacao.
- A heranca de contexto e resolvida campo a campo no controller.
- `DIVIDE` usa `BigDecimal.divide(scale, roundingMode)`.
- `POWER` e `ROOT` mantem `Math.pow` e arredondam o resultado final com `BigDecimal.setScale`.
- O handler preserva `"Invalid request body"` para validacoes antigas e retorna mensagens claras para `scale` e `roundingMode`.

## Test Commands Executed

```bash
./mvnw -Dtest=CalculationContextTest,CalculatorServiceDivideTest,CalculatorServicePowerTest,CalculatorServiceRootTest test
```

Resultado: `BUILD SUCCESS`, `20` testes, `0` falhas.

```bash
./mvnw -Dtest=CalculatorDivideControllerTest,CalculatorPowerControllerTest,CalculatorRootControllerTest,CalculatorEvaluateControllerTest,OpenApiDocumentationTest test
```

Resultado: `BUILD SUCCESS`, `40` testes, `0` falhas.

```bash
./mvnw test
```

Resultado: `BUILD SUCCESS`, `127` testes, `0` falhas.

## Passing Results Summary

- Testes unitarios direcionados passaram.
- Testes de integracao e OpenAPI direcionados passaram.
- Suite completa passou sem regressao detectada.

## Assumptions and Constraints

- Nenhuma dependencia nova foi adicionada.
- `POWER` e `ROOT` continuam usando `Math.pow`; o contexto so arredonda o resultado final.
- O modelo de `Expression` nao foi refatorado.
- Endpoints que nao estavam no escopo continuam sem `context`.

## Test Integrity Confirmation (No Test Modifications)

Os testes foram escritos/alterados no Step 04 antes da implementacao. Durante o Step 05, nenhuma alteracao adicional foi feita nos testes.
