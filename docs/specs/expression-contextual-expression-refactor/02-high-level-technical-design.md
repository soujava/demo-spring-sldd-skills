# High-Level Technical Design: Expression Contextual Expression Refactor

## Requirements Traceability

| Step 01 Requirement | High-Level Design Response |
|---|---|
| Preservar contrato HTTP de `/calculator/evaluate` | DTOs publicos permanecem iguais: `EvaluateRequest`, `ExpressionDto`, `BinaryOperationDto`, `CalculationContextDto`. |
| Preservar contexto raiz | Controller continua resolvendo contexto raiz contra `CalculationContext.defaults()`. |
| Preservar contexto local completo | Contexto local de `BinaryOperationDto.context` sera mapeado para um no de dominio contextual. |
| Preservar heranca parcial | Novo tipo de override parcial resolvera campos ausentes contra o contexto herdado. |
| Representar contexto local no dominio | Introduzir `ContextualExpression` na hierarquia `Expression`. |
| Preservar `evaluate(CalculationContext)` e `evaluate()` | `ContextualExpression` implementa as mesmas assinaturas, sem novo metodo publico de avaliacao. |
| Evitar mudanca observavel | Testes HTTP existentes continuam protegendo contrato e resultados. |

## Architecture Diagram

```text
HTTP JSON
  |
  v
EvaluateRequest
  |
  v
CalculatorController
  |
  | resolve root context
  | map DTO tree to domain tree
  v
Expression tree
  |
  +-- Literal
  +-- Sum / Subtract / Multiply / Divide / Power / Root
  +-- ContextualExpression
        |
        +-- CalculationContextOverride
        +-- wrapped Expression
  |
  v
expression.evaluate(rootContext)
  |
  v
EvaluateResponse
```

Fluxo de contexto:

```text
root CalculationContext
  |
  v
ContextualExpression.evaluate(inherited)
  |
  v
CalculationContextOverride.resolve(inherited)
  |
  v
wrappedExpression.evaluate(resolved)
```

## Component Responsibilities

`Expression`

- Continua sendo a hierarquia fechada do dominio.
- Preserva `Literal evaluate(CalculationContext context)`.
- Preserva `default Literal evaluate()`.
- Passa a permitir `ContextualExpression`.

`ContextualExpression`

- Representa uma expressao com contexto local associado.
- Recebe uma `Expression` interna.
- Recebe um override parcial de contexto.
- Resolve o contexto efetivo contra o contexto herdado.
- Avalia a expressao interna com o contexto efetivo.

`CalculationContextOverride`

- Representa campos opcionais de contexto local.
- Resolve `scale` e `roundingMode` contra um `CalculationContext` herdado.
- Nao substitui `CalculationContext`, que continua sendo o contexto completo de avaliacao.

`CalculatorController`

- Continua sendo ponto de entrada HTTP.
- Preserva os DTOs publicos.
- Deve deixar de avaliar recursivamente DTOs em `/evaluate`.
- Deve mapear DTOs para uma arvore `Expression` completa, incluindo nos `ContextualExpression` quando houver contexto local.

`CalculationContextDto`

- Continua sendo DTO de borda com campos opcionais e validacao Jakarta.
- Nao deve virar tipo central de dominio.

## Data Flow

Para `/calculator/evaluate` sem contexto local:

```text
request.context -> root CalculationContext
request.expression -> Expression
Expression.evaluate(rootContext)
```

Para `/calculator/evaluate` com contexto local:

```text
request.context -> root CalculationContext
BinaryOperationDto.context -> CalculationContextOverride
BinaryOperationDto -> ContextualExpression(operationExpression, override)
ContextualExpression.evaluate(rootContext)
operationExpression.evaluate(resolvedContext)
```

Para contexto parcial:

```text
root:  scale=6, roundingMode=HALF_EVEN
local: scale=2, roundingMode=null
resolved: scale=2, roundingMode=HALF_EVEN
```

## Security and Observability Requirements

- Nao ha novo requisito de autenticacao/autorizacao.
- Nao ha mudanca planejada em logging ou metricas.
- Tratamento de erro deve permanecer compativel:
  - `ArithmeticException` retorna 400 via `ApiErrorHandler`.
  - `NumericOverflowException` retorna 422.
  - erros de validacao de `scale` e `roundingMode` permanecem na camada HTTP.
- O refactor nao deve expor detalhes internos do dominio no JSON publico.

## Trade-Offs and Alternatives

| Alternativa | Decisao | Motivo |
|---|---|---|
| Manter contexto so no controller | Rejeitada | Preserva simplicidade do dominio, mas mantem regra importante fora da arvore `Expression`. |
| Adicionar contexto opcional em cada operacao | Rejeitada | Espalha regra transversal em `Sum`, `Divide`, `Power`, etc. |
| Criar `ContextualExpression` | Selecionada | Modela contexto local como no estrutural e preserva contrato de avaliacao. |
| Adicionar terceiro parametro em `evaluate` | Rejeitada | Complica o contrato publico e contradiz a decisao de contexto como dado estrutural. |
| Converter `Literal` para `BigDecimal` | Fora de escopo | Mudanca numerica ampla, nao necessaria para este refactor. |

## High-Level Test Scenario Map

| Criterio | Camada | Cenario |
|---|---|---|
| AC1 | Integracao HTTP | `/calculator/evaluate` com contexto raiz em operacao `DIVIDE`. |
| AC2 | Integracao HTTP | operacao com contexto local completo sobrescreve contexto raiz. |
| AC3 | Integracao HTTP | contexto local parcial herda `roundingMode` do contexto raiz. |
| AC4 | Unitario dominio | `ContextualExpression` resolve override e avalia expressao interna. |
| AC4 | Unitario dominio | `ContextualExpression` parcial herda campos ausentes. |
| AC5 | Unitario dominio | `ContextualExpression.evaluate()` usa defaults. |
| AC6 | Integracao HTTP | payloads existentes mantem formato e resultado. |
