package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.demo.domain.expression.Expression.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Multiply expression")
class CalculatorServiceMultiplyTest {

    @Test
    @DisplayName("multiplies two literals")
    void multiplyTwoLiterals() {
        var expr = multiply(literal(2.0), literal(3.0));
        assertEquals(6.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("multiplies by zero")
    void multiplyByZero() {
        var expr = multiply(literal(5.0), literal(0.0));
        assertEquals(0.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("multiplies by one")
    void multiplyByOne() {
        var expr = multiply(literal(5.0), literal(1.0));
        assertEquals(5.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("multiplies negative numbers")
    void multiplyNegatives() {
        var expr = multiply(literal(-2.5), literal(-4.0));
        assertEquals(10.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("multiplies with decimal precision")
    void multiplyDecimalPrecision() {
        var expr = multiply(literal(0.1), literal(0.2));
        assertEquals(0.02, expr.evaluate().value(), 1.0E-9);
    }

    @Test
    @DisplayName("multiplies large numbers")
    void multiplyLargeNumbers() {
        var expr = multiply(literal(1.0E150), literal(2.0E150));
        assertEquals(2.0E300, expr.evaluate().value());
    }

    @Test
    @DisplayName("propagates ArithmeticException from nested operation")
    void propagatesException() {
        var expr = multiply(literal(2.0), divide(literal(1.0), literal(0.0)));
        assertThrows(ArithmeticException.class, expr::evaluate);
    }
}