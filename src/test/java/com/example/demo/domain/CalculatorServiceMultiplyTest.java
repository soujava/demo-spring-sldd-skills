package com.example.demo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CalculatorService - multiplicacao")
class CalculatorServiceMultiplyTest {

    private CalculatorService service;

    @BeforeEach
    void setUp() {
        service = new CalculatorService(new OperationRegistry());
    }

    @Test
    @DisplayName("retorna 6.0 quando multiplica 3.0 por 2.0")
    void multiplyBasicValues() {
        assertEquals(6.0, service.multiply(3.0, 2.0));
    }

    @Test
    @DisplayName("retorna 3.75 quando multiplica 1.5 por 2.5")
    void multiplyDecimalValues() {
        assertEquals(3.75, service.multiply(1.5, 2.5));
    }

    @Test
    @DisplayName("retorna zero quando um dos fatores e zero")
    void multiplyWithZero() {
        assertEquals(0.0, service.multiply(7.4, 0.0));
    }

    @Test
    @DisplayName("retorna o outro fator quando um fator e 1.0")
    void multiplyWithOne() {
        assertEquals(9.4, service.multiply(1.0, 9.4));
    }

    @Test
    @DisplayName("retorna resultado negativo quando fatores tem sinais opostos")
    void multiplyWithOppositeSigns() {
        assertEquals(-10.0, service.multiply(-2.5, 4.0));
    }

    @Test
    @DisplayName("retorna resultado positivo quando dois fatores negativos")
    void multiplyTwoNegativeValues() {
        assertEquals(10.0, service.multiply(-2.5, -4.0));
    }

    @Test
    @DisplayName("retorna 0.02 quando multiplica 0.1 por 0.2 com precisao")
    void multiplyPreciseDecimalDoubles() {
        assertEquals(0.02, service.multiply(0.1, 0.2), 1.0E-9);
    }

    @Test
    @DisplayName("retorna resultado deterministico com valores grandes finitos")
    void multiplyLargeFiniteValues() {
        assertEquals(2.0E307, service.multiply(1.0E153, 2.0E154), 1.0E292);
    }
}
