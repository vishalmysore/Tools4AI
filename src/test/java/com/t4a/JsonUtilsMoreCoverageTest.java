package com.t4a;

import com.t4a.annotations.ListType;
import com.t4a.annotations.MapKeyType;
import com.t4a.annotations.MapValueType;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

class JsonUtilsMoreCoverageTest {

    public static class TestClass {
        @ListType(String.class)
        public List<String> stringList;

        @MapKeyType(String.class)
        @MapValueType(Integer.class)
        public Map<String, Integer> stringIntMap;

        public String name;
    }

    @Test
    void testBuildBlankListJsonObject() throws NoSuchFieldException {
        JsonUtils utils = new JsonUtils();
        Field field = TestClass.class.getDeclaredField("stringList");
        JSONObject result = utils.buildBlankListJsonObject(field);

        Assertions.assertEquals("java.util.List", result.getString("className"));
        Assertions.assertTrue(result.has("fields"));
    }

    @Test
    void testBuildBlankMapJsonObject() throws NoSuchFieldException {
        JsonUtils utils = new JsonUtils();
        Field field = TestClass.class.getDeclaredField("stringIntMap");
        JSONObject result = utils.buildBlankMapJsonObject(field);

        Assertions.assertEquals("java.util.Map", result.getString("className"));
        Assertions.assertEquals("java.lang.String", result.getString("keyType"));
        Assertions.assertEquals("java.lang.Integer", result.getString("valueType"));
    }

    @Test
    void testExtractJson() {
        JsonUtils utils = new JsonUtils();
        String input = "some text { \"key\": \"value\" } more text";
        String result = utils.extractJson(input);
        Assertions.assertEquals("{ \"key\": \"value\" }", result);

        Assertions.assertEquals("plain string", utils.extractJson("plain string"));
    }

    @Test
    void testFetchActionNameWithEmpty() {
        JsonUtils utils = new JsonUtils();
        Assertions.assertThrows(IllegalArgumentException.class, () -> utils.fetchActionName(""));
    }

    @Test
    void testFetchActionNameWithNormalJson() {
        JsonUtils utils = new JsonUtils();
        String json = "{ \"actionName\": \"myAction\" }";
        Assertions.assertEquals("myAction", utils.fetchActionName(json));
    }

    @Test
    void testGetFieldValue() {
        JsonUtils utils = new JsonUtils();
        String json = "{ \"fieldValue\": \"myValue\" }";
        Assertions.assertEquals("myValue", utils.getFieldValue(json, "any"));
    }

    @Test
    void testGetFieldValueFromMultipleFields() {
        JsonUtils utils = new JsonUtils();
        String json = "{ \"fields\": [ { \"fieldName\": \"name\", \"fieldValue\": \"vishal\" } ] }";
        Assertions.assertEquals("vishal", utils.getFieldValueFromMultipleFields(json, "name"));
        Assertions.assertNull(utils.getFieldValueFromMultipleFields(json, "age"));
    }

    @Test
    void testConvertObjectToJson() {
        TestClass obj = new TestClass();
        obj.name = "vishal";
        String json = JsonUtils.convertObjectToJson(obj);
        Assertions.assertTrue(json.contains("\"name\":\"vishal\""));
    }

    @Test
    void testCreateJson() {
        JsonUtils utils = new JsonUtils();
        String json = utils.createJson("key1", "key2");
        Assertions.assertTrue(json.contains("key1"));
        Assertions.assertTrue(json.contains("key2"));
        Assertions.assertTrue(json.contains("fields"));
    }
}
