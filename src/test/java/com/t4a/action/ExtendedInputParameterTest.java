package com.t4a.action;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExtendedInputParameterTest {

    @Test
    public void testExtendedInputParameter() {
        ExtendedInputParameter param = new ExtendedInputParameter("name", "type");
        assertEquals("name", param.getName());
        assertEquals("type", param.getType());
        assertFalse(param.hasDefaultValue());
        assertNull(param.getDefaultValueStr());

        param.setDefaultValueStr("default");
        param.setHasDefaultValue(true);
        assertEquals("default", param.getDefaultValueStr());
        assertTrue(param.hasDefaultValue());

        ExtendedInputParameter param2 = new ExtendedInputParameter("n2", "t2", "d2", true);
        assertEquals("n2", param2.getName());
        assertEquals("t2", param2.getType());
        assertEquals("d2", param2.getDefaultValueStr());
        assertTrue(param2.hasDefaultValue());
    }
}
