package com.example.demo.domain;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    public double sum(double firstAddend, double secondAddend) {
        // Convert doubles to BigDecimal deterministically and sum to avoid
        // floating-point precision issues. We use BigDecimal.valueOf(double)
        // which is preferred over new BigDecimal(double) for predictable results.
        BigDecimal a = BigDecimal.valueOf(firstAddend);
        BigDecimal b = BigDecimal.valueOf(secondAddend);
        BigDecimal result = a.add(b);
        return result.doubleValue();
    }

    public double subtract(double minuend, double subtrahend) {
        return minuend - subtrahend;
    }

    public double multiply(double multiplicand, double multiplier) {
        BigDecimal a = BigDecimal.valueOf(multiplicand);
        BigDecimal b = BigDecimal.valueOf(multiplier);
        BigDecimal result = a.multiply(b);
        return result.doubleValue();
    }

    public double divide(double dividend, double divisor) {
        return divide(dividend, divisor, CalculationContext.defaults());
    }

    public double divide(double dividend, double divisor, CalculationContext context) {
        if (divisor == 0.0) {
            throw new ArithmeticException("Division by zero");
        }
        BigDecimal a = BigDecimal.valueOf(dividend);
        BigDecimal b = BigDecimal.valueOf(divisor);
        BigDecimal result = a.divide(b, context.scale(), context.roundingMode());
        return result.doubleValue();
    }

    public double power(double base, double exponent) {
        return power(base, exponent, CalculationContext.defaults());
    }

    public double power(double base, double exponent, CalculationContext context) {
        return roundedFinitePowerResult(Math.pow(base, exponent), context);
    }

    public double root(double radicand, double index) {
        return root(radicand, index, CalculationContext.defaults());
    }

    public double root(double radicand, double index, CalculationContext context) {
        if (index == 0.0) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        double result = Math.pow(radicand, 1.0 / index);
        return roundedFinitePowerResult(result, context);
    }

    private double roundedFinitePowerResult(double result, CalculationContext context) {
        if (Double.isInfinite(result)) {
            throw new NumericOverflowException("Numeric overflow: result is too large");
        }
        if (Double.isNaN(result)) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        return BigDecimal.valueOf(result)
                .setScale(context.scale(), context.roundingMode())
                .doubleValue();
    }

    public double evaluate(Expression expression) {
        return evaluate(expression, CalculationContext.defaults());
    }

    public double evaluate(Expression expression, CalculationContext context) {
        return switch (expression) {
            case Literal(double value) -> value;
            case BinaryOperation(Operator op, Expression left, Expression right) -> {
                double leftVal = evaluate(left, context);
                double rightVal = evaluate(right, context);
                yield switch (op) {
                    case SUM -> sum(leftVal, rightVal);
                    case SUBTRACT -> subtract(leftVal, rightVal);
                    case MULTIPLY -> multiply(leftVal, rightVal);
                    case DIVIDE -> divide(leftVal, rightVal, context);
                    case POWER -> power(leftVal, rightVal, context);
                    case ROOT -> root(leftVal, rightVal, context);
                };
            }
        };
    }
}
