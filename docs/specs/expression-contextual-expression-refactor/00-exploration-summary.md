# Exploration Summary: Contextual Expression Refactor

## Current Understanding

The current `Expression` model is behavioral: each expression implements `Literal evaluate(CalculationContext context)`, and `Expression.evaluate()` delegates to `CalculationContext.defaults()`.

The `/calculator/evaluate` endpoint supports a root calculation context and local per-operation context overrides through `BinaryOperationDto.context`. A local context may be partial and inherits missing fields from the parent context.

Today, this local-context behavior is handled procedurally in `CalculatorController.evaluate(ExpressionDto, CalculationContext)`. The domain model does not represent a contextual expression node, so the controller must recursively evaluate DTOs to preserve per-node context semantics.

## Candidate Product Decision

Preserve existing API behavior for `/calculator/evaluate`: callers can provide a root context and local operation contexts, and local contexts inherit missing fields from the nearest inherited context.

## Candidate Technical Decision

Introduce `ContextualExpression` as part of the `Expression` hierarchy.

`ContextualExpression` wraps another `Expression` and a partial local context override. It remains a normal `Expression` and implements the same evaluation contract.

Suggested shape:

```java
public record ContextualExpression(
    Expression expression,
    CalculationContextOverride context
) implements Expression {

    @Override
    public Literal evaluate(CalculationContext inheritedContext) {
        return expression.evaluate(context.resolve(inheritedContext));
    }
}
```

`CalculationContextOverride` represents optional context fields and resolves them against an inherited context:

```java
public record CalculationContextOverride(
    Integer scale,
    RoundingMode roundingMode
) {

    public CalculationContext resolve(CalculationContext inherited) {
        int resolvedScale = scale == null ? inherited.scale() : scale;
        RoundingMode resolvedRoundingMode = roundingMode == null
            ? inherited.roundingMode()
            : roundingMode;

        return new CalculationContext(resolvedScale, resolvedRoundingMode);
    }
}
```

The `Expression` interface should preserve its existing signatures:

```java
Literal evaluate(CalculationContext context);

default Literal evaluate() {
    return evaluate(CalculationContext.defaults());
}
```

The local context should be structural data in the expression tree, not a third evaluation parameter.

## Alternatives Discussed

| Alternative | Decision | Reason |
|---|---|---|
| Keep context only outside the domain | Not selected for current direction | Keeps domain simpler, but leaves per-node context behavior hidden in controller recursion. |
| Add optional context to every operation record | Avoid | Spreads a cross-cutting concern across all operation types and couples each operation to context override semantics. |
| Add `evaluate(CalculationContext inherited, CalculationContext local)` | Avoid | Makes the evaluation contract harder to reason about; local context should belong to the node. |

## Risks and Assumptions

- `ContextualExpression` expands the sealed `Expression` hierarchy and may require updates to tests and any exhaustive switches.
- `CalculationContextOverride` needs validation boundaries consistent with `CalculationContextDto` (`scale` from 1 to 16).
- The refactor should preserve current HTTP behavior and response values before improving internals.
- The controller mapping can likely become simpler by mapping DTO context into domain `ContextualExpression` nodes, but exact design belongs in Step 02/03.
- Existing behavior around partial context inheritance must be covered by tests before implementation.

## Suggested Next SLDD Step

Proceed to Step 01 — Product Intent Specification.

Step 01 should formalize the user-visible intent, accepted behavior, non-goals, risks, and success criteria. Step 99 should be completed before Step 02 to revalidate the current codebase context for this new refactor scope.
