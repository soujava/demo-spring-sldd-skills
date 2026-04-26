# 04 — Tests First Report (Red phase)

## Test Files Created
- `src/test/java/com/example/demo/domain/CalculatorServicePowerTest.java`
- `src/test/java/com/example/demo/controller/CalculatorPowerControllerTest.java`

## Acceptance Criteria -> Tests Mapping
- **Sucesso (2^3=8):** `CalculatorServicePowerTest.shouldCalculatePowerCorrectly`, `CalculatorPowerControllerTest.shouldReturnOkOnSuccess`
- **Overflow (422):** `CalculatorServicePowerTest.shouldThrowExceptionOnOverflow`, `CalculatorPowerControllerTest.shouldReturnUnprocessableEntityOnOverflow`
- **Resultado Imaginário (400):** `CalculatorServicePowerTest.shouldThrowExceptionOnImaginaryResult`, `CalculatorPowerControllerTest.shouldReturnBadRequestOnArithmeticException`

## Test Commands Executed
```bash
./mvnw test -Dtest=CalculatorServicePowerTest,CalculatorPowerControllerTest
```

## Failing Results Summary
- **Unit Tests:** 3 falhas (1 Error por Not Implemented, 2 Failure por tipo de exceção inesperada).
- **Integration Tests:** 3 falhas (Status 404 em vez de 200/422/400).

## Red-Phase Confirmation
Confirmado: Todos os novos testes falharam conforme o esperado, validando a ausência da implementação e a corretude dos cenários de teste.
