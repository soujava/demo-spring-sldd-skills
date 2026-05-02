package com.example.demo.domain;

public record BinaryOperation(Operator operator, Expression left, Expression right, CalculationContext context) implements Expression {

    public BinaryOperation(Operator operator, Expression left, Expression right) {
        this(operator, left, right, null);
    }
}
