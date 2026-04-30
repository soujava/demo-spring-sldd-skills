package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.demo.domain.expression.Expression.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Subtract expression")
class CalculatorServiceSubtractionTest {

    @Test
    @DisplayName("subtracts two literals")
    void subtractTwoLiterals() {
        var expr = subtract(literal(5.0), literal(3.2));
        assertEquals(1.8, expr.evaluate().value());
    }

    @Test
    @DisplayName("subtracts zero from value")
    void subtractZero() {
        var expr = subtract(literal(5.0), literal(0.0));
        assertEquals(5.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("subtracts value from zero")
    void subtractFromZero() {
        var expr = subtract(literal(0.0), literal(5.0));
        assertEquals(-5.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("subtracts negative numbers")
    void subtractNegatives() {
        var expr = subtract(literal(-2.5), literal(-4.5));
        assertEquals(2.0, expr.evaluate().value()); // -2.5 - (-4.5) = 2.0
    }

    @Test
    @DisplayName("propagates ArithmeticException from nested operation")
    void propagatesException() {
        var expr = subtract(literal(1.0), divide(literal(1.0), literal(0.0)));
        assertThrows(ArithmeticException.class, expr::evaluate);
    }
}