package com.example.demo.controller.api;

import jakarta.validation.constraints.NotNull;

public record MultiplyRequest(
	@NotNull Double multiplicand,
	@NotNull Double multiplier
) {
}