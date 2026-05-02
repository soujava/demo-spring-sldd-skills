package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SubtractOperation")
class SubtractOperationTest {

    private SubtractOperation operation;

    @BeforeEach
    void setUp() {
        operation = new SubtractOperation();
    }

    @Test
    @DisplayName("retorna 6.7 quando 10 - 3.3 (precisao BigDecimal)")
    void subtractWithBigDecimalPrecision() {
        double result = operation.apply(10.0, 3.3);
        assertThat(result).isEqualTo(6.7);
    }

    @Test
    @DisplayName("retorna -0.1 quando 0.1 - 0.2 (precisao BigDecimal)")
    void subtractPreciseDecimalValues() {
        double result = operation.apply(0.1, 0.2);
        assertThat(result).isEqualTo(-0.1);
    }
}
