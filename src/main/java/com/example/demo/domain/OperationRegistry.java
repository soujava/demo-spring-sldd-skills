package com.example.demo.domain;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class OperationRegistry {

    private final Map<Operator, Operation> operations = new EnumMap<>(Operator.class);

    public OperationRegistry() {
        operations.put(Operator.SUM, new SumOperation());
        operations.put(Operator.SUBTRACT, new SubtractOperation());
        operations.put(Operator.MULTIPLY, new MultiplyOperation());
        operations.put(Operator.DIVIDE, new DivideOperation());
        operations.put(Operator.POWER, new PowerOperation());
        operations.put(Operator.ROOT, new RootOperation());
    }

    public Operation lookup(Operator operator) {
        Operation operation = operations.get(operator);
        if (operation == null) {
            throw new IllegalArgumentException("Unknown operator: " + operator);
        }
        return operation;
    }

    public Map<Operator, Operation> all() {
        return Map.copyOf(operations);
    }
}
