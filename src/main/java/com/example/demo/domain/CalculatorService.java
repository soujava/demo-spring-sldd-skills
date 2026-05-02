package com.example.demo.domain;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    private final OperationRegistry operationRegistry;

    public CalculatorService(OperationRegistry operationRegistry) {
        this.operationRegistry = operationRegistry;
    }

    public double sum(double firstAddend, double secondAddend) {
        return operationRegistry.lookup(Operator.SUM).apply(firstAddend, secondAddend);
    }

    public double subtract(double minuend, double subtrahend) {
        return operationRegistry.lookup(Operator.SUBTRACT).apply(minuend, subtrahend);
    }

    public double multiply(double multiplicand, double multiplier) {
        return operationRegistry.lookup(Operator.MULTIPLY).apply(multiplicand, multiplier);
    }

    public double divide(double dividend, double divisor) {
        DivideOperation op = (DivideOperation) operationRegistry.lookup(Operator.DIVIDE);
        return op.apply(dividend, divisor);
    }

    public double divide(double dividend, double divisor, CalculationContext context) {
        DivideOperation op = (DivideOperation) operationRegistry.lookup(Operator.DIVIDE);
        return op.apply(dividend, divisor, context);
    }

    public double power(double base, double exponent) {
        return operationRegistry.lookup(Operator.POWER).apply(base, exponent);
    }

    public double root(double radicand, double index) {
        return operationRegistry.lookup(Operator.ROOT).apply(radicand, index);
    }

    public double evaluate(Expression expression) {
        return switch (expression) {
            case Literal(double value) -> value;
            case BinaryOperation(Operator op, Expression left, Expression right, CalculationContext ctx) -> {
                double leftVal = evaluate(left);
                double rightVal = evaluate(right);
                if (op == Operator.DIVIDE && ctx != null) {
                    yield divide(leftVal, rightVal, ctx);
                }
                yield switch (op) {
                    case SUM -> sum(leftVal, rightVal);
                    case SUBTRACT -> subtract(leftVal, rightVal);
                    case MULTIPLY -> multiply(leftVal, rightVal);
                    case DIVIDE -> divide(leftVal, rightVal);
                    case POWER -> power(leftVal, rightVal);
                    case ROOT -> root(leftVal, rightVal);
                };
            }
        };
    }
}
