package com.example.demo.domain;

import java.math.RoundingMode;

public record CalculationContext(int scale, RoundingMode roundingMode) {

    public static final int DEFAULT_SCALE = 10;
    public static final int MIN_SCALE = 1;
    public static final int MAX_SCALE = 16;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    public static CalculationContext defaults() {
        return new CalculationContext(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }
}
