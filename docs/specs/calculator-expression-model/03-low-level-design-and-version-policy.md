# API Contracts
All existing HTTP contracts remain unchanged:
- **POST /calculator/sum**
  - Request: `{"firstAddend":<double>,"secondAddend":<double>}`
  - Response (200): `{"result":<double>}`
  - Errors: 400 for validation failures (Bean Validation), 500 for arithmetic exceptions (same messages/types as before).
- **POST /calculator/subtract**
  - Request: `{"minuend":<double>,"subtrahend":<double>}`
  - Response: `{"result":<double>}`
- **POST /calculator/multiply**
  - Request: `{"multiplicand":<double>,"multiplier":<double>}`
  - Response: `{"result":<double>}`
- **POST /calculator/divide**
  - Request: `{"dividend":<double>,"divisor":<double>}`
  - Response: `{"result":<double>}`
  - Errors: 500 with message "Division by zero" when divisor == 0.0; also 500 for overflow/invalid results.
- **POST /calculator/power**
  - Request: `{"base":<double>,"exponent":<double>}`
  - Response: `{"result":<double>}`
  - Errors: 500 for overflow (NumericOverflowException) or invalid operation (ArithmeticException) as in original service.
- **POST /calculator/root**
  - Request: `{"radicand":<double>,"index":<double>}`
  - Response: `{"result":<double>}`
  - Errors: 500 for index == 0.0 (ArithmeticException) or overflow/invalid results.
- **POST /calculator/compose**
  - Request: raw string (the expression)
  - Response: `{"result":<double>}`
  - Errors: 400 for invalid expression syntax (from ComposeExpressionValidator); 500 for arithmetic errors during evaluation.

No changes to request/response DTO classes; they remain as `record` types in `controller/api/`.

# Data Models
- **Expression** (`src/main/java/com/example/demo/domain/expression/Expression.java`): sealed interface permitting the literal and operation records.
  - Method: `ExpressionLiteral evaluate()`.
  - Static factories:
    - `static Expression literal(double value)`
    - `static Expression add(Expression left, Expression right)`
    - `static Expression subtract(Expression left, Expression right)`
    - `static Expression multiply(Expression left, Expression right)`
    - `static Expression divide(Expression left, Expression right)`
    - `static Expression power(Expression base, Expression exponent)`
    - `static Expression root(Expression radicand, Expression index)`
- **ExpressionLiteral** (`ExpressionLiteral.java`): `public record ExpressionLiteral(double value) implements Expression`.
  - `evaluate()` returns `this`.
- **Add**, **Subtract**, **Multiply**, **Divide**, **Power**, **Root**: each a `public record` with two `Expression` components (named according to operation, e.g., `Add` has `Expression left`, `Expression right`).
  - Each implements `evaluate()`:
    - Uses `BigDecimal.valueOf(left.evaluate().value()).add(BigDecimal.valueOf(right.evaluate().value())).doubleValue()` for add and multiply (to preserve original precision handling).
    - For subtract: `left.evaluate().value() - right.evaluate().value()`.
    - For divide: after checking divisor == 0, uses `BigDecimal.valueOf(left.evaluate().value()).divide(BigDecimal.valueOf(right.evaluate().value()))` (note: original service used `divide()` without scale, which may throw ArithmeticException for non-terminating decimal; we preserve that behavior).
    - For power: `Math.pow(left.evaluate().value(), right.evaluate().value())` with checks for infinite/NaN as in original service.
    - For root: similar to power, with `Math.pow(radicand, 1.0 / index)` and checks.
- **ComposeExpressionValidator**: unchanged except that when it needs to create a leaf node for a number, it calls `Expression.literal(number)` instead of `new ExpressionLiteral(number)`. When building operation nodes, it uses the appropriate factory (e.g., `Expression.add(leftExpr, rightExpr)`).
- **CalculatorService**: removed.

# Error Model
- All arithmetic exceptions thrown by `Expression.evaluate()` are exactly the same types and messages as thrown by the original `CalculatorService` methods:
  - `ArithmeticException` with message "Division by zero" for division by zero.
  - `ArithmeticException` with message "Invalid operation: result is undefined or imaginary" for invalid operations (e.g., root with index zero, power resulting in NaN).
  - `NumericOverflowException` with message "Numeric overflow: result is too large" for overflow cases (power, root, add, multiply? Actually add and multiply use BigDecimal which does not overflow to infinity; but original service used BigDecimal for add/multiply and returned doubleValue(); BigDecimal can overflow to Double.INFINITY when too large? We'll keep same checks: after computing doubleValue(), if infinite throw NumericOverflowException, if NaN throw ArithmeticException, mirroring original service's approach for consistency.)
- Validation errors (bean validation on DTOs, parse errors in ComposeExpressionValidator) result in 400 Bad Request with standard Spring messages; unchanged.
- All exceptions are handled by `@ControllerAdvice ApiErrorHandler` (unchanged) mapping to appropriate ErrorResponse JSON.

# Test Strategy
- **Unit tests** (domain): 
  - Located in `src/test/java/com/example/demo/domain/`.
  - No Spring context; instantiate expression records directly.
  - Use JUnit 5 and Assertions (or AssertJ if preferred, but unit tests currently use Assertions; we can keep Assertions for simplicity).
  - Test each operation literal factory and evaluate method with typical values, edge cases (zeros, negatives, large numbers), and error conditions.
  - Test that expression trees produce same results as original service (we can keep a reference to original service in tests only for comparison, but not modify production).
  - Tests for `ComposeExpressionValidator` (if we consider it domain; currently it's in controller/validation; but it uses expression model, so we may test it in domain as well). However, per testing conventions, validation logic is part of the border? Actually `ComposeExpressionValidator` is used by controller to build expression tree; it contains parsing logic. We'll treat it as part of the domain for unit testing because it does not involve HTTP; we can place its unit tests in `domain/` or keep in `controller/validation/` but ensure they are unit tests (no Spring). We'll decide to keep them in `controller/validation/` as unit tests (no Spring) because they are validation utilities.
- **Integration tests** (controller):
  - Located in `src/test/java/com/example/demo/controller/`.
  - Use `@SpringBootTest` + `@AutoConfigureMockMvc`, `MockMvcTester`, AssertJ.
  - Test exact same scenarios as before (happy path, validation errors, arithmetic errors) to ensure zero contract change.
  - No modifications to these test files should be needed; they should pass after refactor.

# Test Scenario Catalog
(Representative scenarios)
1. Happy path addition: 1.5 + 2.5 = 4.0
2. Addition with large numbers: 1e307 + 2e307 = 3e307 (as before)
3. Addition producing NaN? Not possible with finite numbers.
4. Subtraction: 5.0 - 3.2 = 1.8
5. Multiplication: 2.0 * 3.0 = 6.0
6. Multiplication precision: 0.1 * 0.2 = 0.02 (using BigDecimal)
7. Division: 6.0 / 3.0 = 2.0
8. Division by zero: -> ArithmeticException "Division by zero"
9. Power: 2.0 ^ 3.0 = 8.0
10. Power overflow: 10.0 ^ 1000.0 -> NumericOverflowException
11. Power invalid (negative base fractional exponent): (-4.0) ^ 0.5 -> ArithmeticException "Invalid operation: result is undefined or imaginary"
12. Root: 27.0 ^ (1/3) = 3.0
13. Root index zero: -> ArithmeticException "Invalid operation: result is undefined or imaginary"
14. Root overflow: similar to power.
15. Compose expression: "(1+2)*3" -> 9.0
16. Compose expression invalid syntax: "1++2" -> 400 Bad Request
17. Compose expression arithmetic error: "5/0" -> 500 Internal Server Error with division by zero message.
18. Bean validation error on arithmetic endpoint: missing field -> 400 Bad Request.

# Dependency and Version Policy
- **No new dependencies required**. The refactor uses only existing dependencies:
  - Spring Boot 4.0.5 (provides Spring Web, Validation, etc.)
  - Java 25 (language level, no additional library needed)
  - Maven (build)
  - Testing dependencies: JUnit Jupiter, AssertJ, Spring Boot Test, spring-boot-starter-webmvc-test (already present).
- Therefore, the current dependency set in `pom.xml` is sufficient.
- No version changes needed; we remain on Java 25 and Spring Boot 4.0.5.

# Ordered Implementation Plan
1. **Update Expression interface** (`src/main/java/com/example/demo/domain/expression/Expression.java`):
   - Add `literal(double)` static factory.
   - Keep existing operation factories (add, subtract, multiply, divide) and add `power` and `root` factories.
2. **Create operation records**:
   - `Add.java`, `Subtract.java`, `Multiply.java`, `Divide.java`, `Power.java`, `Root.java` in same package.
   - Each implements `evaluate()` with logic mirroring original `CalculatorService` methods.
3. **Update `ComposeExpressionValidator`** (`src/main/java/com/example/demo/controller/validation/ComposeExpressionValidator.java`):
   - Replace `new ExpressionLiteral(value)` with `Expression.literal(value)`.
   - Replace `new ExpressionNode(left, right, operation)` with appropriate factory call (e.g., `Expression.add(left, right)`).
4. **Update `CalculatorController`** (`src/main/java/com/example/demo/controller/CalculatorController.java`):
   - Remove `CalculatorService` constructor parameter and field.
   - In each arithmetic endpoint method, build expression tree using `Expression.literal()` for operands and operation factory (add, subtract, etc.), then call `evaluate().value()`.
   - In `/compose` endpoint, keep call to `composeExpressionValidator.parse()` (now returns Expression tree using new factories), then `expr.evaluate().value()`.
   - Use static imports: `import static com.example.demo.domain.Expression.*;` and `var` for local variables where obvious.
5. **Remove `CalculatorService`** (`src/main/java/com/example/demo/domain/CalculatorService.java`):
   - Delete the file.
   - Remove any imports of it elsewhere (only in controller, which we already updated).
6. **Update unit tests**:
   - Rewrite `src/test/java/com/example/demo/domain/CalculatorServiceTest.java` and similar service-specific test files to test the new expression model directly.
   - Keep test class names but change internal logic to instantiate expression records and test `evaluate()`.
   - Ensure error condition tests are preserved.
   - Update `CalculatorServicePowerTest.java`, etc., similarly or rename to reflect operation under test (e.g., `AddTest.java`). However, to avoid churn, we can keep same test file names but change content; they are still unit tests for the domain.
   - If we prefer, we can create new test files for each operation and delete old service tests; but we must ensure no test modification rule is only for Step 05 (implementation) relative to Step 04 tests. Since we are in Step 03, we can plan to write new tests in Step 04.
   - For Step 04 (TDD Red), we will write failing tests based on the new model before implementation. So we do not need to update existing unit tests now; we will write new tests in Step 04 that fail, then implement to make them pass in Step 05. The existing service-oriented unit tests will be deleted or rewritten in Step 05 (as part of minimal implementation) because they test the old service. However, the rule says Step 05 must not modify tests from Step 04. So we need to have the Step 04 tests ready (failing) before we implement. Therefore, we will:
     - In Step 04, write new unit tests for the expression model (and possibly keep the old service tests as they are? but they will fail because service is deleted). Actually we will delete the service entirely, so those tests will not compile. To avoid modifying tests in Step 05, we should have the Step 04 tests ready and then in Step 05 we only change production code to make those tests pass. So we need to delete or repurpose the old service test files in Step 04 (as part of writing tests) because Step 04 is allowed to create/modify tests. Step 05 then only touches production code.
   - Therefore, the plan for Step 04: create new test files (or repurpose existing ones) for the expression model, ensuring they fail initially.
   - Step 05: implement production code to make those tests pass; do not touch the test files.
   - Integration tests in controller remain unchanged and should pass throughout.
7. **Run full test suite** to verify.

# Save and Next Steps
After approval of this Step 03 document, we will proceed to Step 04 (Tests First) to write failing unit tests for the new model.