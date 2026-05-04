package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Literal")
class LiteralTest {

    @Test
    @DisplayName("evaluate returns itself")
    void evaluate_ReturnsItself() {
        Literal literal = new Literal(10.0);

        Literal result = literal.evaluate();

        assertEquals(10.0, result.value());
    }

    @Test
    @DisplayName("evaluate with context returns itself")
    void evaluate_WithContext_ReturnsItself() {
        Literal literal = new Literal(5.5);
        CalculationContext context = CalculationContext.defaults();

        Literal result = literal.evaluate(context);

        assertEquals(5.5, result.value());
    }
}
