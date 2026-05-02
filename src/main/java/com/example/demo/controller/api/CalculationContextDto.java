package com.example.demo.controller.api;

import java.math.RoundingMode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CalculationContextDto(
    @Min(1) @Max(16) Integer scale,
    RoundingMode roundingMode
) {
}
