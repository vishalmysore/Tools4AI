package com.t4a.test;

import com.t4a.detect.ActionState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActionStateTest {

    @Test
    void testAllEnumValues() {
        ActionState[] states = ActionState.values();
        assertEquals(7, states.length);
    }

    @Test
    void testGetValue() {
        assertEquals("submitted", ActionState.SUBMITTED.getValue());
        assertEquals("working", ActionState.WORKING.getValue());
        assertEquals("input-required", ActionState.INPUT_REQUIRED.getValue());
        assertEquals("completed", ActionState.COMPLETED.getValue());
        assertEquals("canceled", ActionState.CANCELED.getValue());
        assertEquals("failed", ActionState.FAILED.getValue());
        assertEquals("unknown", ActionState.UNKNOWN.getValue());
    }

    @Test
    void testForValueKnown() {
        assertEquals(ActionState.SUBMITTED, ActionState.forValue("submitted"));
        assertEquals(ActionState.WORKING, ActionState.forValue("working"));
        assertEquals(ActionState.INPUT_REQUIRED, ActionState.forValue("input-required"));
        assertEquals(ActionState.COMPLETED, ActionState.forValue("completed"));
        assertEquals(ActionState.CANCELED, ActionState.forValue("canceled"));
        assertEquals(ActionState.FAILED, ActionState.forValue("failed"));
        assertEquals(ActionState.UNKNOWN, ActionState.forValue("unknown"));
    }

    @Test
    void testForValueUnknownFallback() {
        assertEquals(ActionState.UNKNOWN, ActionState.forValue("nonexistent"));
        assertEquals(ActionState.UNKNOWN, ActionState.forValue(""));
        assertEquals(ActionState.UNKNOWN, ActionState.forValue("SUBMITTED")); // case-sensitive
    }
}
