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
import com.example.demo.domain.CalculatorService;
import com.example.demo.domain.CalculationContext;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

	private final CalculatorService calculatorService;

	public CalculatorController(CalculatorService calculatorService) {
		this.calculatorService = calculatorService;
	}

	@PostMapping("/evaluate")
	public ResponseEntity<EvaluateResponse> evaluate(@Valid @RequestBody EvaluateRequest request) {
		CalculationContext rootContext = resolveContext(request.context(), CalculationContext.defaults());
		double result = evaluate(request.expression(), rootContext, 0);
		return ResponseEntity.ok(new EvaluateResponse(result));
	}

	private double evaluate(com.example.demo.controller.api.ExpressionDto dto, CalculationContext inheritedContext, int depth) {
		if (depth > 10) {
			throw new IllegalArgumentException("Expression tree is too deep");
		}
		return switch (dto) {
			case com.example.demo.controller.api.LiteralDto literal -> literal.value();
			case com.example.demo.controller.api.BinaryOperationDto op -> {
				CalculationContext operationContext = resolveContext(op.context(), inheritedContext);
				double leftVal = evaluate(op.left(), operationContext, depth + 1);
				double rightVal = evaluate(op.right(), operationContext, depth + 1);
				yield switch (com.example.demo.domain.Operator.valueOf(op.operator().name())) {
					case SUM -> calculatorService.sum(leftVal, rightVal);
					case SUBTRACT -> calculatorService.subtract(leftVal, rightVal);
					case MULTIPLY -> calculatorService.multiply(leftVal, rightVal);
					case DIVIDE -> calculatorService.divide(leftVal, rightVal, operationContext);
					case POWER -> calculatorService.power(leftVal, rightVal, operationContext);
					case ROOT -> calculatorService.root(leftVal, rightVal, operationContext);
				};
			}
		};
	}

	private com.example.demo.domain.Expression mapToDomain(com.example.demo.controller.api.ExpressionDto dto, int depth) {
		if (depth > 10) {
			throw new IllegalArgumentException("Expression tree is too deep");
		}
		return switch (dto) {
			case com.example.demo.controller.api.LiteralDto literal -> 
				new com.example.demo.domain.Literal(literal.value());
			case com.example.demo.controller.api.BinaryOperationDto op -> 
				new com.example.demo.domain.BinaryOperation(
					com.example.demo.domain.Operator.valueOf(op.operator().name()),
					mapToDomain(op.left(), depth + 1),
					mapToDomain(op.right(), depth + 1)
				);
		};
	}

	@PostMapping("/sum")
	public ResponseEntity<SumResponse> sum(@Valid @RequestBody SumRequest request) {
		double result = calculatorService.sum(request.firstAddend(), request.secondAddend());
		return ResponseEntity.ok(new SumResponse(result));
	}

	@PostMapping("/subtract")
	public ResponseEntity<SubtractResponse> subtract(@Valid @RequestBody SubtractRequest request) {
		double result = calculatorService.subtract(request.minuend(), request.subtrahend());
		return ResponseEntity.ok(new SubtractResponse(result));
	}

	@PostMapping("/multiply")
	public ResponseEntity<MultiplyResponse> multiply(@Valid @RequestBody MultiplyRequest request) {
		double result = calculatorService.multiply(request.multiplicand(), request.multiplier());
		return ResponseEntity.ok(new MultiplyResponse(result));
	}

	@PostMapping("/divide")
	public ResponseEntity<DivideResponse> divide(@Valid @RequestBody DivideRequest request) {
		double result = calculatorService.divide(request.dividend(), request.divisor(),
			resolveContext(request.context(), CalculationContext.defaults()));
		return ResponseEntity.ok(new DivideResponse(result));
	}

	@PostMapping("/power")
	public ResponseEntity<PowerResponse> power(@Valid @RequestBody PowerRequest request) {
		double result = calculatorService.power(request.base(), request.exponent(),
			resolveContext(request.context(), CalculationContext.defaults()));
		return ResponseEntity.ok(new PowerResponse(result));
	}

	@PostMapping("/root")
	public ResponseEntity<RootResponse> root(@Valid @RequestBody RootRequest request) {
		double result = calculatorService.root(request.radicand(), request.index(),
			resolveContext(request.context(), CalculationContext.defaults()));
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
