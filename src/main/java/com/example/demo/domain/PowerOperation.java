package com.example.demo.domain;

public class PowerOperation implements Operation {

    @Override
    public double apply(double left, double right) {
        double result = Math.pow(left, right);
        if (Double.isInfinite(result)) {
            throw new NumericOverflowException("Numeric overflow: result is too large");
        }
        if (Double.isNaN(result)) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        return result;
    }
}
