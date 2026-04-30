package com.example.demo.domain.expression;

import com.example.demo.domain.NumericOverflowException;

public record Root(Expression radicand, Expression index) implements Expression {
    @Override
    public ExpressionLiteral evaluate() {
        double r = radicand.evaluate().value();
        double i = index.evaluate().value();
        if (i == 0.0) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        double result = Math.pow(r, 1.0 / i);
        if (Double.isInfinite(result)) {
            throw new NumericOverflowException("Numeric overflow: result is too large");
        }
        if (Double.isNaN(result)) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        return new ExpressionLiteral(result);
    }
}