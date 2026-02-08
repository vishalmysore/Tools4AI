package com.t4a.test;

import com.t4a.api.AIPlatform;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AIPlatformTest {
    @Test
    void testEnumValues() {
        assertNotNull(AIPlatform.valueOf("GEMINI"));
        assertNotNull(AIPlatform.valueOf("OPENAI"));
        assertNotNull(AIPlatform.valueOf("LOCALAI"));
        assertNotNull(AIPlatform.valueOf("ANTHROPIC"));
    }

    @Test
    void testValuesCount() {
        assertEquals(4, AIPlatform.values().length);
    }
}
