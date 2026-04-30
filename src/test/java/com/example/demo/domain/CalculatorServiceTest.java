package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.demo.domain.expression.Expression.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Add expression")
class CalculatorServiceTest {

    @Test
    @DisplayName("literal returns correct value")
    void literalValue() {
        assertEquals(4.2, literal(4.2).evaluate().value());
    }

    @Test
    @DisplayName("adds two literals")
    void addTwoLiterals() {
        var expr = add(literal(1.5), literal(2.5));
        assertEquals(4.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("adds negative and positive")
    void addNegativeAndPositive() {
        var expr = add(literal(-2.5), literal(4.0));
        assertEquals(1.5, expr.evaluate().value());
    }

    @Test
    @DisplayName("adds two negatives")
    void addTwoNegatives() {
        var expr = add(literal(-2.5), literal(-4.5));
        assertEquals(-7.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("add with zero")
    void addWithZero() {
        var expr = add(literal(0.0), literal(7.4));
        assertEquals(7.4, expr.evaluate().value());
    }

    @Test
    @DisplayName("add opposite values")
    void addOppositeValues() {
        var expr = add(literal(-5.0), literal(5.0));
        assertEquals(0.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("add large numbers")
    void addLargeNumbers() {
        var expr = add(literal(1.0E307), literal(2.0E307));
        assertEquals(3.0E307, expr.evaluate().value());
    }

    @Test
    @DisplayName("add decimal precision")
    void addDecimalPrecision() {
        var expr = add(literal(0.1), literal(0.2));
        assertEquals(0.3, expr.evaluate().value(), 1.0E-9);
    }

    @Test
    @DisplayName("propagates ArithmeticException from nested operation")
    void propagatesException() {
        var expr = add(literal(1.0), divide(literal(1.0), literal(0.0)));
        assertThrows(ArithmeticException.class, expr::evaluate);
    }
}