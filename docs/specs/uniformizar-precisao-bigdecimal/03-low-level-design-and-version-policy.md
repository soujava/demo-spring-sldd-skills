# 03 — Low-Level Design and Version Policy: Uniformizar Precisão BigDecimal

## Requirement-to-Design Traceability

| Req/AC | Design Decision (Step 02) | Low-Level Artifact | Arquivo |
|--------|---------------------------|--------------------|---------|
| AC1 — subtract com BigDecimal | `subtract()` migra para `BigDecimal.valueOf()` | Método reescrito seguindo padrão de `sum()` | `CalculatorService.java:23-25` |
| AC2 — divide com defaults | `divide(d,d,s,rm)` com defaults (10, HALF_UP) | Nova assinatura; overload semântico via defaults no controller | `CalculatorService.java:34-41` |
| AC3 — divide com parâmetros explícitos | `divide(d,d,s,rm)` | Chamada direta com parâmetros do request | `CalculatorService.java` |
| AC4 — endpoint /divide com params opcionais | `DivideRequest` + controller lê opcionais | `DivideRequest` com `Integer scale`, `String roundingMode` | `DivideRequest.java` |
| AC5 — endpoint /divide backward-compatible | Campos opcionais nulos → defaults | Lógica no controller | `CalculatorController.java:55-59` |
| AC6 — ExpressionContext no compose | Novo record + campo em ExpressionNode | `ExpressionContext`, `ExpressionNode.context` | Novos + `ExpressionNode.java` |
| AC7 — compose sem context usa defaults | `ComposeExpressionValidator` cria `DEFAULT` | Lógica no parser | `ComposeExpressionValidator.java` |
| AC8 — testes adaptados | Mapa de cenários Step 02 | Testes atualizados + novos | Camada de testes |

## API Contracts

### POST /calculator/divide (atualizado)

**Request:**
```json
{
  "dividend": 10.0,         // @NotNull Double
  "divisor": 3.0,           // @NotNull Double
  "scale": 5,               // Integer (opcional, default: 10, range: 1–100)
  "roundingMode": "HALF_UP" // String (opcional, default: "HALF_UP")
}
```

**Response 200:**
```json
{ "result": 3.33333 }
```

**Response 400 (scale inválido):**
```json
{ "error": "Bad Request", "message": "Invalid scale: must be between 1 and 100" }
```

**Response 400 (roundingMode inválido):**
```json
{ "error": "Bad Request", "message": "Invalid rounding mode: INVALID" }
```

### POST /calculator/compose (atualizado)

**Request com context:**
```json
{
  "operation": "DIVIDE",
  "left": 10,
  "right": 3,
  "context": {
    "scale": 5,
    "roundingMode": "HALF_UP"
  }
}
```

**Response 200:** `{ "result": 3.33333 }`

**Response 400 (context com roundingMode inválido):**
```json
{ "error": "Bad Request", "message": "Invalid request body" }
```

**Response 400 (context com scale inválido):**
```json
{ "error": "Bad Request", "message": "Invalid request body" }
```

## Data Models

### ExpressionContext (NOVO)

```java
package com.example.demo.domain.expression;

import java.math.RoundingMode;

public record ExpressionContext(int scale, RoundingMode roundingMode) {

    public static final ExpressionContext DEFAULT =
        new ExpressionContext(10, RoundingMode.HALF_UP);

    public ExpressionContext {
        if (scale < 1 || scale > 100) {
            throw new IllegalArgumentException(
                "Invalid scale: must be between 1 and 100");
        }
        if (roundingMode == null) {
            throw new IllegalArgumentException(
                "Rounding mode must not be null");
        }
    }
}
```

### ExpressionNode (ATUALIZADO)

```java
// Antes:
public record ExpressionNode(
    ExpressionOperation operation,
    Expression left,
    Expression right
) implements Expression {}

// Depois:
public record ExpressionNode(
    ExpressionOperation operation,
    Expression left,
    Expression right,
    ExpressionContext context
) implements Expression {}
```

### DivideRequest (ATUALIZADO)

```java
// Antes:
public record DivideRequest(
    @NotNull Double dividend,
    @NotNull Double divisor
) {}

// Depois:
public record DivideRequest(
    @NotNull Double dividend,
    @NotNull Double divisor,
    Integer scale,
    String roundingMode
) {}
```

### CalculatorService.subtract() (ATUALIZADO)

```java
// Antes:
public double subtract(double minuend, double subtrahend) {
    return minuend - subtrahend;
}

// Depois:
public double subtract(double minuend, double subtrahend) {
    BigDecimal a = BigDecimal.valueOf(minuend);
    BigDecimal b = BigDecimal.valueOf(subtrahend);
    BigDecimal result = a.subtract(b);
    return result.doubleValue();
}
```

### CalculatorService.divide() (ATUALIZADO)

```java
// Antes:
public double divide(double dividend, double divisor) {
    if (divisor == 0.0) {
        throw new ArithmeticException("Division by zero");
    }
    BigDecimal a = BigDecimal.valueOf(dividend);
    BigDecimal b = BigDecimal.valueOf(divisor);
    BigDecimal result = a.divide(b);
    return result.doubleValue();
}

// Depois:
public double divide(double dividend, double divisor,
                     int scale, RoundingMode roundingMode) {
    if (divisor == 0.0) {
        throw new ArithmeticException("Division by zero");
    }
    if (scale < 1 || scale > 100) {
        throw new IllegalArgumentException(
            "Invalid scale: must be between 1 and 100");
    }
    if (roundingMode == null) {
        throw new IllegalArgumentException(
            "Rounding mode must not be null");
    }
    BigDecimal a = BigDecimal.valueOf(dividend);
    BigDecimal b = BigDecimal.valueOf(divisor);
    BigDecimal result = a.divide(b, scale, roundingMode);
    return result.doubleValue();
}
```

### CalculatorService.evaluate() (ATUALIZADO)

```java
// Antes:
case DIVIDE -> divide(left, right);

// Depois:
case DIVIDE -> divide(left, right,
    node.context().scale(), node.context().roundingMode());
```

### CalculatorController.divide() (ATUALIZADO)

```java
// Antes:
double result = calculatorService.divide(
    request.dividend(), request.divisor());

// Depois:
int scale = request.scale() != null ? request.scale() : 10;
RoundingMode roundingMode = request.roundingMode() != null
    ? RoundingMode.valueOf(request.roundingMode())
    : RoundingMode.HALF_UP;
double result = calculatorService.divide(
    request.dividend(), request.divisor(), scale, roundingMode);
```

### ComposeExpressionValidator (ATUALIZADO)

```java
// ALLOWED_FIELDS atualizado:
private static final Set<String> ALLOWED_FIELDS =
    Set.of("operation", "left", "right", "context");

// No parseNode(), após construir left e right:
JsonNode contextNode = node.get("context");
ExpressionContext context = parseContext(contextNode);
return new ExpressionNode(operation, left, right, context);

// Novo método:
private ExpressionContext parseContext(JsonNode contextNode) {
    if (contextNode == null || contextNode.isNull()) {
        return ExpressionContext.DEFAULT;
    }
    if (!contextNode.isObject()) {
        throw new InvalidExpressionPayloadException("Invalid request body");
    }
    validateContextFields(contextNode);
    JsonNode scaleNode = contextNode.get("scale");
    JsonNode roundingModeNode = contextNode.get("roundingMode");
    int scale = (scaleNode != null && !scaleNode.isNull())
        ? scaleNode.asInt()
        : ExpressionContext.DEFAULT.scale();
    RoundingMode roundingMode = (roundingModeNode != null && !roundingModeNode.isNull())
        ? parseRoundingMode(roundingModeNode.asText())
        : ExpressionContext.DEFAULT.roundingMode();
    return new ExpressionContext(scale, roundingMode);
}

private void validateContextFields(JsonNode contextNode) {
    Set<String> allowed = Set.of("scale", "roundingMode");
    contextNode.fieldNames().forEachRemaining(field -> {
        if (!allowed.contains(field)) {
            throw new InvalidExpressionPayloadException("Invalid request body");
        }
    });
}

private RoundingMode parseRoundingMode(String text) {
    try {
        return RoundingMode.valueOf(text);
    } catch (IllegalArgumentException ex) {
        throw new InvalidExpressionPayloadException("Invalid request body");
    }
}
```

## Error Model

| Erro | HTTP Status | Origem | Mensagem | Handler |
|------|-------------|--------|----------|---------|
| `scale` inválido (fora de 1–100) | 400 | `ExpressionContext` compact constructor / `CalculatorService.divide()` | `"Invalid scale: must be between 1 and 100"` | `ApiErrorHandler` → **NOVO** `IllegalArgumentException` handler |
| `roundingMode` nulo | 400 | `CalculatorService.divide()` | `"Rounding mode must not be null"` | **NOVO** `IllegalArgumentException` handler |
| `roundingMode` inválido (string não mapeável) | 400 | `RoundingMode.valueOf()` / `ComposeExpressionValidator` | `"Invalid request body"` (compose) ou `IllegalArgumentException` (divide endpoint) | **NOVO** `IllegalArgumentException` handler / `InvalidExpressionPayloadException` handler existente |
| Divisão por zero | 400 | `CalculatorService.divide()` | `"Division by zero"` | `ArithmeticException` handler existente |

**Ação necessária:** Adicionar handler para `IllegalArgumentException` no `ApiErrorHandler`:

```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
        IllegalArgumentException ex) {
    return ResponseEntity.badRequest()
        .body(new ErrorResponse("Bad Request", ex.getMessage()));
}
```

## Test Strategy

- **Domínio (unitário, sem Spring):** JUnit 5 + AssertJ
- **Controller (integração, Spring completo):** `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvcTester` + AssertJ
- **Novos testes de integração** seguirão a estrutura das classes existentes (`@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvcTester` + AssertJ), sem criar novos padrões
- **Red phase (Step 04):** Todos os testes novos falham; testes existentes de divide/subtract podem precisar de ajuste

## Test Scenario Catalog

### Domínio — CalculatorServiceSubtractTest (ATUALIZAR)

| # | Cenário | AC | Assert |
|---|---------|-----|--------|
| 1 | `subtract(5.0, 3.0) = 2.0` | AC1 | `assertEquals(2.0, ...)` |
| 2 | `subtract(7.75, 2.25) = 5.5` | AC1 | `assertEquals(5.5, ...)` |
| 3 | `subtract(9.4, 0.0) = 9.4` | AC1 | `assertEquals(9.4, ...)` |
| 4 | `subtract(2.0, 5.5) = -3.5` | AC1 | `assertEquals(-3.5, ...)` |
| 5 | `subtract(5.5, 5.5) = 0.0` | AC1 | `assertEquals(0.0, ...)` |
| 6 | `subtract(3.0E307, 2.0E307)` | AC1 | `assertEquals(1.0E307, ..., delta)` |
| 7 | **NOVO** `subtract(0.3, 0.1) = 0.2` | AC1 | `assertEquals(0.2, ...)` — precisão BigDecimal |

### Domínio — CalculatorServiceDivideTest (ATUALIZAR)

| # | Cenário | AC | Assert |
|---|---------|-----|--------|
| 1 | `divide(6.0, 2.0, 10, HALF_UP) = 3.0` | AC2 | `assertThat(result).isEqualTo(3.0)` |
| 2 | `divide(7.5, 2.5, 10, HALF_UP) = 3.0` | AC2 | `assertThat(result).isEqualTo(3.0)` |
| 3 | `divide(0.0, 5.0, 10, HALF_UP) = 0.0` | AC2 | `assertThat(result).isEqualTo(0.0)` |
| 4 | `divide(-10.0, 2.0, 10, HALF_UP) = -5.0` | AC2 | `assertThat(result).isEqualTo(-5.0)` |
| 5 | `divide(10.0, -2.0, 10, HALF_UP) = -5.0` | AC2 | `assertThat(result).isEqualTo(-5.0)` |
| 6 | `divide(1.0, 0.0, 10, HALF_UP)` → `ArithmeticException` | AC2 | `assertThatThrownBy(...)` |
| 7 | **NOVO** `divide(10.0, 3.0, 10, HALF_UP) = 3.3333333333` | AC2 | `assertThat(result).isEqualTo(3.3333333333)` |
| 8 | **NOVO** `divide(10.0, 3.0, 5, HALF_UP) = 3.33333` | AC3 | `assertThat(result).isEqualTo(3.33333)` |
| 9 | **NOVO** `divide(1.0, 3.0, 10, HALF_UP)` — decimal não-terminante | AC2 | `assertThat(result).isEqualTo(0.3333333333)` |
| 10 | **NOVO** scale inválido → `IllegalArgumentException` | — | `assertThatThrownBy(...)` |
| 11 | **NOVO** roundingMode nulo → `IllegalArgumentException` | — | `assertThatThrownBy(...)` |

### Domínio — CalculatorServiceEvaluateTest (NOVO)

| # | Cenário | AC | Assert |
|---|---------|-----|--------|
| 1 | `ADD(10, 2) = 12.0` | — | `assertEquals(12.0, ...)` |
| 2 | `SUBTRACT(5, 3) = 2.0` | AC1 | `assertEquals(2.0, ...)` |
| 3 | `MULTIPLY(4, 3) = 12.0` | — | `assertEquals(12.0, ...)` |
| 4 | `DIVIDE(10, 3) com DEFAULT context = 3.3333333333` | AC7 | `assertEquals(3.3333333333, ...)` |
| 5 | `DIVIDE(10, 3) com context(5, HALF_UP) = 3.33333` | AC6 | `assertEquals(3.33333, ...)` |
| 6 | Expressão aninhada: `ADD(10, MULTIPLY(2, 3)) = 16.0` | — | `assertEquals(16.0, ...)` |
| 7 | Expressão aninhada com DIVIDE: `ADD(DIVIDE(10,3 ctx=5HU), 2) = 5.33333` | AC6 | `assertEquals(5.33333, ...)` |

### Integração — CalculatorDivideControllerTest (ATUALIZAR)

| # | Cenário | AC | Assert |
|---|---------|-----|--------|
| 1–12 | Testes existentes (ajustar chamadas `divide` com defaults) | AC5 | Manter asserts de resultado |
| 13 | **NOVO** POST com scale=5, roundingMode=HALF_UP → 200, result=3.33333 | AC4 | `hasStatusOk()` + result |
| 14 | **NOVO** POST sem scale/roundingMode → 200, result=3.3333333333 | AC5 | `hasStatusOk()` + result |
| 15 | **NOVO** POST com roundingMode inválido → 400 | — | `hasStatus4xxClientError()` |
| 16 | **NOVO** POST com scale=0 → 400 | — | `hasStatus4xxClientError()` |
| 17 | **NOVO** POST com scale negativo → 400 | — | `hasStatus4xxClientError()` |

### Integração — CalculatorComposeControllerTest (ATUALIZAR)

| # | Cenário | AC | Assert |
|---|---------|-----|--------|
| 1–7 | Testes existentes (ajustar ALLOWED_FIELDS) | — | Manter asserts |
| 8 | **NOVO** DIVIDE com context(scale=5, HALF_UP) → 200, result=3.33333 | AC6 | `hasStatusOk()` + result |
| 9 | **NOVO** DIVIDE sem context → 200, result=3.3333333333 | AC7 | `hasStatusOk()` + result |
| 10 | **NOVO** context com roundingMode inválido → 400 | — | `hasStatus4xxClientError()` |
| 11 | **NOVO** context com campos extras → 400 | — | `hasStatus4xxClientError()` |
| 12 | **NOVO** Expressão aninhada com DIVIDE + context → 200 | AC6 | `hasStatusOk()` + result |

## Dependency and Version Policy

| Dependência | Versão atual | Status | Justificativa |
|-------------|-------------|--------|---------------|
| `java.math.BigDecimal` | JDK 25 | **Já presente** | Usado em `sum()`, `multiply()`, `divide()` |
| `java.math.RoundingMode` | JDK 25 | **Já presente** | Parte do JDK, sem versão extra |
| Spring Boot | 4.0.5 | **Sem mudança** | Nenhum novo starter necessário |
| Jakarta Validation | via spring-boot-starter-validation | **Sem mudança** | `@NotNull` já usado |
| Jackson | via spring-boot-starter-web | **Sem mudança** | Já usado no `ComposeExpressionValidator` |
| JUnit 5 | via spring-boot-starter-test | **Sem mudança** | Testes unitários |
| AssertJ | via spring-boot-starter-test | **Sem mudança** | Testes de integração |

**Nenhuma dependência nova é necessária.** Tudo é coberto pelo JDK 25 e pelas dependências existentes.

## Ordered Implementation Plan

| Ordem | Arquivo | Ação | AC coberto |
|-------|---------|------|------------|
| 1 | `ExpressionContext.java` | **CRIAR** — novo record com compact constructor, DEFAULT, validação | AC6, AC7 |
| 2 | `ExpressionNode.java` | **ATUALIZAR** — adicionar campo `context` | AC6, AC7 |
| 3 | `Expression.java` | **VERIFICAR** — sealed interface pode precisar de ajuste | — |
| 4 | `CalculatorService.java` — `subtract()` | **ATUALIZAR** — migrar para BigDecimal | AC1 |
| 5 | `CalculatorService.java` — `divide()` | **ATUALIZAR** — nova assinatura com scale/roundingMode | AC2, AC3 |
| 6 | `CalculatorService.java` — `evaluate()` | **ATUALIZAR** — despachar context no DIVIDE | AC6, AC7 |
| 7 | `DivideRequest.java` | **ATUALIZAR** — adicionar `Integer scale`, `String roundingMode` | AC4, AC5 |
| 8 | `CalculatorController.java` — `divide()` | **ATUALIZAR** — ler opcionais, aplicar defaults, converter roundingMode | AC4, AC5 |
| 9 | `ApiErrorHandler.java` | **ATUALIZAR** — adicionar handler para `IllegalArgumentException` | — |
| 10 | `ComposeExpressionValidator.java` | **ATUALIZAR** — ALLOWED_FIELDS, parseContext, parseRoundingMode | AC6, AC7 |
| 11 | Testes unitários (domínio) | **ATUALIZAR/CRIAR** — ajustar divide/subtract, criar evaluate | AC1–AC3, AC6–AC8 |
| 12 | Testes de integração (controller) | **ATUALIZAR** — ajustar divide/compose | AC4–AC8 |
