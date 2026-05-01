package com.example.demo.domain.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ExpressionContext")
class ExpressionContextTest {

	@Test
	@DisplayName("DEFAULT tem scale=10 e roundingMode=HALF_UP")
	void defaultContextHasScale10AndHalfUp() {
		assertThat(ExpressionContext.DEFAULT.scale()).isEqualTo(10);
		assertThat(ExpressionContext.DEFAULT.roundingMode()).isEqualTo(RoundingMode.HALF_UP);
	}

	@Test
	@DisplayName("cria context valido com scale=5 e HALF_UP")
	void createsValidContext() {
		ExpressionContext context = new ExpressionContext(5, RoundingMode.HALF_UP);
		assertThat(context.scale()).isEqualTo(5);
		assertThat(context.roundingMode()).isEqualTo(RoundingMode.HALF_UP);
	}

	@Test
	@DisplayName("lança IllegalArgumentException quando scale=0")
	void throwsWhenScaleIsZero() {
		assertThatThrownBy(() -> new ExpressionContext(0, RoundingMode.HALF_UP))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("scale");
	}

	@Test
	@DisplayName("lança IllegalArgumentException quando scale negativo")
	void throwsWhenScaleIsNegative() {
		assertThatThrownBy(() -> new ExpressionContext(-1, RoundingMode.HALF_UP))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("scale");
	}

	@Test
	@DisplayName("lança IllegalArgumentException quando scale > 100")
	void throwsWhenScaleExceeds100() {
		assertThatThrownBy(() -> new ExpressionContext(101, RoundingMode.HALF_UP))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("scale");
	}

	@Test
	@DisplayName("lança IllegalArgumentException quando roundingMode e null")
	void throwsWhenRoundingModeIsNull() {
		assertThatThrownBy(() -> new ExpressionContext(10, null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Rounding mode");
	}
}