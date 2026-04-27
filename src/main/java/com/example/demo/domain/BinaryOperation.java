package com.example.demo.domain;

public record BinaryOperation(Operator operator, Expression left, Expression right) implements Expression {
}
