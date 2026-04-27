package com.example.demo.controller.api;

import jakarta.validation.constraints.NotNull;

public record LiteralDto(@NotNull Double value) implements ExpressionDto {
}
