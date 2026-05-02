# Step 05 — Minimal Implementation Report: Calculator Operations Refactor

## Production Files Changed

| Arquivo | Mudança |
|---|---|
| `SumOperation.java` | `BigDecimal.valueOf(a).add(BigDecimal.valueOf(b)).doubleValue()` |
| `SubtractOperation.java` | `BigDecimal.valueOf(a).subtract(BigDecimal.valueOf(b)).doubleValue()` |
| `MultiplyOperation.java` | `BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(b)).doubleValue()` |
| `DivideOperation.java` | `apply()` delega para `apply(left, right, DEFAULT)`. `apply(left, right, ctx)` valida divisor e usa `BigDecimal.divide(b, scale, roundingMode)` |
| `PowerOperation.java` | `Math.pow()` + validação overflow/NaN |
| `RootOperation.java` | `Math.pow(left, 1/right)` + validação overflow/NaN |

Nenhum outro arquivo de produção ou teste foi alterado.

## Implementation Notes (Minimal Scope)

- Apenas as 6 classes `*Operation` foram alteradas — de `throw new Error("not implemented")` para a lógica real.
- `Operation`, `OperationRegistry`, `CalculationContext`, `BinaryOperation`, `DivideRequest`, `BinaryOperationDto`, `CalculationContextDto`, `CalculatorService`, `CalculatorController` permanecem como estavam no Step 04 (já estavam corretos).
- Nenhuma dependência nova foi adicionada.
- A implementação segue exatamente o Step 03 design: híbrido BigDecimal (sum, subtract, multiply, divide) + Math.pow (power, root).

## Test Commands Executed

```
./mvnw test
```

## Passing Results Summary

```
Tests run: 128, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Todos os 128 testes passam — incluindo os 41 que falhavam e os 42 com erro no Step 04.

## Assumptions and Constraints

- `DivideOperation.apply(left, right)` sem contexto usa `CalculationContext.DEFAULT` (scale=10, HALF_UP)
- `DivideOperation.apply(left, right, ctx)` usa scale e roundingMode fornecidos
- Divisão por zero lança `ArithmeticException` independente do contexto
- `SubtractOperation` usa BigDecimal, corrigindo a assimetria anterior com `double - double`

## Test Integrity Confirmation (No Test Modifications)

Nenhum arquivo de teste foi modificado durante o Step 05. Os testes são exatamente os mesmos do Step 04.
