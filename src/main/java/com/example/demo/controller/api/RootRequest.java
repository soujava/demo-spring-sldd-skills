package com.example.demo.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RootRequest(
	@NotNull Double radicand,
	@NotNull Double index,
	@Valid CalculationContextDto context
) {
}
