package com.example.demo.controller.api;

import jakarta.validation.constraints.NotNull;

public record SumRequest(
	@NotNull Double firstAddend,
	@NotNull Double secondAddend
) {
}
