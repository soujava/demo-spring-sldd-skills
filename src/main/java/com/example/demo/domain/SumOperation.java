package com.example.demo.domain;

import java.math.BigDecimal;

public class SumOperation implements Operation {
    @Override
    public double apply(double left, double right) {
        BigDecimal a = BigDecimal.valueOf(left);
        BigDecimal b = BigDecimal.valueOf(right);
        return a.add(b).doubleValue();
    }
}
