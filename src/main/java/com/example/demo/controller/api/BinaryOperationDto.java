package com.example.demo.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record BinaryOperationDto(
    @NotNull OperatorDto operator,
    @NotNull @Valid ExpressionDto left,
    @NotNull @Valid ExpressionDto right,
    @Valid CalculationContextDto context
) implements ExpressionDto {
}
