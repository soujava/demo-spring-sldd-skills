# 03 — Low-Level Design and Version Policy

## Requirement-to-Design Traceability

| Requisito aprovado | Decisao de baixo nivel |
|---|---|
| Introduzir `CalculationContext` no dominio | Criar `com.example.demo.domain.CalculationContext` como record |
| Defaults `scale = 10`, `HALF_UP` | `CalculationContext.defaults()` retorna esses valores |
| `scale` valido `1..16` | `CalculationContextDto.scale` usa `@Min(1)` e `@Max(16)` |
| `roundingMode` como enum | `CalculationContextDto.roundingMode` usa `java.math.RoundingMode` |
| `/divide`, `/power`, `/root`, `/evaluate` aceitam contexto | Adicionar campo opcional `context` aos respectivos request DTOs |
| `DIVIDE`, `POWER`, `ROOT` consomem contexto | Adicionar assinaturas/overloads no `CalculatorService` com `CalculationContext` |
| `/evaluate` com contexto raiz e local | `EvaluateRequest.context` e `BinaryOperationDto.context` opcionais |
| Heranca campo a campo | Resolver contexto local usando fallback do contexto pai |
| Contexto invalido retorna 400 claro | Usar Bean Validation para `scale` e ajustar handler para mensagens relevantes |

## API Contracts

### `CalculationContextDto`

```json
{
  "scale": 10,
  "roundingMode": "HALF_UP"
}
```

Campos:

- `scale`: inteiro opcional, minimo `1`, maximo `16`.
- `roundingMode`: enum opcional com valores oficiais de `java.math.RoundingMode`.

Valores aceitos de `roundingMode`:

```text
UP
DOWN
CEILING
FLOOR
HALF_UP
HALF_DOWN
HALF_EVEN
UNNECESSARY
```

### `POST /calculator/divide`

Request sem contexto continua valido:

```json
{
  "dividend": 1,
  "divisor": 3
}
```

Request com contexto:

```json
{
  "dividend": 1,
  "divisor": 3,
  "context": {
    "scale": 4,
    "roundingMode": "HALF_UP"
  }
}
```

Response 200:

```json
{
  "result": 0.3333
}
```

### `POST /calculator/power`

```json
{
  "base": 2,
  "exponent": 0.5,
  "context": {
    "scale": 4,
    "roundingMode": "HALF_UP"
  }
}
```

Response 200:

```json
{
  "result": 1.4142
}
```

### `POST /calculator/root`

```json
{
  "radicand": 2,
  "index": 2,
  "context": {
    "scale": 4,
    "roundingMode": "HALF_UP"
  }
}
```

Response 200:

```json
{
  "result": 1.4142
}
```

### `POST /calculator/evaluate`

Contexto raiz:

```json
{
  "context": {
    "scale": 6,
    "roundingMode": "HALF_EVEN"
  },
  "expression": {
    "type": "operation",
    "operator": "DIVIDE",
    "left": { "type": "literal", "value": 1 },
    "right": { "type": "literal", "value": 3 }
  }
}
```

Contexto local parcial:

```json
{
  "context": {
    "scale": 6,
    "roundingMode": "HALF_EVEN"
  },
  "expression": {
    "type": "operation",
    "operator": "DIVIDE",
    "left": { "type": "literal", "value": 1 },
    "right": { "type": "literal", "value": 3 },
    "context": {
      "scale": 2
    }
  }
}
```

Neste caso, a operacao usa:

```text
scale = 2
roundingMode = HALF_EVEN
```

## Data Models

### Dominio

```java
public record CalculationContext(int scale, RoundingMode roundingMode) {
    public static final int DEFAULT_SCALE = 10;
    public static final int MIN_SCALE = 1;
    public static final int MAX_SCALE = 16;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    public static CalculationContext defaults() { ... }
}
```

`CalculatorService` deve expor operacoes com contexto:

```java
double divide(double dividend, double divisor, CalculationContext context)
double power(double base, double exponent, CalculationContext context)
double root(double radicand, double index, CalculationContext context)
double evaluate(Expression expression, CalculationContext context)
```

Overloads existentes sem contexto devem permanecer:

```java
double divide(double dividend, double divisor)
double power(double base, double exponent)
double root(double radicand, double index)
double evaluate(Expression expression)
```

Esses overloads usam `CalculationContext.defaults()`.

### API

Criar:

```java
public record CalculationContextDto(
    @Min(1) @Max(16) Integer scale,
    RoundingMode roundingMode
) {
}
```

Alterar:

```java
public record DivideRequest(
    @NotNull Double dividend,
    @NotNull Double divisor,
    @Valid CalculationContextDto context
) {
}
```

```java
public record PowerRequest(
    @NotNull Double base,
    @NotNull Double exponent,
    @Valid CalculationContextDto context
) {
}
```

```java
public record RootRequest(
    @NotNull Double radicand,
    @NotNull Double index,
    @Valid CalculationContextDto context
) {
}
```

```java
public record EvaluateRequest(
    @NotNull @Valid ExpressionDto expression,
    @Valid CalculationContextDto context
) {
}
```

```java
public record BinaryOperationDto(
    @NotNull OperatorDto operator,
    @NotNull @Valid ExpressionDto left,
    @NotNull @Valid ExpressionDto right,
    @Valid CalculationContextDto context
) implements ExpressionDto {
}
```

## Error Model

### Validation Errors

`scale < 1`:

```json
{
  "error": "Bad Request",
  "message": "scale must be greater than or equal to 1"
}
```

`scale > 16`:

```json
{
  "error": "Bad Request",
  "message": "scale must be less than or equal to 16"
}
```

`roundingMode` invalido:

```json
{
  "error": "Bad Request",
  "message": "Invalid request body"
}
```

Ou, se viavel no handler:

```json
{
  "error": "Bad Request",
  "message": "Invalid roundingMode"
}
```

A implementacao deve preferir a mensagem especifica quando possivel, sem expor stack trace.

### Arithmetic Errors

Divisao por zero permanece:

```json
{
  "error": "Bad Request",
  "message": "Division by zero"
}
```

`RoundingMode.UNNECESSARY` quando arredondamento e necessario deve retornar `400` via `ArithmeticException`, com a mensagem do Java ou uma mensagem clara equivalente.

Overflow em `power` permanece `422 Unprocessable Entity`.

## Test Strategy

Seguir as convencoes existentes:

- Testes unitarios de dominio:
  - JUnit puro.
  - Instanciar `CalculatorService` diretamente.
  - Testar `CalculationContext` e operacoes matematicas sem Spring.

- Testes de integracao HTTP:
  - `@SpringBootTest`.
  - `@AutoConfigureMockMvc`.
  - `MockMvcTester`.
  - Testar status HTTP, JSON de resposta e validacao.

- Testes OpenAPI:
  - Verificar que schemas documentam `context`.
  - Verificar que `roundingMode` aparece como enum.

## Test Scenario Catalog

| Camada | Cenario | Resultado esperado |
|---|---|---|
| Dominio | `CalculationContext.defaults()` | scale 10, HALF_UP |
| Dominio | `divide(1, 3, scale 4 HALF_UP)` | `0.3333` |
| Dominio | `divide(1, 3, scale 2 DOWN)` | `0.33` |
| Dominio | `divide(1, 0, context)` | `ArithmeticException` |
| Dominio | `divide(1, 3, UNNECESSARY)` | `ArithmeticException` |
| Dominio | `power(2, 0.5, scale 4 HALF_UP)` | `1.4142` |
| Dominio | `root(2, 2, scale 4 HALF_UP)` | `1.4142` |
| HTTP | `/divide` sem context | 200 usando defaults |
| HTTP | `/divide` com context | 200 usando contexto |
| HTTP | `/power` com context | 200 com resultado arredondado |
| HTTP | `/root` com context | 200 com resultado arredondado |
| HTTP | `scale = 0` | 400 com mensagem clara |
| HTTP | `scale = 17` | 400 com mensagem clara |
| HTTP | `roundingMode = INVALID` | 400 com mensagem clara |
| HTTP | `/evaluate` com contexto raiz | operacoes interessadas usam raiz |
| HTTP | `/evaluate` com contexto local parcial | herda campo ausente do contexto raiz |
| OpenAPI | schema de contexto | `roundingMode` documentado como enum |

## Dependency and Version Policy

O conjunto atual de dependencias e suficiente.

Nao adicionar novas dependencias.

Dependencias relevantes ja existentes:

- Java 25:
  - records;
  - sealed interfaces;
  - pattern matching usado no codigo existente.
- Spring Boot 4.0.5:
  - MVC;
  - Bean Validation;
  - Jackson;
  - geracao OpenAPI ja existente no projeto.
- `java.math.BigDecimal`, `RoundingMode`:
  - biblioteca padrao do JDK.

Impacto:

- Runtime: apenas novos campos opcionais e arredondamento explicito em operacoes interessadas.
- Testes: novos testes unitarios e de integracao.
- Manutencao: regras de contexto ficam centralizadas em `CalculationContext` e conversao/validacao na camada HTTP.

## Ordered Implementation Plan

1. Criar `CalculationContext` no dominio com defaults e constantes.
2. Criar `CalculationContextDto` em `controller/api`.
3. Adicionar `context` opcional a `DivideRequest`, `PowerRequest`, `RootRequest`, `EvaluateRequest` e `BinaryOperationDto`.
4. Adicionar conversao de `CalculationContextDto` para `CalculationContext`.
5. Implementar resolucao campo a campo:
   - local;
   - pai/raiz;
   - defaults.
6. Adicionar overloads com contexto em `CalculatorService`.
7. Atualizar `divide` para usar `BigDecimal.divide(scale, roundingMode)`.
8. Atualizar `power` e `root` para arredondar resultado final com `BigDecimal.setScale`.
9. Atualizar `/evaluate` para propagar contexto efetivo por operacao.
10. Ajustar `ApiErrorHandler` para mensagens claras de validacao de contexto.
11. Atualizar testes unitarios.
12. Atualizar testes de integracao.
13. Atualizar/verificar teste OpenAPI.
