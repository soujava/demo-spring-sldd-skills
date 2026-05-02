package com.example.demo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CalculationContext")
class CalculationContextTest {

    @Test
    @DisplayName("defaults usa scale 10 e HALF_UP")
    void defaultsUseScaleTenAndHalfUp() {
        CalculationContext context = CalculationContext.defaults();

        assertEquals(10, context.scale());
        assertEquals(RoundingMode.HALF_UP, context.roundingMode());
    }
}
