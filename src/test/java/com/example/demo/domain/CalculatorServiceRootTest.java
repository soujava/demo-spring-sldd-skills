package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.demo.domain.expression.Expression.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Root expression")
class CalculatorServiceRootTest {

    @Test
    @DisplayName("calculates root correctly")
    void calculateRoot() {
        // cube root of 27
        assertEquals(3.0, root(literal(27.0), literal(3.0)).evaluate().value());
        // square root of 16
        assertEquals(4.0, root(literal(16.0), literal(2.0)).evaluate().value());
        // root with index 1 returns the radicand
        assertEquals(5.0, root(literal(5.0), literal(1.0)).evaluate().value());
    }

    @Test
    @DisplayName("throws ArithmeticException when index is zero")
    void throwsOnZeroIndex() {
        assertThrows(ArithmeticException.class, () -> root(literal(5.0), literal(0.0)).evaluate());
    }

    @Test
    @DisplayName("throws NumericOverflowException when result overflows")
    void throwsOnOverflow() {
        // a large radicand with small index can overflow
        assertThrows(NumericOverflowException.class, () -> root(literal(1.0E200), literal(0.1)).evaluate());
    }

    @Test
    @DisplayName("throws ArithmeticException when result is imaginary (NaN)")
    void throwsOnImaginaryResult() {
        // negative radicand with fractional index that is not odd integer root -> NaN
        assertThrows(ArithmeticException.class, () -> root(literal(-16.0), literal(4.0)).evaluate());
    }

    @Test
    @DisplayName("propagates ArithmeticException from nested operation")
    void propagatesException() {
        var expr = root(add(literal(2.0), literal(2.0)), literal(2.0)); // sqrt(4) = 2
        assertEquals(2.0, expr.evaluate().value());
    }
}