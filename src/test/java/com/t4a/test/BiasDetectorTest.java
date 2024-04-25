package com.t4a.test;

import com.t4a.api.ActionType;
import com.t4a.api.GuardRailException;
import com.t4a.detect.BiasDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BiasDetectorTest {

    private BiasDetector biasDetector;

    @BeforeEach
    public void setup() {
        biasDetector = new BiasDetector();
    }

    @Test
    public void testGetActionType() {
        assertEquals(ActionType.BIAS, biasDetector.getActionType());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Detect Bias in response", biasDetector.getDescription());
    }

    @Test
    public void testExecute() throws GuardRailException {
        assertNull(biasDetector.execute(null));
    }
}