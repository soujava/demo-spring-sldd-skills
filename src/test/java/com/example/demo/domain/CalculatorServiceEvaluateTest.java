package com.example.demo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.domain.expression.ExpressionContext;
import com.example.demo.domain.expression.ExpressionLiteral;
import com.example.demo.domain.expression.ExpressionNode;
import com.example.demo.domain.expression.ExpressionOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CalculatorService.evaluate()")
class CalculatorServiceEvaluateTest {

	private CalculatorService service;

	@BeforeEach
	void setUp() {
		service = new CalculatorService();
	}

	@Test
	@DisplayName("retorna 12.0 para ADD(10, 2)")
	void evaluateAdd() {
		ExpressionNode expr = new ExpressionNode(
			ExpressionOperation.ADD,
			new ExpressionLiteral(10),
			new ExpressionLiteral(2),
			ExpressionContext.DEFAULT
		);
		assertEquals(12.0, service.evaluate(expr));
	}

	@Test
	@DisplayName("retorna 2.0 para SUBTRACT(5, 3) com BigDecimal")
	void evaluateSubtract() {
		ExpressionNode expr = new ExpressionNode(
			ExpressionOperation.SUBTRACT,
			new ExpressionLiteral(5),
			new ExpressionLiteral(3),
			ExpressionContext.DEFAULT
		);
		assertEquals(2.0, service.evaluate(expr));
	}

	@Test
	@DisplayName("retorna 12.0 para MULTIPLY(4, 3)")
	void evaluateMultiply() {
		ExpressionNode expr = new ExpressionNode(
			ExpressionOperation.MULTIPLY,
			new ExpressionLiteral(4),
			new ExpressionLiteral(3),
			ExpressionContext.DEFAULT
		);
		assertEquals(12.0, service.evaluate(expr));
	}

	@Test
	@DisplayName("retorna 3.3333333333 para DIVIDE(10, 3) com DEFAULT context")
	void evaluateDivideWithDefaultContext() {
		ExpressionNode expr = new ExpressionNode(
			ExpressionOperation.DIVIDE,
			new ExpressionLiteral(10),
			new ExpressionLiteral(3),
			ExpressionContext.DEFAULT
		);
		assertEquals(3.3333333333, service.evaluate(expr));
	}

	@Test
	@DisplayName("retorna 3.33333 para DIVIDE(10, 3) com context(scale=5, HALF_UP)")
	void evaluateDivideWithExplicitContext() {
		ExpressionContext context = new ExpressionContext(5, java.math.RoundingMode.HALF_UP);
		ExpressionNode expr = new ExpressionNode(
			ExpressionOperation.DIVIDE,
			new ExpressionLiteral(10),
			new ExpressionLiteral(3),
			context
		);
		assertEquals(3.33333, service.evaluate(expr));
	}

	@Test
	@DisplayName("retorna 16.0 para ADD(10, MULTIPLY(2, 3)) aninhada")
	void evaluateNestedAddMultiply() {
		ExpressionNode inner = new ExpressionNode(
			ExpressionOperation.MULTIPLY,
			new ExpressionLiteral(2),
			new ExpressionLiteral(3),
			ExpressionContext.DEFAULT
		);
		ExpressionNode outer = new ExpressionNode(
			ExpressionOperation.ADD,
			new ExpressionLiteral(10),
			inner,
			ExpressionContext.DEFAULT
		);
		assertEquals(16.0, service.evaluate(outer));
	}

	@Test
	@DisplayName("retorna 5.33333 para ADD(DIVIDE(10,3 ctx=5HU), 2) aninhada")
	void evaluateNestedWithDivideContext() {
		ExpressionContext divideCtx = new ExpressionContext(5, java.math.RoundingMode.HALF_UP);
		ExpressionNode divideNode = new ExpressionNode(
			ExpressionOperation.DIVIDE,
			new ExpressionLiteral(10),
			new ExpressionLiteral(3),
			divideCtx
		);
		ExpressionNode addNode = new ExpressionNode(
			ExpressionOperation.ADD,
			divideNode,
			new ExpressionLiteral(2),
			ExpressionContext.DEFAULT
		);
		assertEquals(5.33333, service.evaluate(addNode));
	}
}