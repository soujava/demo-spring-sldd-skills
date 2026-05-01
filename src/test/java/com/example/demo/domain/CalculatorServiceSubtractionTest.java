package com.example.demo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CalculatorService - subtracao")
class CalculatorServiceSubtractionTest {

	private CalculatorService service;

	@BeforeEach
	void setUp() {
		service = new CalculatorService();
	}

	@Test
	@DisplayName("retorna 2.0 quando subtrai 5.0 de 3.0")
	void subtractBasicValues() {
		assertEquals(2.0, service.subtract(5.0, 3.0));
	}

	@Test
	@DisplayName("retorna 5.5 quando subtrai 7.75 de 2.25")
	void subtractDecimalValues() {
		assertEquals(5.5, service.subtract(7.75, 2.25));
	}

	@Test
	@DisplayName("retorna o valor original quando subtrahend e zero")
	void subtractWithZeroAsSubtrahend() {
		assertEquals(9.4, service.subtract(9.4, 0.0));
	}

	@Test
	@DisplayName("retorna valor negativo quando resultado da subtracao e negativo")
	void subtractWithNegativeResult() {
		assertEquals(-3.5, service.subtract(2.0, 5.5));
	}

	@Test
	@DisplayName("retorna zero quando minuend e subtrahend sao iguais")
	void subtractEqualValues() {
		assertEquals(0.0, service.subtract(5.5, 5.5));
	}

	@Test
	@DisplayName("retorna resultado deterministico com valores grandes finitos")
	void subtractLargeFiniteValues() {
		assertEquals(1.0E307, service.subtract(3.0E307, 2.0E307), 1.0E292);
	}

	@Test
	@DisplayName("retorna 0.2 quando subtrai 0.1 de 0.3 com precisao BigDecimal")
	void subtractWithBigDecimalPrecision() {
		assertEquals(0.2, service.subtract(0.3, 0.1));
	}
}