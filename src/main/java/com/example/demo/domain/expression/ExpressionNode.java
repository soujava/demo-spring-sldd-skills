package com.example.demo.domain.expression;

public record ExpressionNode(ExpressionOperation operation, Expression left, Expression right) implements Expression {
}
