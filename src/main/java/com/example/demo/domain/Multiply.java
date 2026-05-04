package com.example.demo.domain;

import java.math.BigDecimal;

public record Multiply(Expression multiplicand, Expression multiplier) implements Expression {

    @Override
    public Literal evaluate(CalculationContext context) {
        var a = BigDecimal.valueOf(multiplicand.evaluate(context).value());
        var b = BigDecimal.valueOf(multiplier.evaluate(context).value());
        return new Literal(a.multiply(b).doubleValue());
    }
}
