package com.example.demo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CalculationContextOverride")
class CalculationContextOverrideTest {

    @Test
    @DisplayName("resolve herda roundingMode quando override define apenas scale")
    void resolve_InheritsRoundingMode_WhenOnlyScaleIsProvided() {
        var inherited = new CalculationContext(6, RoundingMode.HALF_EVEN);
        var override = new CalculationContextOverride(2, null);

        CalculationContext resolved = override.resolve(inherited);

        assertEquals(2, resolved.scale());
        assertEquals(RoundingMode.HALF_EVEN, resolved.roundingMode());
    }

    @Test
    @DisplayName("resolve usa valores locais completos quando presentes")
    void resolve_UsesCompleteLocalValues_WhenProvided() {
        var inherited = new CalculationContext(6, RoundingMode.HALF_EVEN);
        var override = new CalculationContextOverride(4, RoundingMode.HALF_UP);

        CalculationContext resolved = override.resolve(inherited);

        assertEquals(4, resolved.scale());
        assertEquals(RoundingMode.HALF_UP, resolved.roundingMode());
    }
}
