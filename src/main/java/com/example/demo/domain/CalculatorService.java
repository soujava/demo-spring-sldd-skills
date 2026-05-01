package com.example.demo.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.example.demo.domain.expression.Expression;
import com.example.demo.domain.expression.ExpressionLiteral;
import com.example.demo.domain.expression.ExpressionNode;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

	public double sum(double firstAddend, double secondAddend) {
		BigDecimal a = BigDecimal.valueOf(firstAddend);
		BigDecimal b = BigDecimal.valueOf(secondAddend);
		BigDecimal result = a.add(b);
		return result.doubleValue();
	}

	public double subtract(double minuend, double subtrahend) {
		BigDecimal a = BigDecimal.valueOf(minuend);
		BigDecimal b = BigDecimal.valueOf(subtrahend);
		BigDecimal result = a.subtract(b);
		return result.doubleValue();
	}

	public double multiply(double multiplicand, double multiplier) {
		BigDecimal a = BigDecimal.valueOf(multiplicand);
		BigDecimal b = BigDecimal.valueOf(multiplier);
		BigDecimal result = a.multiply(b);
		return result.doubleValue();
	}

	public double divide(double dividend, double divisor, int scale, RoundingMode roundingMode) {
		if (divisor == 0.0) {
			throw new ArithmeticException("Division by zero");
		}
		if (scale < 1 || scale > 100) {
			throw new IllegalArgumentException(
				"Invalid scale: must be between 1 and 100");
		}
		if (roundingMode == null) {
			throw new IllegalArgumentException(
				"Rounding mode must not be null");
		}
		BigDecimal a = BigDecimal.valueOf(dividend);
		BigDecimal b = BigDecimal.valueOf(divisor);
		BigDecimal result = a.divide(b, scale, roundingMode);
		return result.doubleValue();
	}

	public double power(double base, double exponent) {
		double result = Math.pow(base, exponent);
		if (Double.isInfinite(result)) {
			throw new NumericOverflowException("Numeric overflow: result is too large");
		}
		if (Double.isNaN(result)) {
			throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
		}
		return result;
	}

	public double root(double radicand, double index) {
		if (index == 0.0) {
			throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
		}
		double result = Math.pow(radicand, 1.0 / index);
		if (Double.isInfinite(result)) {
			throw new NumericOverflowException("Numeric overflow: result is too large");
		}
		if (Double.isNaN(result)) {
			throw new ArithmeticException("Invalid operation: result is undefined or imaginary");
		}
		return result;
	}

	public double evaluate(Expression expression) {
		if (expression instanceof ExpressionLiteral literal) {
			return literal.value();
		}
		if (expression instanceof ExpressionNode node) {
			double left = evaluate(node.left());
			double right = evaluate(node.right());
			return switch (node.operation()) {
				case ADD -> sum(left, right);
				case SUBTRACT -> subtract(left, right);
				case MULTIPLY -> multiply(left, right);
				case DIVIDE -> divide(left, right,
					node.context().scale(), node.context().roundingMode());
			};
		}
		throw new IllegalArgumentException("Unsupported expression type");
	}
}