package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.demo.domain.expression.Expression.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Divide expression")
class CalculatorServiceDivideTest {

    @Test
    @DisplayName("divides two literals")
    void divideTwoLiterals() {
        var expr = divide(literal(6.0), literal(3.0));
        assertEquals(2.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("divides by zero throws ArithmeticException")
    void divideByZero() {
        var expr = divide(literal(5.0), literal(0.0));
        assertThrows(ArithmeticException.class, expr::evaluate);
    }

    @Test
    @DisplayName("divide zero by non-zero")
    void zeroDividedBy() {
        var expr = divide(literal(0.0), literal(5.0));
        assertEquals(0.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("divide negative numbers")
    void divideNegatives() {
        var expr = divide(literal(-10.0), literal(-2.0));
        assertEquals(5.0, expr.evaluate().value());
    }

    @Test
    @DisplayName("propagates ArithmeticException from nested operation")
    void propagatesException() {
        var expr = divide(add(literal(1.0), literal(2.0)), literal(0.0));
        assertThrows(ArithmeticException.class, expr::evaluate);
    }
}