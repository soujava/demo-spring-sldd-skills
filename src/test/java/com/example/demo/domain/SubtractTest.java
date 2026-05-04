package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Subtract")
class SubtractTest {

    @Test
    @DisplayName("evaluate returns difference")
    void evaluate_ReturnsDifference() {
        Expression expression = Expression.subtract(Expression.literal(5.0), Expression.literal(3.0));

        Literal result = expression.evaluate();

        assertEquals(2.0, result.value());
    }
}
