package com.t4a.api;

import com.google.cloud.vertexai.api.Type;
import com.google.gson.Gson;
import com.google.protobuf.Value;
import com.google.protobuf.Struct;
import com.google.protobuf.ListValue;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JavaActionExecutorMoreCoverageTest {

    public static class SimplePojo {
        public String name;
        public int age;
    }

    private static class TestExecutor extends JavaActionExecutor {
        private final Map<String, Object> props = new HashMap<>();
        private final Gson gson = new Gson();

        @Override
        public Map<String, Object> getProperties() {
            return props;
        }

        @Override
        public Gson getGson() {
            return gson;
        }
    }

    @Test
    public void testMapTypeString() {
        TestExecutor executor = new TestExecutor();
        assertEquals(Type.STRING, executor.mapType("String"));
        assertEquals(Type.INTEGER, executor.mapType("int"));
        assertEquals(Type.INTEGER, executor.mapType("integer"));
        assertEquals(Type.NUMBER, executor.mapType("num"));
        assertEquals(Type.BOOLEAN, executor.mapType("boolean"));
        assertEquals(Type.ARRAY, executor.mapType("array"));
        assertEquals(Type.OBJECT, executor.mapType("other"));
    }

    @Test
    public void testMapTypeClass() {
        TestExecutor executor = new TestExecutor();
        assertEquals(Type.STRING, executor.mapType(String.class));
        assertEquals(Type.INTEGER, executor.mapType(Integer.class));
        assertEquals(Type.INTEGER, executor.mapType(int.class));
        assertEquals(Type.NUMBER, executor.mapType(Double.class));
        assertEquals(Type.NUMBER, executor.mapType(double.class));
        assertEquals(Type.BOOLEAN, executor.mapType(Boolean.class));
        assertEquals(Type.BOOLEAN, executor.mapType(boolean.class));
        assertEquals(Type.ARRAY, executor.mapType(String[].class));
        assertEquals(Type.STRING, executor.mapType(java.util.Date.class));
        assertEquals(Type.OBJECT, executor.mapType(Object.class));
    }

    @Test
    public void testProtobufToMap() {
        TestExecutor executor = new TestExecutor();
        Map<String, Value> protobufMap = new HashMap<>();

        protobufMap.put("string", Value.newBuilder().setStringValue("val").build());
        protobufMap.put("number", Value.newBuilder().setNumberValue(123.0).build());
        protobufMap.put("bool", Value.newBuilder().setBoolValue(true).build());
        protobufMap.put("null", Value.newBuilder().setNullValueValue(0).build());

        Struct struct = Struct.newBuilder().putFields("nested", Value.newBuilder().setStringValue("nestedVal").build())
                .build();
        protobufMap.put("struct", Value.newBuilder().setStructValue(struct).build());

        ListValue list = ListValue.newBuilder().addValues(Value.newBuilder().setStringValue("item1").build()).build();
        protobufMap.put("list", Value.newBuilder().setListValue(list).build());

        Map<String, Object> result = executor.protobufToMap(protobufMap);
        assertEquals("val", result.get("string"));
        assertEquals(123.0, result.get("number"));
        assertEquals(true, result.get("bool"));
        assertNull(result.get("null"));
        assertTrue(result.get("struct") instanceof Map);
        assertEquals("nestedVal", ((Map) result.get("struct")).get("nested"));
    }

    @Test
    public void testGetPropertyValuesMapFromString() {
        TestExecutor executor = new TestExecutor();
        String response = "key1=value1, key2=value2, invalidEntry, key3=value3";
        Map<String, Object> result = executor.getPropertyValuesMap(response);

        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
        assertEquals("value3", result.get("key3"));
        assertNull(result.get("invalidEntry"));
    }

    @Test
    public void testGetBuildForJson() {
        TestExecutor executor = new TestExecutor();
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "val1");
        Map<String, Object> nested = new HashMap<>();
        nested.put("key2", 123);
        map.put("key3", nested);

        List<Object> list = new ArrayList<>();
        Map<String, Object> listItem = new HashMap<>();
        listItem.put("key4", true);
        list.add(listItem);
        map.put("key5", list);

        assertNotNull(executor.getBuildForJson(map));
    }

    @Test
    public void testGetBuild() {
        TestExecutor executor = new TestExecutor();
        Map<String, Object> properties = new HashMap<>();
        properties.put("prop1", Type.STRING);
        properties.put("prop2", SimplePojo.class);

        assertNotNull(executor.getBuild(properties));
    }

    @Test
    public void testGetBuildSingle() {
        TestExecutor executor = new TestExecutor();
        assertNotNull(executor.getBuild(Type.STRING, "prop"));
    }

    @Test
    public void testMapClassToFun() throws ClassNotFoundException {
        TestExecutor executor = new TestExecutor();
        assertNotNull(executor.mapClassToFun(SimplePojo.class.getName(), "fun", "desc"));
    }
}
