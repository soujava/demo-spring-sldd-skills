package com.example.demo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CalculatorService")
class CalculatorServiceTest {

    private CalculatorService service;

    @BeforeEach
    void setUp() {
        service = new CalculatorService(new OperationRegistry());
    }

    @Test
    @DisplayName("retorna a soma de dois doubles positivos")
    void sumTwoPositiveDoubles() {
        assertEquals(4.0, service.sum(1.5, 2.5));
    }

    @Test
    @DisplayName("retorna a soma de dois doubles positivos muito grandes")
    void sumLargePositiveDoubles() {
        assertEquals(3.0E307, service.sum(1.0E307, 2.0E307));
    }

    @Test
    @DisplayName("retorna a soma de dois doubles decimais")
    void sumDecimalDoubles() {
        assertEquals(3.75, service.sum(1.5, 2.25));
    }

    @Test
    @DisplayName("retorna a soma de doubles com precisao decimal longa")
    void sumPreciseDecimalDoubles() {
        assertEquals(0.3, service.sum(0.1, 0.2), 1.0E-9);
    }

    @Test
    @DisplayName("retorna o outro operando quando um dos valores e zero")
    void sumWithZero() {
        assertEquals(7.4, service.sum(0.0, 7.4));
    }

    @Test
    @DisplayName("retorna zero quando soma valores opostos")
    void sumOppositeValues() {
        assertEquals(0.0, service.sum(-5.0, 5.0));
    }

    @Test
    @DisplayName("retorna a soma quando um dos valores e negativo")
    void sumWithNegativeValue() {
        assertEquals(1.5, service.sum(-2.5, 4.0));
    }

    @Test
    @DisplayName("retorna a soma de dois valores negativos")
    void sumTwoNegativeValues() {
        assertEquals(-7.0, service.sum(-2.5, -4.5));
    }
}
