package com.example.demo.domain.expression;

import java.math.RoundingMode;

public record ExpressionContext(int scale, RoundingMode roundingMode) {

	public static final ExpressionContext DEFAULT =
		new ExpressionContext(10, RoundingMode.HALF_UP);

	public ExpressionContext {
		if (scale < 1 || scale > 100) {
			throw new IllegalArgumentException(
				"Invalid scale: must be between 1 and 100");
		}
		if (roundingMode == null) {
			throw new IllegalArgumentException(
				"Rounding mode must not be null");
		}
	}
}