package com.example.demo.controller;

import com.example.demo.controller.api.SumRequest;
import com.example.demo.controller.api.SumResponse;
import com.example.demo.controller.api.SubtractRequest;
import com.example.demo.controller.api.SubtractResponse;
import com.example.demo.controller.api.MultiplyRequest;
import com.example.demo.controller.api.MultiplyResponse;
import com.example.demo.controller.api.DivideRequest;
import com.example.demo.controller.api.DivideResponse;
import com.example.demo.controller.api.PowerRequest;
import com.example.demo.controller.api.PowerResponse;
import com.example.demo.controller.api.RootRequest;
import com.example.demo.controller.api.RootResponse;
import com.example.demo.controller.api.EvaluateRequest;
import com.example.demo.controller.api.EvaluateResponse;
import com.example.demo.controller.api.CalculationContextDto;
import com.example.demo.controller.api.ExpressionDto;
import com.example.demo.controller.api.LiteralDto;
import com.example.demo.controller.api.BinaryOperationDto;
import com.example.demo.controller.api.OperatorDto;
import com.example.demo.domain.CalculationContext;
import com.example.demo.domain.Expression;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

	@PostMapping("/evaluate")
	public ResponseEntity evaluate(@Valid @RequestBody EvaluateRequest request) {
		CalculationContext rootContext = resolveContext(request.context(), CalculationContext.defaults());
		double result = evaluate(request.expression(), rootContext);
		return ResponseEntity.ok(new EvaluateResponse(result));
	}

	private double evaluate(ExpressionDto dto, CalculationContext inheritedContext) {
		return switch (dto) {
			case LiteralDto literal -> literal.value();
			case BinaryOperationDto op -> {
				CalculationContext context = resolveContext(op.context(), inheritedContext);
				Expression left = mapToDomain(op.left());
				Expression right = mapToDomain(op.right());
				double leftVal = left.evaluate(context).value();
				double rightVal = right.evaluate(context).value();
				yield switch (op.operator()) {
					case SUM -> Expression.sum(Expression.literal(leftVal), Expression.literal(rightVal)).evaluate(context).value();
					case SUBTRACT -> Expression.subtract(Expression.literal(leftVal), Expression.literal(rightVal)).evaluate(context).value();
					case MULTIPLY -> Expression.multiply(Expression.literal(leftVal), Expression.literal(rightVal)).evaluate(context).value();
					case DIVIDE -> Expression.divide(Expression.literal(leftVal), Expression.literal(rightVal)).evaluate(context).value();
					case POWER -> Expression.power(Expression.literal(leftVal), Expression.literal(rightVal)).evaluate(context).value();
					case ROOT -> Expression.root(Expression.literal(leftVal), Expression.literal(rightVal)).evaluate(context).value();
				};
			}
		};
	}

	private Expression mapToDomain(ExpressionDto dto) {
		return switch (dto) {
			case LiteralDto literal -> Expression.literal(literal.value());
			case BinaryOperationDto op -> {
				var left = mapToDomain(op.left());
				var right = mapToDomain(op.right());
				yield switch (op.operator()) {
					case SUM -> Expression.sum(left, right);
					case SUBTRACT -> Expression.subtract(left, right);
					case MULTIPLY -> Expression.multiply(left, right);
					case DIVIDE -> Expression.divide(left, right);
					case POWER -> Expression.power(left, right);
					case ROOT -> Expression.root(left, right);
				};
			}
		};
	}

	@PostMapping("/sum")
	public ResponseEntity sum(@Valid @RequestBody SumRequest request) {
		double result = Expression.sum(
			Expression.literal(request.firstAddend()),
			Expression.literal(request.secondAddend())
		).evaluate().value();
		return ResponseEntity.ok(new SumResponse(result));
	}

	@PostMapping("/subtract")
	public ResponseEntity subtract(@Valid @RequestBody SubtractRequest request) {
		double result = Expression.subtract(
			Expression.literal(request.minuend()),
			Expression.literal(request.subtrahend())
		).evaluate().value();
		return ResponseEntity.ok(new SubtractResponse(result));
	}

	@PostMapping("/multiply")
	public ResponseEntity multiply(@Valid @RequestBody MultiplyRequest request) {
		double result = Expression.multiply(
			Expression.literal(request.multiplicand()),
			Expression.literal(request.multiplier())
		).evaluate().value();
		return ResponseEntity.ok(new MultiplyResponse(result));
	}

	@PostMapping("/divide")
	public ResponseEntity divide(@Valid @RequestBody DivideRequest request) {
		CalculationContext context = resolveContext(request.context(), CalculationContext.defaults());
		double result = Expression.divide(
			Expression.literal(request.dividend()),
			Expression.literal(request.divisor())
		).evaluate(context).value();
		return ResponseEntity.ok(new DivideResponse(result));
	}

	@PostMapping("/power")
	public ResponseEntity power(@Valid @RequestBody PowerRequest request) {
		CalculationContext context = resolveContext(request.context(), CalculationContext.defaults());
		double result = Expression.power(
			Expression.literal(request.base()),
			Expression.literal(request.exponent())
		).evaluate(context).value();
		return ResponseEntity.ok(new PowerResponse(result));
	}

	@PostMapping("/root")
	public ResponseEntity root(@Valid @RequestBody RootRequest request) {
		CalculationContext context = resolveContext(request.context(), CalculationContext.defaults());
		double result = Expression.root(
			Expression.literal(request.radicand()),
			Expression.literal(request.index())
		).evaluate(context).value();
		return ResponseEntity.ok(new RootResponse(result));
	}

	private CalculationContext resolveContext(CalculationContextDto dto, CalculationContext fallback) {
		if (dto == null) {
			return fallback;
		}
		int scale = dto.scale() == null ? fallback.scale() : dto.scale();
		var roundingMode = dto.roundingMode() == null ? fallback.roundingMode() : dto.roundingMode();
		return new CalculationContext(scale, roundingMode);
	}
}
