# 05 — Minimal Implementation Report: Endpoint Raiz

## Production Files Changed
- `src/main/java/com/example/demo/controller/api/RootRequest.java`
- `src/main/java/com/example/demo/controller/api/RootResponse.java`
- `src/main/java/com/example/demo/controller/CalculatorController.java`
- `src/main/java/com/example/demo/domain/CalculatorService.java`

## Implementation Notes (Minimal Scope)
- Criado `RootRequest` com `@NotNull Double radicand` e `@NotNull Double index`.
- Criado `RootResponse` com `double result`.
- Adicionado `POST /calculator/root` em `CalculatorController`.
- Adicionado `CalculatorService.root(double radicand, double index)`.
- `index == 0.0` lanca `ArithmeticException`.
- Resultado `Infinity` lanca `NumericOverflowException`.
- Resultado `NaN` lanca `ArithmeticException`.
- `ApiErrorHandler` existente foi reutilizado sem alteracoes.

## Test Commands Executed
```bash
./mvnw test
```

## Passing Results Summary
```text
Tests run: 101, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Assumptions and Constraints
- A raiz e calculada como `Math.pow(radicand, 1.0 / index)`.
- Nao ha suporte a numeros complexos.
- Raiz impar de negativos nao recebeu tratamento manual nesta implementacao minima.
- Nenhuma dependencia Maven foi adicionada.

## Test Integrity Confirmation (No Test Modifications)
Confirmado: os testes criados no Step 04 nao foram modificados durante o Step 05.
