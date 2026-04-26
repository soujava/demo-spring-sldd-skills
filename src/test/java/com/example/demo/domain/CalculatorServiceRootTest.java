package com.example.demo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CalculatorService root")
class CalculatorServiceRootTest {

	private final CalculatorService calculatorService = new CalculatorService();

	@Test
	@DisplayName("retorna raiz quadrada de um double positivo")
	void shouldCalculateSquareRoot() {
		assertEquals(3.0, calculatorService.root(9.0, 2.0));
	}

	@Test
	@DisplayName("retorna raiz cubica de um double positivo")
	void shouldCalculateCubeRoot() {
		assertEquals(3.0, calculatorService.root(27.0, 3.0), 1.0E-9);
	}

	@Test
	@DisplayName("retorna zero quando radicando e zero")
	void shouldReturnZeroWhenRadicandIsZero() {
		assertEquals(0.0, calculatorService.root(0.0, 2.0));
	}

	@Test
	@DisplayName("lanca ArithmeticException quando indice e zero")
	void shouldThrowArithmeticExceptionWhenIndexIsZero() {
		assertThrows(ArithmeticException.class, () -> calculatorService.root(9.0, 0.0));
	}

	@Test
	@DisplayName("lanca ArithmeticException quando resultado e indefinido ou imaginario")
	void shouldThrowArithmeticExceptionWhenResultIsUndefinedOrImaginary() {
		assertThrows(ArithmeticException.class, () -> calculatorService.root(-4.0, 2.0));
	}
}
