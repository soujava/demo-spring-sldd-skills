package com.example.demo.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DivideRequest(
	@NotNull Double dividend,
	@NotNull Double divisor,
	@Valid CalculationContextDto context
) {
}
