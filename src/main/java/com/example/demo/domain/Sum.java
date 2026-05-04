package com.example.demo.domain;

import java.math.BigDecimal;

public record Sum(Expression firstAddend, Expression secondAddend) implements Expression {

    @Override
    public Literal evaluate(CalculationContext context) {
        var a = BigDecimal.valueOf(firstAddend.evaluate(context).value());
        var b = BigDecimal.valueOf(secondAddend.evaluate(context).value());
        return new Literal(a.add(b).doubleValue());
    }
}
