package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.RoundingMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DivideOperation")
class DivideOperationTest {

    private DivideOperation operation;

    @BeforeEach
    void setUp() {
        operation = new DivideOperation();
    }

    @Test
    @DisplayName("retorna 0.3333333333 quando 1 / 3 com default (scale=10, HALF_UP)")
    void divideWithDefaultScale() {
        double result = operation.apply(1.0, 3.0);
        assertThat(result).isEqualTo(0.3333333333);
    }

    @Test
    @DisplayName("retorna 0.3333 quando 1 / 3 com scale=4 e HALF_DOWN")
    void divideWithExplicitScaleAndRounding() {
        double result = operation.apply(1.0, 3.0, new CalculationContext(4, RoundingMode.HALF_DOWN));
        assertThat(result).isEqualTo(0.3333);
    }

    @Test
    @DisplayName("lanca ArithmeticException quando divisor e zero")
    void divideByZeroThrowsException() {
        assertThatThrownBy(() -> operation.apply(1.0, 0.0))
            .isInstanceOf(ArithmeticException.class);
    }

    @Test
    @DisplayName("retorna 0.0 quando 1 / 3 com scale=0 e HALF_UP")
    void divideWithScaleZero() {
        double result = operation.apply(1.0, 3.0, new CalculationContext(0, RoundingMode.HALF_UP));
        assertThat(result).isEqualTo(0.0);
    }
}
