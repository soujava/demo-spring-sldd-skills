package com.example.demo.domain;

import java.math.BigDecimal;

public record Power(Expression base, Expression exponent) implements Expression {

    @Override
    public Literal evaluate(CalculationContext context) {
        double result = Math.pow(base.evaluate(context).value(), exponent.evaluate(context).value());
        if (Double.isInfinite(result)) {
            throw new NumericOverflowException("Numeric overflow: result is too large");
        }
        if (Double.isNaN(result)) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        return new Literal(BigDecimal.valueOf(result)
                .setScale(context.scale(), context.roundingMode())
                .doubleValue());
    }
}
