package com.example.demo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ContextualExpression")
class ContextualExpressionTest {

    @Test
    @DisplayName("evaluate usa contexto local completo para expressao interna")
    void evaluate_UsesCompleteLocalContext_ForWrappedExpression() {
        Expression expression = new ContextualExpression(
                Expression.divide(Expression.literal(1.0), Expression.literal(3.0)),
                new CalculationContextOverride(2, RoundingMode.HALF_UP));

        Literal result = expression.evaluate(new CalculationContext(6, RoundingMode.HALF_EVEN));

        assertEquals(0.33, result.value());
    }

    @Test
    @DisplayName("evaluate herda campos ausentes do contexto recebido")
    void evaluate_InheritsMissingFields_FromInheritedContext() {
        Expression expression = new ContextualExpression(
                Expression.divide(Expression.literal(1.0), Expression.literal(3.0)),
                new CalculationContextOverride(2, null));

        Literal result = expression.evaluate(new CalculationContext(6, RoundingMode.HALF_EVEN));

        assertEquals(0.33, result.value());
    }

    @Test
    @DisplayName("evaluate sem contexto explicito usa defaults")
    void evaluate_WithoutExplicitContext_UsesDefaults() {
        Expression expression = new ContextualExpression(
                Expression.divide(Expression.literal(1.0), Expression.literal(3.0)),
                new CalculationContextOverride(2, null));

        Literal result = expression.evaluate();

        assertEquals(0.33, result.value());
    }

    @Test
    @DisplayName("factory Expression.contextual cria expressao contextual")
    void contextualFactory_CreatesContextualExpression() {
        Expression expression = Expression.contextual(
                Expression.divide(Expression.literal(1.0), Expression.literal(3.0)),
                new CalculationContextOverride(2, RoundingMode.HALF_UP));

        Literal result = expression.evaluate(new CalculationContext(6, RoundingMode.HALF_EVEN));

        assertEquals(0.33, result.value());
    }
}
