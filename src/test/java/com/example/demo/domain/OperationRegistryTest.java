package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OperationRegistry")
class OperationRegistryTest {

    private OperationRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new OperationRegistry();
    }

    @Test
    @DisplayName("contem todas as 6 operacoes registradas")
    void containsAllOperations() {
        assertThat(registry.all()).containsOnlyKeys(
            Operator.SUM, Operator.SUBTRACT, Operator.MULTIPLY,
            Operator.DIVIDE, Operator.POWER, Operator.ROOT
        );
    }

    @Test
    @DisplayName("lookup(DIVIDE) retorna DivideOperation")
    void lookupDivideReturnsDivideOperation() {
        Operation operation = registry.lookup(Operator.DIVIDE);
        assertThat(operation).isInstanceOf(DivideOperation.class);
    }

    @Test
    @DisplayName("lookup(SUM) retorna SumOperation")
    void lookupSumReturnsSumOperation() {
        Operation operation = registry.lookup(Operator.SUM);
        assertThat(operation).isInstanceOf(SumOperation.class);
    }
}
