package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.demo.domain.expression.Expression.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Power expression")
class CalculatorServicePowerTest {

    @Test
    @DisplayName("calculates power correctly")
    void calculatePower() {
        assertEquals(8.0, power(literal(2.0), literal(3.0)).evaluate().value());
        assertEquals(1.0, power(literal(5.0), literal(0.0)).evaluate().value());
        assertEquals(0.0, power(literal(0.0), literal(5.0)).evaluate().value());
    }

    @Test
    @DisplayName("throws NumericOverflowException when result overflows")
    void throwsOnOverflow() {
        assertThrows(NumericOverflowException.class, () -> power(literal(10.0), literal(1000.0)).evaluate());
    }

    @Test
    @DisplayName("throws ArithmeticException when result is imaginary (NaN)")
    void throwsOnImaginaryResult() {
        assertThrows(ArithmeticException.class, () -> power(literal(-4.0), literal(0.5)).evaluate());
    }

    @Test
    @DisplayName("propagates ArithmeticException from nested operation")
    void propagatesException() {
        var expr = power(add(literal(2.0), literal(3.0)), literal(2.0)); // (2+3)^2 = 25
        assertEquals(25.0, expr.evaluate().value());
    }
}