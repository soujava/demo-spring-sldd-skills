# Low-Level Design and Version Policy: Expression Contextual Expression Refactor

## Requirement-to-Design Traceability

| Requisito | Decisao low-level |
|---|---|
| AC1: preservar contexto raiz | `/calculator/evaluate` deve resolver `EvaluateRequest.context` para `CalculationContext` completo e chamar `expression.evaluate(rootContext)`. |
| AC2: preservar contexto local completo | `BinaryOperationDto.context` completo deve virar `CalculationContextOverride` dentro de `ContextualExpression`. |
| AC3: preservar heranca parcial | `CalculationContextOverride.resolve(CalculationContext inherited)` deve preencher `null` com valores herdados. |
| AC4: representar contexto local no dominio | Adicionar `ContextualExpression` como implementacao permitida de `Expression`. |
| AC5: preservar assinatura de avaliacao | Nao adicionar novo overload publico; manter `evaluate(CalculationContext)` e `evaluate()`. |
| AC6: nao alterar contrato HTTP | Manter DTOs, payload JSON, respostas e status HTTP atuais. |

Decisoes do Step 02 que restringem Step 04/05:

- O controller deve mapear DTO para arvore `Expression`, nao avaliar DTO recursivamente.
- Contexto local deve ser modelado como no estrutural via `ContextualExpression`.
- `CalculationContextDto` continua sendo DTO de borda.
- `CalculationContext` continua sendo contexto completo de dominio.
- O override parcial deve ser um tipo de dominio separado.

## API Contracts

Contrato HTTP publico permanece igual.

Endpoint:

```text
POST /calculator/evaluate
```

Request continua usando:

```java
EvaluateRequest(
    ExpressionDto expression,
    CalculationContextDto context
)
```

Arvore JSON continua usando:

```java
LiteralDto(double value)
BinaryOperationDto(
    OperatorDto operator,
    ExpressionDto left,
    ExpressionDto right,
    CalculationContextDto context
)
```

Response permanece:

```java
EvaluateResponse(double result)
```

Sem novos campos JSON, sem remocao de campos existentes, sem mudanca de nomes.

Contrato interno de dominio:

```java
public sealed interface Expression
        permits Literal, Sum, Subtract, Multiply, Divide, Power, Root, ContextualExpression {

    Literal evaluate(CalculationContext context);

    default Literal evaluate() {
        return evaluate(CalculationContext.defaults());
    }

    static Expression contextual(Expression expression, CalculationContextOverride context) {
        return new ContextualExpression(expression, context);
    }
}
```

Contrato de `ContextualExpression`:

```java
public record ContextualExpression(
    Expression expression,
    CalculationContextOverride context
) implements Expression {

    @Override
    public Literal evaluate(CalculationContext inheritedContext) {
        CalculationContext resolvedContext = context.resolve(inheritedContext);
        return expression.evaluate(resolvedContext);
    }
}
```

Contrato de `CalculationContextOverride`:

```java
public record CalculationContextOverride(
    Integer scale,
    RoundingMode roundingMode
) {
    public CalculationContext resolve(CalculationContext inherited) {
        int resolvedScale = scale == null ? inherited.scale() : scale;
        RoundingMode resolvedRoundingMode =
            roundingMode == null ? inherited.roundingMode() : roundingMode;
        return new CalculationContext(resolvedScale, resolvedRoundingMode);
    }
}
```

Factory opcional recomendada em `Expression`:

```java
static Expression contextual(Expression expression, CalculationContextOverride context)
```

## Data Models

`CalculationContext`

- Continua completo.
- Continua usando `int scale`.
- Continua usando `RoundingMode roundingMode`.
- Continua com `defaults()`.

`CalculationContextOverride`

- Novo record de dominio.
- Campos opcionais:
  - `Integer scale`
  - `RoundingMode roundingMode`
- Representa somente override parcial/local.
- Nao substitui `CalculationContext`.
- Nao deve conter anotacoes Jakarta de validacao.

`ContextualExpression`

- Novo record de dominio.
- Campos:
  - `Expression expression`
  - `CalculationContextOverride context`
- Avalia a expressao interna com contexto resolvido.
- Deve ser incluido no `permits` de `Expression`.

Mapeamento DTO -> dominio no controller:

```text
LiteralDto
  -> Expression.literal(value)

BinaryOperationDto sem context
  -> Expression.<operator>(leftExpression, rightExpression)

BinaryOperationDto com context
  -> Expression.contextual(
       Expression.<operator>(leftExpression, rightExpression),
       new CalculationContextOverride(dto.context().scale(), dto.context().roundingMode())
     )
```

O `mapToDomain(ExpressionDto)` atual deve passar a considerar `BinaryOperationDto.context`.

## Error Model

Sem novos tipos de erro.

Erros preservados:

| Origem | Comportamento esperado |
|---|---|
| Divisao por zero | `ArithmeticException`, HTTP 400 via `ApiErrorHandler`. |
| Overflow/NaN em `Power` ou `Root` | `NumericOverflowException`, HTTP 422. |
| `scale` fora de 1..16 em DTO | Validacao Jakarta na borda, sem mover para dominio. |
| `roundingMode` invalido no JSON | Erro de desserializacao/validacao HTTP existente. |

`CalculationContextOverride.resolve(...)` nao deve introduzir validacao de limites duplicada, porque `CalculationContextDto` ja protege entrada HTTP e `CalculationContext` atual tambem nao valida limites no construtor.

## Test Strategy

Seguir as convencoes do repo:

- Testes de dominio: JUnit puro, sem Spring.
- Testes HTTP: `@SpringBootTest`, `@AutoConfigureMockMvc`, `MockMvcTester`.
- Step 04 deve escrever testes primeiro.
- Step 05 deve implementar o minimo para passar os testes.

Testes de dominio devem cobrir a nova modelagem:

- `CalculationContextOverride.resolve(...)`
- `ContextualExpression.evaluate(CalculationContext)`
- `ContextualExpression.evaluate()`
- composicao de `ContextualExpression` dentro de arvore maior

Testes HTTP devem preservar regressao:

- contexto raiz
- contexto local completo
- contexto local parcial
- formato de resposta atual

## Test Scenario Catalog

| ID | Camada | Criterio | Cenario |
|---|---|---|---|
| T1 | Dominio | AC3 | `CalculationContextOverride(scale=2, roundingMode=null)` resolve contra `scale=6, HALF_EVEN` para `scale=2, HALF_EVEN`. |
| T2 | Dominio | AC2/AC4 | `ContextualExpression(Expression.divide(1, 3), override scale=2 HALF_UP)` avaliada com contexto herdado escala 6 retorna `0.33`. |
| T3 | Dominio | AC3/AC4 | `ContextualExpression` com override parcial herda `roundingMode` do contexto herdado. |
| T4 | Dominio | AC5 | `ContextualExpression.evaluate()` usa `CalculationContext.defaults()`. |
| T5 | Dominio | AC4 | Arvore com no contextual interno preserva contexto local apenas naquela subexpressao. |
| T6 | HTTP | AC1/AC6 | `/calculator/evaluate` com contexto raiz em `DIVIDE` continua retornando `0.3333`. |
| T7 | HTTP | AC2/AC6 | `/calculator/evaluate` com contexto local completo em `POWER` continua retornando `1.4142`. |
| T8 | HTTP | AC3/AC6 | `/calculator/evaluate` com contexto raiz `HALF_EVEN` e contexto local parcial `scale=2` continua retornando `0.33`. |
| T9 | HTTP | AC6 | soma simples e operacoes aninhadas continuam com mesmo formato de resposta. |
| T10 | HTTP | Error model | divisao por zero em `/calculator/evaluate` continua retornando 400. |

## Dependency and Version Policy

Dependencias atuais sao suficientes.

Nenhuma nova dependencia deve ser adicionada.

Politica:

- Manter Spring Boot `4.0.5`.
- Manter Java `25`.
- Manter Maven wrapper atual.
- Usar JUnit/AssertJ ja presentes no projeto.
- Nao adicionar bibliotecas para mapeamento DTO, validacao ou algebra de expressoes.

Impacto:

- Runtime: comportamento externo deve permanecer igual.
- Testes: apenas novos testes unitarios de dominio e possivel reforco dos testes HTTP existentes.
- Manutencao: reduz duplicidade no controller e centraliza regra de contexto local no dominio.

## Ordered Implementation Plan

1. Criar teste unitario para `CalculationContextOverride.resolve(...)`.
2. Criar testes unitarios para `ContextualExpression.evaluate(CalculationContext)` e `evaluate()`.
3. Criar ou ajustar teste unitario para arvore com contexto local interno.
4. Reforcar testes HTTP existentes apenas se algum cenario aprovado nao estiver coberto.
5. Adicionar `CalculationContextOverride` em `src/main/java/com/example/demo/domain/`.
6. Adicionar `ContextualExpression` em `src/main/java/com/example/demo/domain/`.
7. Atualizar `Expression permits` para incluir `ContextualExpression`.
8. Adicionar factory `Expression.contextual(...)`.
9. Alterar `CalculatorController.mapToDomain(ExpressionDto)` para envolver operacoes com `ContextualExpression` quando `BinaryOperationDto.context` estiver presente.
10. Alterar `/calculator/evaluate` para resolver contexto raiz, mapear DTO para dominio e chamar `expression.evaluate(rootContext)`.
11. Remover o metodo procedural `evaluate(ExpressionDto, CalculationContext)` se ficar sem uso.
12. Executar `./mvnw test`.
13. Corrigir apenas falhas relacionadas ao refactor, sem ampliar escopo.
