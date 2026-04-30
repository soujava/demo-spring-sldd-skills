package com.example.demo.domain.expression;

import java.math.BigDecimal;

public record Add(Expression left, Expression right) implements Expression {
    @Override
    public ExpressionLiteral evaluate() {
        double lv = left.evaluate().value();
        double rv = right.evaluate().value();
        BigDecimal bdLeft = BigDecimal.valueOf(lv);
        BigDecimal bdRight = BigDecimal.valueOf(rv);
        BigDecimal result = bdLeft.add(bdRight);
        return new ExpressionLiteral(result.doubleValue());
    }
}