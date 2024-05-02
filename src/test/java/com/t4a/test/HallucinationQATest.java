package com.t4a.test;

import com.t4a.detect.HallucinationQA;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HallucinationQATest {
    @Test
    void testCalculateTruthPercent() {
        HallucinationQA hallucinationQA = new HallucinationQA();
        hallucinationQA.setTruthPercentage("75%");

        double truthPercent = hallucinationQA.calculateTruthPercent();

        assertEquals(75.0, truthPercent);
    }
}
