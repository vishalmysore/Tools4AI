package com.t4a.test;



import com.t4a.action.http.InputParameter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InputParameterTest {

    @Test
    public void testInputParameter() {
        String name = "Test name";
        String type = "Test type";
        String description = "Test description";
        InputParameter inputParameter = new InputParameter(name, type, description);

        assertEquals(name, inputParameter.getName());
        assertEquals(type, inputParameter.getType());
        assertEquals(description, inputParameter.getDescription());
    }

    @Test
    public void testHasDefaultValue() {
        InputParameter inputParameter = new InputParameter();
        assertFalse(inputParameter.hasDefaultValue());

        inputParameter.setDefaultValue("Default value");
        assertTrue(inputParameter.hasDefaultValue());
    }
}
