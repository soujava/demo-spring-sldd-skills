package com.example.demo.domain;

import java.math.BigDecimal;

public record Subtract(Expression minuend, Expression subtrahend) implements Expression {

    @Override
    public Literal evaluate(CalculationContext context) {
        var a = BigDecimal.valueOf(minuend.evaluate(context).value());
        var b = BigDecimal.valueOf(subtrahend.evaluate(context).value());
        return new Literal(a.subtract(b).doubleValue());
    }
}
