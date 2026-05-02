package com.example.demo.domain;

import java.math.BigDecimal;

public class SubtractOperation implements Operation {
    @Override
    public double apply(double left, double right) {
        BigDecimal a = BigDecimal.valueOf(left);
        BigDecimal b = BigDecimal.valueOf(right);
        return a.subtract(b).doubleValue();
    }
}
