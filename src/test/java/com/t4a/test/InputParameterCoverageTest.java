package com.t4a.test;

import com.t4a.action.http.InputParameter;
import com.t4a.action.http.ParamLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InputParameterCoverageTest {

    @Test
    public void testHasDefaultValue() {
        InputParameter param = new InputParameter("name", "type", "desc");
        Assertions.assertFalse(param.hasDefaultValue());

        param.setDefaultValue("default");
        Assertions.assertTrue(param.hasDefaultValue());
    }

    @Test
    public void testAllArgsConstructor() {
        InputParameter param = new InputParameter("name", "type", "desc", "default", ParamLocation.QUERY);
        Assertions.assertEquals("name", param.getName());
        Assertions.assertEquals("type", param.getType());
        Assertions.assertEquals("desc", param.getDescription());
        Assertions.assertEquals("default", param.getDefaultValue());
        Assertions.assertEquals(ParamLocation.QUERY, param.getLocation());
    }

    @Test
    public void testSettersGetters() {
        InputParameter param = new InputParameter();
        param.setName("testName");
        param.setType("String");
        param.setDescription("test desc");
        param.setDefaultValue("testDefault");
        param.setLocation(ParamLocation.PATH);

        Assertions.assertEquals("testName", param.getName());
        Assertions.assertEquals("String", param.getType());
        Assertions.assertEquals("test desc", param.getDescription());
        Assertions.assertEquals("testDefault", param.getDefaultValue());
        Assertions.assertEquals(ParamLocation.PATH, param.getLocation());
    }
}
