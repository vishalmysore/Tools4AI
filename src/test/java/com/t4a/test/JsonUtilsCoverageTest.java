package com.t4a.test;

import com.t4a.JsonUtils;
import com.t4a.annotations.Prompt;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

public class JsonUtilsCoverageTest {

    @Test
    public void testPopulateComplexObject() throws Exception {
        JsonUtils utils = new JsonUtils();

        JSONObject json = new JSONObject();
        json.put("className", CoverageComplexObject.class.getName());

        JSONArray fields = new JSONArray();

        // String
        JSONObject strField = new JSONObject();
        strField.put("fieldName", "stringField");
        strField.put("fieldType", "String");
        strField.put("fieldValue", "testValue");
        fields.put(strField);

        // Int
        JSONObject intField = new JSONObject();
        intField.put("fieldName", "intField");
        intField.put("fieldType", "int");
        intField.put("fieldValue", 123);
        fields.put(intField);

        // Date
        JSONObject dateField = new JSONObject();
        dateField.put("fieldName", "dateField");
        dateField.put("fieldType", "Date");
        dateField.put("fieldValue", "2023-01-01");
        dateField.put("dateFormat", "yyyy-MM-dd");
        fields.put(dateField);

        // Nested
        JSONObject nestedField = new JSONObject();
        nestedField.put("fieldName", "nested");
        nestedField.put("className", CoverageNestedObject.class.getName());
        nestedField.put("fieldType", CoverageNestedObject.class.getName());
        JSONArray nestedFields = new JSONArray();
        JSONObject nestedName = new JSONObject();
        nestedName.put("fieldName", "name");
        nestedName.put("fieldType", "String");
        nestedName.put("fieldValue", "nestedName");
        nestedFields.put(nestedName);
        nestedField.put("fields", nestedFields);
        fields.put(nestedField);

        // Array
        JSONObject arrayField = new JSONObject();
        arrayField.put("fieldName", "intArray");
        arrayField.put("fieldType", "int[]");
        JSONArray intArray = new JSONArray();
        intArray.put(1);
        intArray.put(2);
        arrayField.put("fieldValue", intArray);
        fields.put(arrayField);

        // List
        JSONObject listField = new JSONObject();
        listField.put("fieldName", "stringList");
        listField.put("fieldType", "list");
        listField.put("className", String.class.getName());
        JSONArray listArray = new JSONArray();
        listArray.put("l1");
        listArray.put("l2");
        listField.put("fieldValue", listArray);
        fields.put(listField);

        // Map
        JSONObject mapField = new JSONObject();
        mapField.put("fieldName", "mapField");
        mapField.put("fieldType", "map");
        JSONArray mapArray = new JSONArray();
        JSONObject mapEntry = new JSONObject();
        mapEntry.put("key", "key1");
        mapEntry.put("value", "100");
        mapArray.put(mapEntry);
        mapField.put("fields", mapArray);
        fields.put(mapField);

        json.put("fields", fields);

        Object result = utils.populateObject(json, null);
        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertTrue(result instanceof CoverageComplexObject);
        CoverageComplexObject obj = (CoverageComplexObject) result;
        Assertions.assertEquals("testValue", obj.stringField);
        Assertions.assertEquals(123, obj.intField);
        Assertions.assertNotNull(obj.dateField);
        Assertions.assertNotNull(obj.nested);
        Assertions.assertEquals("nestedName", obj.nested.name);
        Assertions.assertNotNull(obj.intArray);
        Assertions.assertEquals(2, obj.intArray.length);
        Assertions.assertEquals(1, obj.intArray[0]);
        Assertions.assertNotNull(obj.stringList);
        Assertions.assertEquals(2, obj.stringList.size());
        Assertions.assertEquals("l1", obj.stringList.get(0));
        Assertions.assertNotNull(obj.mapField);
        // Note: JsonUtils puts Map<String,String> into the field, forcing runtime check
        // to suffice.
        Assertions.assertEquals("100", String.valueOf(obj.mapField.get("key1")));
    }

    @Test
    public void testConvertClassToJSONString() {
        JsonUtils utils = new JsonUtils();
        String result = utils.convertClassToJSONString(CoverageComplexObject.class);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("stringField"));
        Assertions.assertTrue(result.contains("intField"));
        Assertions.assertTrue(result.contains("nested"));
        Assertions.assertTrue(result.contains("CoverageNestedObject"));
        Assertions.assertTrue(result.contains("dateFormat"));
    }

    public void testMethod(String p1, @Prompt(describe = "desc") int p2, List<String> list) {
    }

    @Test
    public void testConvertMethodTOJsonString() throws NoSuchMethodException {
        JsonUtils utils = new JsonUtils();
        Method method = this.getClass().getMethod("testMethod", String.class, int.class, List.class);
        String result = utils.convertMethodTOJsonString(method);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("testMethod"));
        Assertions.assertTrue(result.contains("p1"));
        Assertions.assertTrue(result.contains("p2"));
        Assertions.assertTrue(result.contains("desc"));
        Assertions.assertTrue(result.contains("list"));
    }

    @Test
    public void testExtractJson() {
        JsonUtils utils = new JsonUtils();
        String input = "Some text before {\"key\":\"value\"} and after";
        String result = utils.extractJson(input);
        Assertions.assertEquals("{\"key\":\"value\"}", result);
    }

    @Test
    public void testFetchActionName() {
        JsonUtils utils = new JsonUtils();
        String json = "{\"actionName\":\"testAction\"}";
        String actionName = utils.fetchActionName(json);
        Assertions.assertEquals("testAction", actionName);
    }

    @Test
    public void testFetchGroupName() {
        JsonUtils utils = new JsonUtils();
        String json = "{\"groupName\":\"testGroup\"}";
        String groupName = utils.fetchGroupName(json);
        Assertions.assertEquals("testGroup", groupName);
    }
}
