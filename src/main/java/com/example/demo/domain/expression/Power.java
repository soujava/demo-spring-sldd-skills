package com.example.demo.domain.expression;

import com.example.demo.domain.NumericOverflowException;

public record Power(Expression base, Expression exponent) implements Expression {
    @Override
    public ExpressionLiteral evaluate() {
        double b = base.evaluate().value();
        double e = exponent.evaluate().value();
        double result = Math.pow(b, e);
        if (Double.isInfinite(result)) {
            throw new NumericOverflowException("Numeric overflow: result is too large");
        }
        if (Double.isNaN(result)) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        return new ExpressionLiteral(result);
    }
}