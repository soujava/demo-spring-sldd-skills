package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Sum")
class SumTest {

    @Test
    @DisplayName("evaluate two literals returns sum")
    void evaluate_TwoLiterals_ReturnsSum() {
        Expression expression = Expression.sum(Expression.literal(1.5), Expression.literal(2.5));

        Literal result = expression.evaluate();

        assertEquals(4.0, result.value());
    }

    @Test
    @DisplayName("evaluate nested expression returns correct result")
    void evaluate_NestedExpression_ReturnsCorrectResult() {
        Expression inner = Expression.sum(Expression.literal(2.0), Expression.literal(3.0));
        Expression expression = Expression.sum(Expression.literal(1.0), inner);

        Literal result = expression.evaluate();

        assertEquals(6.0, result.value());
    }
}
