package com.example.demo.domain.expression;

public sealed interface Expression permits ExpressionLiteral, Add, Subtract, Multiply, Divide, Power, Root {
    Result evaluate();

    static Expression literal(double value) {
        return new ExpressionLiteral(value);
    }

    static Expression add(Expression left, Expression right) {
        return new Add(left, right);
    }

    static Expression subtract(Expression left, Expression right) {
        return new Subtract(left, right);
    }

    static Expression multiply(Expression left, Expression right) {
        return new Multiply(left, right);
    }

    static Expression divide(Expression left, Expression right) {
        return new Divide(left, right);
    }

    static Expression power(Expression base, Expression exponent) {
        return new Power(base, exponent);
    }

    static Expression root(Expression radicand, Expression index) {
        return new Root(radicand, index);
    }

    interface Result {
        double value();
    }
}