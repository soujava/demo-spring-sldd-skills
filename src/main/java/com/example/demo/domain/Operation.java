package com.example.demo.domain;

@FunctionalInterface
public interface Operation {
    double apply(double left, double right);
}
