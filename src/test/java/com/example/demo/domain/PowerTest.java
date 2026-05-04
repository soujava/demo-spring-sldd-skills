package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Power")
class PowerTest {

    @Test
    @DisplayName("evaluate returns power")
    void evaluate_ReturnsPower() {
        Expression expression = Expression.power(Expression.literal(2.0), Expression.literal(3.0));

        Literal result = expression.evaluate();

        assertEquals(8.0, result.value());
    }

    @Test
    @DisplayName("evaluate overflow throws NumericOverflowException")
    void evaluate_Overflow_ThrowsNumericOverflowException() {
        Expression expression = Expression.power(Expression.literal(2.0), Expression.literal(1024.0));

        assertThrows(NumericOverflowException.class, expression::evaluate);
    }

    @Test
    @DisplayName("evaluate with context applies scale")
    void evaluate_WithContext_AppliesScale() {
        Expression expression = Expression.power(Expression.literal(2.0), Expression.literal(0.5));
        CalculationContext context = new CalculationContext(4, java.math.RoundingMode.HALF_UP);

        Literal result = expression.evaluate(context);

        assertEquals(1.4142, result.value());
    }
}
