package com.example.demo.controller.api;

import jakarta.validation.constraints.NotNull;

public record PowerRequest(
    @NotNull Double base,
    @NotNull Double exponent
) {
}
