package com.example.demo.controller.api;

import jakarta.validation.constraints.NotNull;

public record BinaryOperationDto(
    @NotNull OperatorDto operator,
    @NotNull ExpressionDto left,
    @NotNull ExpressionDto right,
    CalculationContextDto context
) implements ExpressionDto {
}
