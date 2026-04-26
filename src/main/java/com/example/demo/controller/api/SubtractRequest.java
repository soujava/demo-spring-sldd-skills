package com.example.demo.controller.api;

import jakarta.validation.constraints.NotNull;

public record SubtractRequest(
	@NotNull Double minuend,
	@NotNull Double subtrahend
) {
}
