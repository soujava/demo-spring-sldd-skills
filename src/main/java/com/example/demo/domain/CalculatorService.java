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
        if (divisor == 0.0) {
            throw new ArithmeticException("Division by zero");
        }
        BigDecimal a = BigDecimal.valueOf(dividend);
        BigDecimal b = BigDecimal.valueOf(divisor);
        BigDecimal result = a.divide(b);
        return result.doubleValue();
    }

    public double power(double base, double exponent) {
        double result = Math.pow(base, exponent);
        if (Double.isInfinite(result)) {
            throw new NumericOverflowException("Numeric overflow: result is too large");
        }
        if (Double.isNaN(result)) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        return result;
    }

    public double root(double radicand, double index) {
        if (index == 0.0) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        double result = Math.pow(radicand, 1.0 / index);
        if (Double.isInfinite(result)) {
            throw new NumericOverflowException("Numeric overflow: result is too large");
        }
        if (Double.isNaN(result)) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        return result;
    }

    public double evaluate(Expression expression) {
        return switch (expression) {
            case Literal(double value) -> value;
            case BinaryOperation(Operator op, Expression left, Expression right) -> {
                double leftVal = evaluate(left);
                double rightVal = evaluate(right);
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
