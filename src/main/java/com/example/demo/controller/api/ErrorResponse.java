package com.example.demo.controller.api;

public record ErrorResponse(
	String error,
	String message
) {
}
