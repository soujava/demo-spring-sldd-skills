package com.example.demo.controller;

import com.example.demo.controller.api.ErrorResponse;
import com.example.demo.domain.NumericOverflowException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class ApiErrorHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.filter(error -> error.getField().endsWith("scale"))
				.findFirst()
				.map(error -> "%s %s".formatted(error.getField(), error.getDefaultMessage()))
				.orElse("Invalid request body");
		return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", message));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
		String exceptionMessage = ex.getMessage() + " " + ex.getMostSpecificCause().getMessage();
		String message = exceptionMessage.contains("roundingMode")
				|| exceptionMessage.contains("RoundingMode")
				|| exceptionMessage.contains("HALF_UP")
				? "Invalid roundingMode"
				: "Invalid request body";
		return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", message));
	}

	@ExceptionHandler(ArithmeticException.class)
	public ResponseEntity<ErrorResponse> handleArithmeticException(ArithmeticException ex) {
		return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", ex.getMessage()));
	}

	@ExceptionHandler(NumericOverflowException.class)
	public ResponseEntity<ErrorResponse> handleNumericOverflowException(NumericOverflowException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new ErrorResponse("Unprocessable Entity", ex.getMessage()));
	}
}
