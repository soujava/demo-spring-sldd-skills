package com.example.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Expression Tree")
class ExpressionTreeTest {

    @Test
    @DisplayName("evaluate nested sum and multiply respects order")
    void evaluate_NestedSumAndMultiply_RespectsOrder() {
        // (1 + 2) * 3 = 9
        Expression sum = Expression.sum(Expression.literal(1.0), Expression.literal(2.0));
        Expression expression = Expression.multiply(sum, Expression.literal(3.0));

        Literal result = expression.evaluate();

        assertEquals(9.0, result.value());
    }

    @Test
    @DisplayName("evaluate complex tree with multiple operations")
    void evaluate_ComplexTree_MultipleOperations() {
        // ((10 - 3) * 2) + (8 / 4) = 14 + 2 = 16
        Expression subtract = Expression.subtract(Expression.literal(10.0), Expression.literal(3.0));
        Expression multiply = Expression.multiply(subtract, Expression.literal(2.0));
        Expression divide = Expression.divide(Expression.literal(8.0), Expression.literal(4.0));
        Expression expression = Expression.sum(multiply, divide);

        Literal result = expression.evaluate();

        assertEquals(16.0, result.value());
    }

    @Test
    @DisplayName("evaluate with inherited context propagates to subexpressions")
    void evaluate_WithInheritedContext_Propagates() {
        // (1/3) * 1 = 0.33 (with scale=2)
        Expression divide = Expression.divide(Expression.literal(1.0), Expression.literal(3.0));
        Expression expression = Expression.multiply(divide, Expression.literal(1.0));
        CalculationContext context = new CalculationContext(2, java.math.RoundingMode.HALF_UP);

        Literal result = expression.evaluate(context);

        assertEquals(0.33, result.value());
    }
}
