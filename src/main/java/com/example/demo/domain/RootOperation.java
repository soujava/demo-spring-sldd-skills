package com.example.demo.domain;

public class RootOperation implements Operation {

    @Override
    public double apply(double left, double right) {
        if (right == 0.0) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        double result = Math.pow(left, 1.0 / right);
        if (Double.isInfinite(result)) {
            throw new NumericOverflowException("Numeric overflow: result is too large");
        }
        if (Double.isNaN(result)) {
            throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
        }
        return result;
    }
}
