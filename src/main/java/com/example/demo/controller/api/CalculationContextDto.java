package com.example.demo.controller.api;

import jakarta.validation.constraints.NotNull;

public record CalculationContextDto(int scale, @NotNull String roundingMode) {
}
