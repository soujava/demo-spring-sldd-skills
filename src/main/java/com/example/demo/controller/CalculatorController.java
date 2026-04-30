package com.example.demo.controller;

import com.example.demo.controller.api.ComposeResponse;
import com.example.demo.controller.api.DivideRequest;
import com.example.demo.controller.api.DivideResponse;
import com.example.demo.controller.api.MultiplyRequest;
import com.example.demo.controller.api.MultiplyResponse;
import com.example.demo.controller.api.PowerRequest;
import com.example.demo.controller.api.PowerResponse;
import com.example.demo.controller.api.RootRequest;
import com.example.demo.controller.api.RootResponse;
import com.example.demo.controller.api.SumRequest;
import com.example.demo.controller.api.SumResponse;
import com.example.demo.controller.api.SubtractRequest;
import com.example.demo.controller.api.SubtractResponse;
import com.example.demo.controller.validation.ComposeExpressionValidator;
import com.example.demo.domain.expression.Expression;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.demo.domain.expression.Expression.literal;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final ComposeExpressionValidator composeExpressionValidator;

    public CalculatorController(ComposeExpressionValidator composeExpressionValidator) {
        this.composeExpressionValidator = composeExpressionValidator;
    }

    @PostMapping("/sum")
    public ResponseEntity<SumResponse> sum(@Valid @RequestBody SumRequest request) {
        var expr = Expression.add(literal(request.firstAddend()), literal(request.secondAddend()));
        double result = expr.evaluate().value();
        return ResponseEntity.ok(new SumResponse(result));
    }

    @PostMapping("/subtract")
    public ResponseEntity<SubtractResponse> subtract(@Valid @RequestBody SubtractRequest request) {
        var expr = Expression.subtract(literal(request.minuend()), literal(request.subtrahend()));
        double result = expr.evaluate().value();
        return ResponseEntity.ok(new SubtractResponse(result));
    }

    @PostMapping("/multiply")
    public ResponseEntity<MultiplyResponse> multiply(@Valid @RequestBody MultiplyRequest request) {
        var expr = Expression.multiply(literal(request.multiplicand()), literal(request.multiplier()));
        double result = expr.evaluate().value();
        return ResponseEntity.ok(new MultiplyResponse(result));
    }

    @PostMapping("/divide")
    public ResponseEntity<DivideResponse> divide(@Valid @RequestBody DivideRequest request) {
        var expr = Expression.divide(literal(request.dividend()), literal(request.divisor()));
        double result = expr.evaluate().value();
        return ResponseEntity.ok(new DivideResponse(result));
    }

    @PostMapping("/power")
    public ResponseEntity<PowerResponse> power(@Valid @RequestBody PowerRequest request) {
        var expr = Expression.power(literal(request.base()), literal(request.exponent()));
        double result = expr.evaluate().value();
        return ResponseEntity.ok(new PowerResponse(result));
    }

    @PostMapping("/root")
    public ResponseEntity<RootResponse> root(@Valid @RequestBody RootRequest request) {
        var expr = Expression.root(literal(request.radicand()), literal(request.index()));
        double result = expr.evaluate().value();
        return ResponseEntity.ok(new RootResponse(result));
    }

    @PostMapping("/compose")
    public ResponseEntity<ComposeResponse> compose(@RequestBody String request) {
        var expr = composeExpressionValidator.parse(request);
        double result = expr.evaluate().value();
        return ResponseEntity.ok(new ComposeResponse(result));
    }
}