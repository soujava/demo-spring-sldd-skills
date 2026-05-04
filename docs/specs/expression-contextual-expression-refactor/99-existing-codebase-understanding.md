# Existing Codebase Understanding: Expression Contextual Expression Refactor

## Repository Structure Overview

- Projeto Spring Boot 4.0.5 com Java 25 e Maven.
- Entrada da aplicacao: `src/main/java/com/example/demo/DemoApplication.java`.
- Dominio principal: `src/main/java/com/example/demo/domain/`.
- Camada HTTP: `src/main/java/com/example/demo/controller/`.
- DTOs de API: `src/main/java/com/example/demo/controller/api/`.
- Testes unitarios de dominio: `src/test/java/com/example/demo/domain/`.
- Testes de integracao HTTP: `src/test/java/com/example/demo/controller/`.
- Spec atual: `docs/specs/expression-contextual-expression-refactor/`.

## Architecture Summary

O modelo de dominio usa uma hierarquia fechada com `sealed interface Expression`:

```java
public sealed interface Expression permits Literal, Sum, Subtract, Multiply, Divide, Power, Root
```

Cada expressao implementa:

```java
Literal evaluate(CalculationContext context)
```

E `Expression` tambem oferece:

```java
default Literal evaluate()
```

que usa `CalculationContext.defaults()`.

Operacoes atuais:

- `Literal`: retorna `this`.
- `Sum`, `Subtract`, `Multiply`: avaliam filhos com o mesmo contexto e usam `BigDecimal` para a operacao.
- `Divide`: avalia divisor, rejeita zero e aplica `scale`/`roundingMode`.
- `Power` e `Root`: usam `Math.pow`, tratam overflow/NaN e aplicam `scale`/`roundingMode`.

`CalculationContext` e um record completo:

```java
public record CalculationContext(int scale, RoundingMode roundingMode)
```

com defaults:

- `scale = 10`
- `roundingMode = HALF_UP`
- limites documentados: `MIN_SCALE = 1`, `MAX_SCALE = 16`

Na API, o contexto parcial existe em `CalculationContextDto`:

```java
public record CalculationContextDto(
    @Min(1) @Max(16) Integer scale,
    RoundingMode roundingMode
)
```

O endpoint `/calculator/evaluate` aceita:

- `EvaluateRequest.context`: contexto raiz opcional.
- `BinaryOperationDto.context`: contexto local opcional por operacao.

Hoje a heranca de contexto local e resolvida no controller por:

```java
private CalculationContext resolveContext(CalculationContextDto dto, CalculationContext fallback)
```

O controller tem duas rotas internas relevantes:

- `evaluate(ExpressionDto, CalculationContext)`: avalia recursivamente DTOs preservando contexto local.
- `mapToDomain(ExpressionDto)`: converte DTO para `Expression`, mas ignora `BinaryOperationDto.context`.

Essa duplicidade e o principal ponto brownfield para o refactor.

## Conventions to Preserve

- Java 25 com `sealed interface`, `record`, pattern matching e switch expressions.
- Testes unitarios de dominio sem contexto Spring.
- Testes de integracao HTTP com `@SpringBootTest`, `@AutoConfigureMockMvc` e `MockMvcTester`.
- Testes HTTP devem validar contrato da borda, nao logica de dominio.
- Testes de dominio devem instanciar classes diretamente.
- Contrato JSON de `/calculator/evaluate` deve ser preservado.
- Endpoints diretos (`/sum`, `/subtract`, `/multiply`, `/divide`, `/power`, `/root`) devem continuar respondendo no formato atual.
- Erros de dominio seguem por `ApiErrorHandler`, especialmente `ArithmeticException` como 400 e `NumericOverflowException` como 422.

## Integration Points

- `CalculatorController.evaluate(...)` e o ponto principal de integracao para `/calculator/evaluate`.
- `ExpressionDto`, `LiteralDto`, `BinaryOperationDto` e `OperatorDto` definem a arvore JSON.
- `CalculationContextDto` representa override parcial na borda.
- `CalculationContext` representa contexto completo no dominio.
- `Expression` e a hierarquia central a ser expandida se `ContextualExpression` for introduzida.
- Testes existentes mais criticos:
  - `CalculatorEvaluateControllerTest`
  - `ExpressionTreeTest`
  - `DivideTest`
  - `PowerTest`
  - `RootTest`
  - `CalculationContextTest`

## Risks and Unknowns

- Adicionar `ContextualExpression` exige atualizar o `permits` de `Expression`.
- A decisao sobre criar um novo tipo de dominio para contexto parcial ainda precisa ser desenhada no Step 02/03.
- O comportamento atual de `mapToDomain(ExpressionDto)` ignora contexto local; o design deve decidir se ele sera substituido, alterado ou complementado.
- A avaliacao atual em `/calculator/evaluate` avalia filhos antes de reconstruir uma operacao com literais, o que pode esconder diferencas sutis ao mover essa regra para o dominio.
- O contrato HTTP ja possui testes para contexto raiz, contexto local completo e heranca parcial; esses testes devem permanecer como protecao de regressao.
- O design deve evitar mover validacao de DTO para o dominio sem necessidade.
- O dominio atual usa `double` em `Literal`; trocar para `BigDecimal` esta fora de escopo.

## Context to Carry Into Steps 02-06

- Step 02 deve decidir onde ficara o mapeamento DTO -> arvore de dominio contextual.
- Step 02 deve preservar a assinatura `Expression.evaluate(CalculationContext)` e `Expression.evaluate()`.
- Step 03 deve especificar contratos de `ContextualExpression` e do tipo que representa override parcial.
- Step 03 deve definir se a factory sera em `Expression`, por exemplo `Expression.contextual(...)`.
- Step 04 deve criar testes unitarios para contexto local no dominio antes da implementacao.
- Step 04 deve manter ou reforcar testes de integracao de `/calculator/evaluate` para contexto raiz, contexto local completo e contexto parcial herdado.
- Step 05 deve buscar a menor alteracao que remova a regra procedural do controller sem mudar comportamento observavel.
- Step 06 deve verificar compatibilidade HTTP, cobertura de criterios de aceitacao e ausencia de mudanca fora do escopo.
