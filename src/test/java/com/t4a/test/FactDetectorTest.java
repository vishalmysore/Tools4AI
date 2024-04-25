package com.t4a.test;

import com.t4a.api.ActionType;
import com.t4a.api.GuardRailException;
import com.t4a.detect.DetectValues;
import com.t4a.detect.FactDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class FactDetectorTest {

    private FactDetector factDetector;

    @BeforeEach
    public void setup() {
        factDetector = new FactDetector();
    }

    @Test
     void testGetActionType() {
        assertEquals(ActionType.FACT, factDetector.getActionType());
    }

    @Test
     void testGetDescription() {
        assertEquals("Fact Check in response", factDetector.getDescription());
    }

    @Test
     void testExecute() throws GuardRailException {
        assertNull(factDetector.execute(new DetectValues()));
    }
}