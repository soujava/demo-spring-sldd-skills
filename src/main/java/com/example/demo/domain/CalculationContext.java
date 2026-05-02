package com.example.demo.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record CalculationContext(int scale, RoundingMode roundingMode) {
    public static final CalculationContext DEFAULT = new CalculationContext(10, RoundingMode.HALF_UP);
}
