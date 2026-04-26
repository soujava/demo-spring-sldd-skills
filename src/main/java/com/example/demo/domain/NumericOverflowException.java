package com.example.demo.domain;

public class NumericOverflowException extends RuntimeException {
    public NumericOverflowException(String message) {
        super(message);
    }
}
