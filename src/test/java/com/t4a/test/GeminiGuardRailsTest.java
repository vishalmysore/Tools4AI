package com.t4a.test;

import com.t4a.api.GeminiGuardRails;
import com.t4a.api.GuardRailException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeminiGuardRailsTest {

    private GeminiGuardRails geminiGuardRails;

    @BeforeEach
    public void setUp() {
        geminiGuardRails = new GeminiGuardRails();

    }

    @Test
    public void testMethod1() throws GuardRailException {
        Assertions.assertFalse(geminiGuardRails.validateRequest("test"));
    }

}
