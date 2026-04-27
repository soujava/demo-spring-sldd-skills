package com.example.demo.domain;

public sealed interface Expression permits Literal, BinaryOperation {
}
