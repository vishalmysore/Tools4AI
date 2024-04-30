package com.t4a.test;





import com.t4a.action.ExtendedInputParameter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ExtendedInputParameterTest {

    @Test
    public void testExtendedInputParameter() {
        // Create an instance of ExtendedInputParameter
        ExtendedInputParameter inputParameter = new ExtendedInputParameter("TestName", "TestValue");

        // Test the methods of ExtendedInputParameter
        assertEquals("TestName", inputParameter.getName());
        assertEquals(false, inputParameter.hasDefaultValue());
        assertEquals("TestValue", inputParameter.getType());
        assertNull(inputParameter.getDefaultValueStr());
    }
}