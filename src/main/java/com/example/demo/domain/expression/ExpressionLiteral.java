package com.example.demo.domain.expression;

public record ExpressionLiteral(double value) implements Expression, Expression.Result {
    @Override
    public ExpressionLiteral evaluate() {
        return this;
    }
}