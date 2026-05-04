package com.example.demo.domain;

import java.math.RoundingMode;

public record CalculationContextOverride(Integer scale, RoundingMode roundingMode) {

    public CalculationContext resolve(CalculationContext inherited) {
        int resolvedScale = scale == null ? inherited.scale() : scale;
        var resolvedRoundingMode = roundingMode == null ? inherited.roundingMode() : roundingMode;
        return new CalculationContext(resolvedScale, resolvedRoundingMode);
    }
}
