package com.example.demo.controller.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = LiteralDto.class, name = "literal"),
    @JsonSubTypes.Type(value = BinaryOperationDto.class, name = "operation")
})
public sealed interface ExpressionDto permits LiteralDto, BinaryOperationDto {
}
