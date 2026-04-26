# 04 — Tests First Report: Endpoint Raiz

## Test Files Created
- `src/test/java/com/example/demo/domain/CalculatorServiceRootTest.java`
- `src/test/java/com/example/demo/controller/CalculatorRootControllerTest.java`

## Acceptance Criteria -> Tests Mapping
- Raiz quadrada valida: `CalculatorServiceRootTest.shouldCalculateSquareRoot` e `CalculatorRootControllerTest.shouldReturnSquareRootForValidPayload`.
- Raiz cubica valida: `CalculatorServiceRootTest.shouldCalculateCubeRoot` e `CalculatorRootControllerTest.shouldReturnCubeRootForValidPayload`.
- Raiz de zero: `CalculatorServiceRootTest.shouldReturnZeroWhenRadicandIsZero`.
- Indice zero: `CalculatorServiceRootTest.shouldThrowArithmeticExceptionWhenIndexIsZero` e `CalculatorRootControllerTest.shouldReturnBadRequestWhenIndexIsZero`.
- Operacao indefinida/imaginaria: `CalculatorServiceRootTest.shouldThrowArithmeticExceptionWhenResultIsUndefinedOrImaginary`.
- Campo `radicand` ausente: `CalculatorRootControllerTest.shouldReturnBadRequestWhenRadicandIsMissing`.
- Campo `index` ausente: `CalculatorRootControllerTest.shouldReturnBadRequestWhenIndexIsMissing`.
- JSON malformado: `CalculatorRootControllerTest.shouldReturnBadRequestWhenJsonIsMalformed`.
- Tipo incompativel: `CalculatorRootControllerTest.shouldReturnBadRequestWhenRadicandHasInvalidType`.

## Test Commands Executed
```bash
./mvnw test
```

## Failing Results Summary
O comando falhou em `testCompile`, antes da execucao da suite, porque a implementacao ainda nao existe.

Falhas principais:
```text
cannot find symbol: class RootResponse
cannot find symbol: method root(double,double)
```

O Maven encerrou com:
```text
BUILD FAILURE
COMPILATION ERROR
```

## Red-Phase Confirmation
Confirmado: os testes foram escritos antes da implementacao e falham pelo motivo esperado. Nenhum arquivo de producao foi alterado no Step 04.
