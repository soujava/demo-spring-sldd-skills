# Step 05 — Minimal Implementation Report

## Production Changes

### 1. Expression.java
- Removed all inner classes (Literal, Add, Subtract, Multiply, Divide, Power, Root) that shadowed the existing top-level implementations
- Kept interface with `Result evaluate()` method, `Result` inner interface, and static factory methods (`literal`, `add`, `subtract`, `multiply`, `divide`, `power`, `root`) that delegate to top-level record classes
- Factory methods now create instances of `ExpressionLiteral`, `Add`, `Subtract`, `Multiply`, `Divide`, `Power`, `Root` from the same package

### 2. ExpressionLiteral.java
- Added `implements Expression.Result` to satisfy the `Result` return type contract from `Expression.evaluate()`

### 3. Subtract.java
- Changed from plain `double` subtraction to `BigDecimal`-based subtraction for precision (matching Add and Multiply implementations)
- `5.0 - 3.2` now correctly yields `1.8` instead of `1.7999999999999998`

### 4. ComposeExpressionValidator.java
- Changed operation name matching from lowercase (`"add"`, `"subtract"`, etc.) to uppercase (`"ADD"`, `"SUBTRACT"`, etc.) to match the JSON test payloads
- Added `"POWER"` and `"ROOT"` operation support to the switch statement

### 5. ExpressionOperation.java
- Added `POWER` and `ROOT` enum values

## Verification

- Test command: `./mvnw test`
- Result: **Tests run: 111, Failures: 0, Errors: 0, Skipped: 0** — BUILD SUCCESS
- No test files were modified

## Files Changed

| File | Change |
|------|--------|
| `src/main/java/com/example/demo/domain/expression/Expression.java` | Removed inner classes; kept interface with factory methods |
| `src/main/java/com/example/demo/domain/expression/ExpressionLiteral.java` | Added `implements Expression.Result` |
| `src/main/java/com/example/demo/domain/expression/Subtract.java` | Changed to BigDecimal-based subtraction |
| `src/main/java/com/example/demo/controller/validation/ComposeExpressionValidator.java` | Uppercase operations; added POWER/ROOT |
| `src/main/java/com/example/demo/domain/expression/ExpressionOperation.java` | Added POWER, ROOT values |