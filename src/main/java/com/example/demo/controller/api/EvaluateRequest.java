package com.example.demo.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record EvaluateRequest(@NotNull @Valid ExpressionDto expression) {
}
