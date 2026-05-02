package com.example.demo.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DivideOperation implements Operation {

    @Override
    public double apply(double left, double right) {
        return apply(left, right, CalculationContext.DEFAULT);
    }

    public double apply(double left, double right, CalculationContext context) {
        if (right == 0.0) {
            throw new ArithmeticException("Division by zero");
        }
        BigDecimal a = BigDecimal.valueOf(left);
        BigDecimal b = BigDecimal.valueOf(right);
        return a.divide(b, context.scale(), context.roundingMode()).doubleValue();
    }
}
