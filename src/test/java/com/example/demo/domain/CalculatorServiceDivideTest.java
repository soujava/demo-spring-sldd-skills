package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CalculatorService.divide()")
class CalculatorServiceDivideTest {

	private final CalculatorService service = new CalculatorService();

	@Test
	@DisplayName("retorna 3.0 quando dividend=6.0 e divisor=2.0")
	void returnsQuotientForValidNumbers() {
		double result = service.divide(6.0, 2.0);
		assertThat(result).isEqualTo(3.0);
	}

	@Test
	@DisplayName("retorna 3.0 quando dividend=7.5 e divisor=2.5")
	void returnsQuotientForDecimalNumbers() {
		double result = service.divide(7.5, 2.5);
		assertThat(result).isEqualTo(3.0);
	}

	@Test
	@DisplayName("retorna 0.0 quando dividend=0.0 e divisor=5.0")
	void returnsZeroWhenDividendIsZero() {
		double result = service.divide(0.0, 5.0);
		assertThat(result).isEqualTo(0.0);
	}

	@Test
	@DisplayName("retorna -5.0 quando dividend=-10.0 e divisor=2.0")
	void returnsNegativeQuotientWhenSignsDiffer() {
		double result = service.divide(-10.0, 2.0);
		assertThat(result).isEqualTo(-5.0);
	}

	@Test
	@DisplayName("retorna -5.0 quando dividend=10.0 e divisor=-2.0")
	void returnsNegativeQuotientWhenDivisorIsNegative() {
		double result = service.divide(10.0, -2.0);
		assertThat(result).isEqualTo(-5.0);
	}

	@Test
	@DisplayName("lança ArithmeticException quando divisor=0.0")
	void throwsArithmeticExceptionWhenDivisorIsZero() {
		assertThatThrownBy(() -> service.divide(1.0, 0.0))
			.isInstanceOf(ArithmeticException.class);
	}

	@Test
	@DisplayName("retorna 0.3333 quando divide 1 por 3 com scale 4 e HALF_UP")
	void returnsRoundedQuotientUsingProvidedContext() {
		double result = service.divide(1.0, 3.0, new CalculationContext(4, RoundingMode.HALF_UP));

		assertThat(result).isEqualTo(0.3333);
	}

	@Test
	@DisplayName("retorna 0.33 quando divide 1 por 3 com scale 2 e DOWN")
	void returnsRoundedDownQuotientUsingProvidedContext() {
		double result = service.divide(1.0, 3.0, new CalculationContext(2, RoundingMode.DOWN));

		assertThat(result).isEqualTo(0.33);
	}

	@Test
	@DisplayName("lanca ArithmeticException quando divide 1 por 3 com UNNECESSARY")
	void throwsArithmeticExceptionWhenRoundingIsNecessaryButForbidden() {
		assertThatThrownBy(() -> service.divide(1.0, 3.0, new CalculationContext(10, RoundingMode.UNNECESSARY)))
			.isInstanceOf(ArithmeticException.class);
	}
}
