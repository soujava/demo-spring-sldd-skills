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
import com.example.demo.domain.CalculationContext;
import com.example.demo.domain.CalculatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.RoundingMode;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluateResponse> evaluate(@Valid @RequestBody EvaluateRequest request) {
        com.example.demo.domain.Expression domainExpression = mapToDomain(request.expression(), 0);
        double result = calculatorService.evaluate(domainExpression);
        return ResponseEntity.ok(new EvaluateResponse(result));
    }

    private com.example.demo.domain.Expression mapToDomain(com.example.demo.controller.api.ExpressionDto dto, int depth) {
        if (depth > 10) {
            throw new IllegalArgumentException("Expression tree is too deep");
        }
        return switch (dto) {
            case com.example.demo.controller.api.LiteralDto literal -> 
                new com.example.demo.domain.Literal(literal.value());
            case com.example.demo.controller.api.BinaryOperationDto op -> {
                CalculationContext ctx = null;
                if (op.context() != null) {
                    ctx = new CalculationContext(
                        op.context().scale(),
                        RoundingMode.valueOf(op.context().roundingMode())
                    );
                }
                yield new com.example.demo.domain.BinaryOperation(
                    com.example.demo.domain.Operator.valueOf(op.operator().name()),
                    mapToDomain(op.left(), depth + 1),
                    mapToDomain(op.right(), depth + 1),
                    ctx
                );
            }
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
        CalculationContext ctx = buildCalculationContext(request);
        double result;
        if (ctx != null) {
            result = calculatorService.divide(request.dividend(), request.divisor(), ctx);
        } else {
            result = calculatorService.divide(request.dividend(), request.divisor());
        }
        return ResponseEntity.ok(new DivideResponse(result));
    }

    private CalculationContext buildCalculationContext(DivideRequest request) {
        if (request.scale() != null && request.roundingMode() != null) {
            return new CalculationContext(request.scale(), RoundingMode.valueOf(request.roundingMode()));
        }
        return null;
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
}
