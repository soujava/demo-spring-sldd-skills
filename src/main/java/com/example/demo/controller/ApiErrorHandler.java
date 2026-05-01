package com.example.demo.controller;

import com.example.demo.controller.api.ErrorResponse;
import com.example.demo.controller.validation.InvalidExpressionPayloadException;
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
		return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", "Invalid request body"));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", "Invalid request body"));
	}

	@ExceptionHandler(InvalidExpressionPayloadException.class)
	public ResponseEntity<ErrorResponse> handleInvalidExpressionPayloadException(InvalidExpressionPayloadException ex) {
		return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", ex.getMessage()));
	}

	@ExceptionHandler(ArithmeticException.class)
	public ResponseEntity<ErrorResponse> handleArithmeticException(ArithmeticException ex) {
		return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", ex.getMessage()));
	}

	@ExceptionHandler(NumericOverflowException.class)
	public ResponseEntity<ErrorResponse> handleNumericOverflowException(NumericOverflowException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new ErrorResponse("Unprocessable Entity", ex.getMessage()));
	}
}