package com.example.demo.domain;

import java.math.BigDecimal;

public record Divide(Expression dividend, Expression divisor) implements Expression {

    @Override
    public Literal evaluate(CalculationContext context) {
        double divisorValue = divisor.evaluate(context).value();
        if (divisorValue == 0.0) {
            throw new ArithmeticException("Division by zero");
        }
        var a = BigDecimal.valueOf(dividend.evaluate(context).value());
        var b = BigDecimal.valueOf(divisorValue);
        return new Literal(a.divide(b, context.scale(), context.roundingMode()).doubleValue());
    }
}
