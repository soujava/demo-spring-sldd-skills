package com.example.demo.domain;

public sealed interface Expression permits Literal, Sum, Subtract, Multiply, Divide, Power, Root, ContextualExpression {

    Literal evaluate(CalculationContext context);

    default Literal evaluate() {
        return evaluate(CalculationContext.defaults());
    }

    static Expression literal(double value) {
        return new Literal(value);
    }

    static Expression sum(Expression firstAddend, Expression secondAddend) {
        return new Sum(firstAddend, secondAddend);
    }

    static Expression subtract(Expression minuend, Expression subtrahend) {
        return new Subtract(minuend, subtrahend);
    }

    static Expression multiply(Expression multiplicand, Expression multiplier) {
        return new Multiply(multiplicand, multiplier);
    }

    static Expression divide(Expression dividend, Expression divisor) {
        return new Divide(dividend, divisor);
    }

    static Expression power(Expression base, Expression exponent) {
        return new Power(base, exponent);
    }

    static Expression root(Expression radicand, Expression index) {
        return new Root(radicand, index);
    }

    static Expression contextual(Expression expression, CalculationContextOverride context) {
        return new ContextualExpression(expression, context);
    }
}
