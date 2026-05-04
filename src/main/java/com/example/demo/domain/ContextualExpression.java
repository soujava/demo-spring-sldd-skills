package com.example.demo.domain;

public record ContextualExpression(Expression expression, CalculationContextOverride context) implements Expression {

    @Override
    public Literal evaluate(CalculationContext inheritedContext) {
        return expression.evaluate(context.resolve(inheritedContext));
    }
}
