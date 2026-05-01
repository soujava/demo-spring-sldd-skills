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
	@DisplayName("retorna 3.0 quando dividend=6.0 e divisor=2.0 com defaults")
	void returnsQuotientForValidNumbers() {
		double result = service.divide(6.0, 2.0, 10, RoundingMode.HALF_UP);
		assertThat(result).isEqualTo(3.0);
	}

	@Test
	@DisplayName("retorna 3.0 quando dividend=7.5 e divisor=2.5 com defaults")
	void returnsQuotientForDecimalNumbers() {
		double result = service.divide(7.5, 2.5, 10, RoundingMode.HALF_UP);
		assertThat(result).isEqualTo(3.0);
	}

	@Test
	@DisplayName("retorna 0.0 quando dividend=0.0 e divisor=5.0 com defaults")
	void returnsZeroWhenDividendIsZero() {
		double result = service.divide(0.0, 5.0, 10, RoundingMode.HALF_UP);
		assertThat(result).isEqualTo(0.0);
	}

	@Test
	@DisplayName("retorna -5.0 quando dividend=-10.0 e divisor=2.0 com defaults")
	void returnsNegativeQuotientWhenSignsDiffer() {
		double result = service.divide(-10.0, 2.0, 10, RoundingMode.HALF_UP);
		assertThat(result).isEqualTo(-5.0);
	}

	@Test
	@DisplayName("retorna -5.0 quando dividend=10.0 e divisor=-2.0 com defaults")
	void returnsNegativeQuotientWhenDivisorIsNegative() {
		double result = service.divide(10.0, -2.0, 10, RoundingMode.HALF_UP);
		assertThat(result).isEqualTo(-5.0);
	}

	@Test
	@DisplayName("lança ArithmeticException quando divisor=0.0")
	void throwsArithmeticExceptionWhenDivisorIsZero() {
		assertThatThrownBy(() -> service.divide(1.0, 0.0, 10, RoundingMode.HALF_UP))
			.isInstanceOf(ArithmeticException.class);
	}

	@Test
	@DisplayName("retorna 3.3333333333 quando divide 10 por 3 com scale=10 HALF_UP")
	void returnsNonTerminatingDecimalWithDefaults() {
		double result = service.divide(10.0, 3.0, 10, RoundingMode.HALF_UP);
		assertThat(result).isEqualTo(3.3333333333);
	}

	@Test
	@DisplayName("retorna 3.33333 quando divide 10 por 3 com scale=5 HALF_UP")
	void returnsNonTerminatingDecimalWithExplicitScale() {
		double result = service.divide(10.0, 3.0, 5, RoundingMode.HALF_UP);
		assertThat(result).isEqualTo(3.33333);
	}

	@Test
	@DisplayName("retorna 0.3333333333 quando divide 1 por 3 com scale=10 HALF_UP")
	void returnsNonTerminatingFractionWithDefaults() {
		double result = service.divide(1.0, 3.0, 10, RoundingMode.HALF_UP);
		assertThat(result).isEqualTo(0.3333333333);
	}

	@Test
	@DisplayName("lança IllegalArgumentException quando scale=0")
	void throwsIllegalArgumentExceptionWhenScaleIsZero() {
		assertThatThrownBy(() -> service.divide(10.0, 3.0, 0, RoundingMode.HALF_UP))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("scale");
	}

	@Test
	@DisplayName("lança IllegalArgumentException quando scale e negativo")
	void throwsIllegalArgumentExceptionWhenScaleIsNegative() {
		assertThatThrownBy(() -> service.divide(10.0, 3.0, -1, RoundingMode.HALF_UP))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("scale");
	}

	@Test
	@DisplayName("lança IllegalArgumentException quando roundingMode e null")
	void throwsIllegalArgumentExceptionWhenRoundingModeIsNull() {
		assertThatThrownBy(() -> service.divide(10.0, 3.0, 10, null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Rounding mode");
	}
}