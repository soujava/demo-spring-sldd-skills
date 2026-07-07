package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Multiply")
class MultiplyTest {

    @Test
    @DisplayName("R1.1 evaluate two literals returns product")
    void evaluate_TwoLiterals_ReturnsProduct() {
        Expression expression = Expression.multiply(Expression.literal(2.0), Expression.literal(3.0));

        Literal result = expression.evaluate();

        assertEquals(6.0, result.value());
    }

    @Test
    @DisplayName("R1.5 evaluate with BigDecimal precision")
    void evaluate_WithBigDecimalPrecision() {
        Expression expression = Expression.multiply(Expression.literal(0.1), Expression.literal(0.2));

        Literal result = expression.evaluate();

        assertEquals(0.02, result.value(), 0.0001);
    }
}
