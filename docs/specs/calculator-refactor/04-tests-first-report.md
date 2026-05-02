# Step 04 — Tests First Report: Calculator Operations Refactor

## Test Files Created

| Arquivo | Tipo | Status |
|---|---|---|
| `src/test/java/.../domain/DivideOperationTest.java` | Unitário (JUnit 5 puro) | Novo |
| `src/test/java/.../domain/SubtractOperationTest.java` | Unitário (JUnit 5 puro) | Novo |
| `src/test/java/.../domain/OperationRegistryTest.java` | Unitário (JUnit 5 puro) | Novo |
| `src/test/java/.../domain/CalculatorServiceDivideTest.java` | Unitário (JUnit 5 puro) | Modificado (novos cenários) |
| `src/test/java/.../domain/CalculatorServiceEvaluateTest.java` | Integração (@SpringBootTest) | Modificado (novos cenários) |
| `src/test/java/.../controller/CalculatorDivideControllerTest.java` | Integração (@SpringBootTest + MockMvcTester) | Modificado (novos cenários) |
| `src/test/java/.../controller/CalculatorEvaluateControllerTest.java` | Integração (@SpringBootTest + MockMvcTester) | Modificado (novos cenários) |

## Acceptance Criteria -> Tests Mapping

| Step 01 AC | Teste(s) |
|---|---|
| AC1 — subtract com BigDecimal consistente | `SubtractOperationTest`, `CalculatorServiceSubtractionTest` |
| AC2 — divide default scale/rounding | `DivideOperationTest.divideWithDefaultScale()`, `CalculatorServiceDivideTest.divideWithDefaultScale()` |
| AC3 — divide com scale/roundingMode explícito | `DivideOperationTest.divideWithExplicitScaleAndRounding()`, `DivideOperationTest.divideWithScaleZero()`, `CalculatorDivideControllerTest.returnsDivideWithExplicitScaleAndRounding()` |
| AC4 — divisão por zero → 400 | `DivideOperationTest.divideByZeroThrowsException()`, `CalculatorServiceDivideTest.throwsArithmeticExceptionWhenDivisorIsZero()`, `CalculatorDivideControllerTest.returnsBadRequestWhenDivisorIsZero()` |
| AC5 — evaluate com Divide sem ctx → default | `CalculatorServiceEvaluateTest.evaluate_DivideWithoutContext_UsesDefaultScale()`, `CalculatorEvaluateControllerTest.evaluate_DivideWithoutContext_UsesDefaultScale()` |
| AC6 — regressão zero | `CalculatorServiceTest`, `CalculatorServiceMultiplyTest`, `CalculatorServicePowerTest`, `CalculatorServiceRootTest` (mantidos) |
| AC7 — extensibilidade | `OperationRegistryTest` |

## Test Commands Executed

```
./mvnw test
```

## Failing Results Summary

```
Tests run: 128, Failures: 41, Errors: 42, Skipped: 0

Errors (42) — stubs de produção lançam `Error("not implemented")`:
  CalculatorServiceTest.*          → todas as 8 operações SUM falham
  CalculatorServiceMultiplyTest.*  → todas as 8 operações MULTIPLY falham
  CalculatorServiceSubtractionTest.*  → todas as 6 operações SUBTRACT falham
  CalculatorDivideControllerTest.* → 2 novos cenários de divide com scale
  CalculatorEvaluateControllerTest.* → 2 novos cenários de evaluate com context
  DivideOperationTest.*            → todos os 4 cenários
  SubtractOperationTest.*          → ambos os cenários
  OperationRegistryTest.*          → lookup retorna instância mas apply falha

Failures (41) — stubs propagam erro via CalculatorService.evaluate() e controllers:
  CalculatorServiceDivideTest.*    → 2 novos cenários + 5 existentes
  CalculatorServicePowerTest.*     → todos os 3 cenários
  CalculatorServiceRootTest.*      → todos os 5 cenários
  CalculatorServiceEvaluateTest.*  → 2 novos cenários + evaluate_DivisionByZero
  CalculatorDivideControllerTest.* → cenários existentes que chamam divide()
  Diversos controllers             → via ApplicationContext (stub propagation)
```

## Red-Phase Confirmation

**Red phase confirmado.** Todas as stubs de produção lançam `Error("not implemented")` imediatamente. Nenhuma lógica de negócio, validação, placeholder return ou delegating call foi implementada. Os 128 testes compilam (estrutura correta) e falham em execução (comportamento não implementado).
