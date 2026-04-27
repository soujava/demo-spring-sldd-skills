package com.example.demo.controller;

import com.example.demo.controller.api.SumRequest;
import com.example.demo.controller.api.SumResponse;
import com.example.demo.controller.api.SubtractRequest;
import com.example.demo.controller.api.SubtractResponse;
import com.example.demo.controller.api.MultiplyRequest;
import com.example.demo.controller.api.MultiplyResponse;
import com.example.demo.controller.api.DivideRequest;
import com.example.demo.controller.api.DivideResponse;
import com.example.demo.controller.api.ComposeResponse;
import com.example.demo.controller.api.PowerRequest;
import com.example.demo.controller.api.PowerResponse;
import com.example.demo.controller.api.RootRequest;
import com.example.demo.controller.api.RootResponse;
import com.example.demo.controller.validation.ComposeExpressionValidator;
import com.example.demo.domain.CalculatorService;
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
	private final ComposeExpressionValidator composeExpressionValidator;

	public CalculatorController(CalculatorService calculatorService, ComposeExpressionValidator composeExpressionValidator) {
		this.calculatorService = calculatorService;
		this.composeExpressionValidator = composeExpressionValidator;
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
		double result = calculatorService.divide(request.dividend(), request.divisor());
		return ResponseEntity.ok(new DivideResponse(result));
	}

	@PostMapping("/power")
	public ResponseEntity<PowerResponse> power(@Valid @RequestBody PowerRequest request) {
		double result = calculatorService.power(request.base(), request.exponent());
		return ResponseEntity.ok(new PowerResponse(result));
	}

	@PostMapping("/root")
	public ResponseEntity<RootResponse> root(@Valid @RequestBody RootRequest request) {
		double result = calculatorService.root(request.radicand(), request.index());
		return ResponseEntity.ok(new RootResponse(result));
	}

	@PostMapping("/compose")
	public ResponseEntity<ComposeResponse> compose(@RequestBody String request) {
		double result = calculatorService.evaluate(composeExpressionValidator.parse(request));
		return ResponseEntity.ok(new ComposeResponse(result));
	}
}
