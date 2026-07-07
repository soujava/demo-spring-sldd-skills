package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Divide")
class DivideTest {

    @Test
    @DisplayName("R1.1 evaluate returns quotient")
    void evaluate_ReturnsQuotient() {
        Expression expression = Expression.divide(Expression.literal(6.0), Expression.literal(2.0));

        Literal result = expression.evaluate();

        assertEquals(3.0, result.value());
    }

    @Test
    @DisplayName("R1.5 evaluate with context applies scale and rounding")
    void evaluate_WithContext_ScaleAndRounding() {
        Expression expression = Expression.divide(Expression.literal(1.0), Expression.literal(3.0));
        CalculationContext context = new CalculationContext(2, java.math.RoundingMode.HALF_UP);

        Literal result = expression.evaluate(context);

        assertEquals(0.33, result.value());
    }

    @Test
    @DisplayName("R2.4 evaluate divisor zero throws ArithmeticException")
    void evaluate_DivisorZero_ThrowsArithmeticException() {
        Expression expression = Expression.divide(Expression.literal(10.0), Expression.literal(0.0));

        ArithmeticException exception = assertThrows(ArithmeticException.class, expression::evaluate);
        assertEquals("Division by zero", exception.getMessage());
    }
}
