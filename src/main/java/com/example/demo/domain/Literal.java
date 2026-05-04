package com.example.demo.domain;

public record Literal(double value) implements Expression {

    @Override
    public Literal evaluate(CalculationContext context) {
        return this;
    }
}
