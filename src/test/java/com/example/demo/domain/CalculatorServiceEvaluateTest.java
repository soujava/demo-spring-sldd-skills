package com.example.demo.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CalculatorService.evaluate()")
public class CalculatorServiceEvaluateTest {

    private final CalculatorService calculatorService = new CalculatorService();

    @Test
    void evaluate_Literal_ReturnsValue() {
        Expression expression = new Literal(10.0);
        double result = calculatorService.evaluate(expression);
        assertEquals(10.0, result);
    }

    @Test
    void evaluate_SimpleSum_ReturnsCorrectResult() {
        Expression expression = new BinaryOperation(Operator.SUM, new Literal(10.0), new Literal(5.0));
        double result = calculatorService.evaluate(expression);
        assertEquals(15.0, result);
    }

    @Test
    void evaluate_NestedOperations_RespectsOrder() {
        // (10 + 5) * 2 = 30
        Expression expression = new BinaryOperation(Operator.MULTIPLY,
            new BinaryOperation(Operator.SUM, new Literal(10.0), new Literal(5.0)),
            new Literal(2.0)
        );
        double result = calculatorService.evaluate(expression);
        assertEquals(30.0, result);
    }

    @Test
    void evaluate_DivisionByZero_ThrowsArithmeticException() {
        Expression expression = new BinaryOperation(Operator.DIVIDE, new Literal(10.0), new Literal(0.0));
        assertThrows(ArithmeticException.class, () -> calculatorService.evaluate(expression));
    }
}
