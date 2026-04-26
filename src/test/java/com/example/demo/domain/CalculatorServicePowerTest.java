package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorServicePowerTest {

    private final CalculatorService calculatorService = new CalculatorService();

    @Test
    @DisplayName("Should calculate power correctly")
    void shouldCalculatePowerCorrectly() {
        assertEquals(8.0, calculatorService.power(2.0, 3.0));
        assertEquals(1.0, calculatorService.power(5.0, 0.0));
        assertEquals(0.0, calculatorService.power(0.0, 5.0));
    }

    @Test
    @DisplayName("Should throw NumericOverflowException when result overflows")
    void shouldThrowExceptionOnOverflow() {
        assertThrows(NumericOverflowException.class, () -> calculatorService.power(10.0, 1000.0));
    }

    @Test
    @DisplayName("Should throw ArithmeticException when result is imaginary (NaN)")
    void shouldThrowExceptionOnImaginaryResult() {
        assertThrows(ArithmeticException.class, () -> calculatorService.power(-4.0, 0.5));
    }
}
