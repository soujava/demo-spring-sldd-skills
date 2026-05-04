package com.example.demo.domain;

import java.math.BigDecimal;

public record Root(Expression radicand, Expression index) implements Expression {

    @Override
    public Literal evaluate(CalculationContext context) {
        double indexValue = index.evaluate(context).value();
        if (indexValue == 0.0) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        double result = Math.pow(radicand.evaluate(context).value(), 1.0 / indexValue);
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
