package com.t4a.test;

import com.t4a.api.ActionType;
import com.t4a.api.JavaActionExecutor;
import com.google.cloud.vertexai.api.Type;
import com.google.cloud.vertexai.api.Schema; // Added
import com.google.protobuf.Value; // Added
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.Map; // Added

public class JavaActionExecutorCoverageTest {

    public static class TestExecutor extends JavaActionExecutor {
        @Override
        protected java.util.Map<String, Object> getProperties() {
            return java.util.Collections.emptyMap();
        }

        @Override
        protected com.google.gson.Gson getGson() {
            return new com.google.gson.Gson();
        }
    }

    @Test
    public void testMapTypeClass() {
        TestExecutor executor = new TestExecutor();
        Assertions.assertEquals(Type.STRING, executor.mapType(String.class));
        Assertions.assertEquals(Type.INTEGER, executor.mapType(int.class));
        Assertions.assertEquals(Type.INTEGER, executor.mapType(Integer.class));
        Assertions.assertEquals(Type.NUMBER, executor.mapType(double.class));
        Assertions.assertEquals(Type.NUMBER, executor.mapType(Double.class));
        Assertions.assertEquals(Type.BOOLEAN, executor.mapType(boolean.class));
        Assertions.assertEquals(Type.BOOLEAN, executor.mapType(Boolean.class));
        Assertions.assertEquals(Type.STRING, executor.mapType(Date.class));
        Assertions.assertEquals(Type.ARRAY, executor.mapType(String[].class));
        Assertions.assertEquals(Type.OBJECT, executor.mapType(Object.class));
    }

    @Test
    public void testMapTypeString() {
        TestExecutor executor = new TestExecutor();
        Assertions.assertEquals(Type.STRING, executor.mapType("String"));
        Assertions.assertEquals(Type.INTEGER, executor.mapType("int"));
        Assertions.assertEquals(Type.INTEGER, executor.mapType("integer"));
        Assertions.assertEquals(Type.NUMBER, executor.mapType("num"));
        Assertions.assertEquals(Type.BOOLEAN, executor.mapType("boolean"));
        Assertions.assertEquals(Type.ARRAY, executor.mapType("array"));
        Assertions.assertEquals(Type.OBJECT, executor.mapType("other"));
    }

    @Test
    public void testMapTypeForPojo() {
        TestExecutor executor = new TestExecutor();
        Assertions.assertEquals(Type.STRING, executor.mapTypeForPojo(String.class));
        Assertions.assertEquals(Type.INTEGER, executor.mapTypeForPojo(int.class));
        Assertions.assertEquals(Type.OBJECT, executor.mapTypeForPojo(Object.class));
    }

    public static class TestPojo {
        public String name;
        public int age;
    }

    @Test
    public void testMapClassToFun() throws ClassNotFoundException {
        TestExecutor executor = new TestExecutor();
        Schema schema = executor.mapClassToFun(TestPojo.class.getName(), "testFun", "desc");
        Assertions.assertEquals(Type.OBJECT, schema.getType());
        Assertions.assertTrue(schema.getPropertiesMap().containsKey("name"));
        Assertions.assertTrue(schema.getPropertiesMap().containsKey("age"));
    }

    @Test
    public void testProtobufToMap() {
        TestExecutor executor = new TestExecutor();
        Map<String, Value> protoMap = new java.util.HashMap<>();
        protoMap.put("key1", Value.newBuilder().setStringValue("value1").build());
        protoMap.put("key2", Value.newBuilder().setNumberValue(123).build());
        protoMap.put("key3", Value.newBuilder().setBoolValue(true).build());

        Map<String, Object> result = executor.protobufToMap(protoMap);
        Assertions.assertEquals("value1", result.get("key1"));
        Assertions.assertEquals(123.0, result.get("key2")); // Number value is double
        Assertions.assertEquals(true, result.get("key3"));
    }
}
