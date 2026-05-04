package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Root")
class RootTest {

    @Test
    @DisplayName("evaluate returns root")
    void evaluate_ReturnsRoot() {
        Expression expression = Expression.root(Expression.literal(8.0), Expression.literal(3.0));

        Literal result = expression.evaluate();

        assertEquals(2.0, result.value());
    }

    @Test
    @DisplayName("evaluate index zero throws ArithmeticException")
    void evaluate_IndexZero_ThrowsArithmeticException() {
        Expression expression = Expression.root(Expression.literal(8.0), Expression.literal(0.0));

        ArithmeticException exception = assertThrows(ArithmeticException.class, expression::evaluate);
        assertEquals("Invalid operation: result is undefined or imaginary", exception.getMessage());
    }

    @Test
    @DisplayName("evaluate negative radicand even index NaN throws ArithmeticException")
    void evaluate_NegativeRadicandEvenIndex_ThrowsArithmeticException() {
        Expression expression = Expression.root(Expression.literal(-4.0), Expression.literal(2.0));

        ArithmeticException exception = assertThrows(ArithmeticException.class, expression::evaluate);
        assertEquals("Invalid operation: result is undefined or imaginary", exception.getMessage());
    }
}
