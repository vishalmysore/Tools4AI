package com.t4a.test;

import com.t4a.api.ActionRisk;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActionRiskTest {

    @Test
    void testAllValues() {
        ActionRisk[] risks = ActionRisk.values();
        assertEquals(3, risks.length);
    }

    @Test
    void testValueOf() {
        assertEquals(ActionRisk.LOW, ActionRisk.valueOf("LOW"));
        assertEquals(ActionRisk.MEDIUM, ActionRisk.valueOf("MEDIUM"));
        assertEquals(ActionRisk.HIGH, ActionRisk.valueOf("HIGH"));
    }

    @Test
    void testOrdinals() {
        assertEquals(0, ActionRisk.LOW.ordinal());
        assertEquals(1, ActionRisk.MEDIUM.ordinal());
        assertEquals(2, ActionRisk.HIGH.ordinal());
    }

    @Test
    void testNames() {
        assertEquals("LOW", ActionRisk.LOW.name());
        assertEquals("MEDIUM", ActionRisk.MEDIUM.name());
        assertEquals("HIGH", ActionRisk.HIGH.name());
    }
}
