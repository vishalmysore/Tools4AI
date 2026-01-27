package com.t4a.api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JavaMethodInvokerMoreCoverageTest {

    public static class SamplePojo {
        private String name;
        private int age;

        public SamplePojo() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Test
    public void testParseSimple() {
        JavaMethodInvoker invoker = new JavaMethodInvoker();
        JSONObject json = new JSONObject();
        json.put("methodName", "testMethod");
        json.put("returnType", "void");
        JSONArray params = new JSONArray();

        JSONObject param1 = new JSONObject();
        param1.put("name", "p1");
        param1.put("type", "String");
        param1.put("fieldValue", "val1");
        params.put(param1);

        JSONObject param2 = new JSONObject();
        param2.put("name", "p2");
        param2.put("type", "int");
        param2.put("fieldValue", "123");
        params.put(param2);

        json.put("parameters", params);

        Object[] result = invoker.parse(json.toString());
        assertNotNull(result);
        assertEquals(2, result.length);
        List<Class<?>> types = (List<Class<?>>) result[0];
        List<Object> values = (List<Object>) result[1];

        assertEquals(String.class, types.get(0));
        assertEquals("val1", values.get(0));
        assertEquals(int.class, types.get(1));
        assertEquals(123, values.get(1));
    }

    @Test
    public void testParseWithPojo() {
        JavaMethodInvoker invoker = new JavaMethodInvoker();
        JSONObject json = new JSONObject();
        json.put("methodName", "testPojo");
        json.put("returnType", "void");
        JSONArray params = new JSONArray();

        JSONObject param1 = new JSONObject();
        param1.put("name", "pojo");
        param1.put("type", SamplePojo.class.getName());
        JSONArray fields = new JSONArray();

        JSONObject field1 = new JSONObject();
        field1.put("fieldName", "name");
        field1.put("fieldType", "String");
        field1.put("fieldValue", "John");
        fields.put(field1);

        JSONObject field2 = new JSONObject();
        field2.put("fieldName", "age");
        field2.put("fieldType", "int");
        field2.put("fieldValue", "30");
        fields.put(field2);

        param1.put("fields", fields);
        params.put(param1);
        json.put("parameters", params);

        Object[] result = invoker.parse(json.toString());
        assertNotNull(result);
        assertEquals(2, result.length);
        List<Object> values = (List<Object>) result[1];
        SamplePojo pojo = (SamplePojo) values.get(0);
        assertEquals("John", pojo.getName());
        assertEquals(30, pojo.getAge());
    }

    @Test
    public void testGetValueTypes() throws Exception {
        JavaMethodInvoker invoker = new JavaMethodInvoker();

        JSONObject json = new JSONObject();
        json.put("methodName", "m");
        json.put("returnType", "v");
        JSONArray params = new JSONArray();

        String[] types = { "double", "boolean", "Date" };
        String[] vals = { "1.5", "true", "2024/01/01" };

        for (int i = 0; i < types.length; i++) {
            JSONObject p = new JSONObject();
            p.put("name", "p" + i);
            p.put("type", types[i]);
            p.put("fieldValue", vals[i]);
            if (types[i].equals("Date")) {
                p.put("dateFormat", "yyyy/MM/dd");
            }
            params.put(p);
        }
        json.put("parameters", params);

        Object[] result = invoker.parse(json.toString());
        assertNotNull(result);
        assertEquals(2, result.length);
        List<Object> values = (List<Object>) result[1];

        assertEquals(1.5, values.get(0));
        assertEquals(true, values.get(1));
        assertTrue(values.get(2) instanceof Date);
    }

    @Test
    public void testParseInvalidJson() {
        JavaMethodInvoker invoker = new JavaMethodInvoker();
        try {
            invoker.parse("invalid");
            fail("Expected JSONException");
        } catch (org.json.JSONException e) {
            // expected
        }
    }
}
