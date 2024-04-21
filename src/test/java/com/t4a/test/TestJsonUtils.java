package com.t4a.test;

import com.t4a.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class TestJsonUtils {
    @Test
    public void testExtractJson() {
        JsonUtils utils = new JsonUtils();
        String input = "Some text before {\"key\":\"value\"} some text after";
        String extracted = utils.extractJson(input);
        Assertions.assertEquals("{\"key\":\"value\"}", extracted, "Extracted JSON does not match expected output");

        String invalidInput = "No brackets here";
        Assertions.assertEquals(invalidInput, utils.extractJson(invalidInput), "Extracted JSON should be same as input");
    }
    @Test
    public void testFetchGroupName() {
        JsonUtils utils = new JsonUtils();
        String groupJson = "{\"groupName\":\"Test Group\"}";
        String groupName = utils.fetchGroupName(groupJson);
        Assertions.assertEquals("Test Group", groupName, "Group name should match the expected value");


    }

    @Test
    public void testFetchActionNameWithEmptyJson() {
        JsonUtils utils = new JsonUtils();
        String emptyJson = "";
        Assertions.assertThrows(IllegalArgumentException.class, () -> utils.fetchActionName(emptyJson), "An exception should be thrown when the JSON string is empty");

    }

    @Test
    public void testFetchActionNameWithNullJson() {
        JsonUtils utils = new JsonUtils();
        String nullJson = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> utils.fetchActionName(nullJson), "An exception should be thrown when the JSON string is null");
    }

    @Test
    public void testFetchActionNameWithInvalidJson() {
        JsonUtils utils = new JsonUtils();
        String invalidJson = "This is not a valid JSON string";
        Assertions.assertThrows(JSONException.class, () -> utils.fetchActionName(invalidJson), "An exception should be thrown when the JSON string is not valid");
    }

    @Test
    public void testFetchActionNameWithMultipleKeys() {
        JsonUtils utils = new JsonUtils();
        String jsonWithMultipleKeys = "{\"actionName\":\"TestAction\", \"anotherKey\":\"AnotherValue\"}";
        String actionName = utils.fetchActionName(jsonWithMultipleKeys);
        Assertions.assertEquals("TestAction", actionName, "Action name should match the expected value even when there are multiple keys in the JSON string");
    }

    @Test
    public void testFetchActionNameWithNestedJson() {
        JsonUtils utils = new JsonUtils();
        String jsonWithNestedJson = "{\"actionName\":\"TestAction\", \"nestedJson\":{\"anotherKey\":\"AnotherValue\"}}";
        String actionName = utils.fetchActionName(jsonWithNestedJson);
        Assertions.assertEquals("TestAction", actionName, "Action name should match the expected value even when the JSON string contains nested JSON objects");
    }


    @Test
    public void testFetchActionNameNoAction() {
        JsonUtils utils = new JsonUtils();

        // Scenario 1: JSON string contains an actionName key
        String jsonWithActionName = "{\"actionName\":\"TestAction\"}";
        String actionName = utils.fetchActionName(jsonWithActionName);
        Assertions.assertEquals("TestAction", actionName, "Action name should match the expected value");

        // Scenario 2: JSON string does not contain an actionName key
        String jsonWithoutActionName = "{\"someOtherKey\":\"SomeValue\"}";
        actionName = utils.fetchActionName(jsonWithoutActionName);
        Assertions.assertEquals(jsonWithoutActionName, actionName, "When actionName key is missing, the original JSON string should be returned");

        // Scenario 3: The value of the actionName key is null
        String jsonWithNullActionName = "{\"actionName\":null}";
        actionName = utils.fetchActionName(jsonWithNullActionName);
        Assertions.assertEquals(jsonWithNullActionName, actionName, "When actionName value is null, the original JSON string should be returned");
    }




    @Test
    public void testFetchActionName() {
        JsonUtils utils = new JsonUtils();

        // Valid JSON with actionName
        String validJson = "{\"actionName\":\"TestAction\"}";
        String actionName = utils.fetchActionName(validJson);
        Assertions.assertEquals("TestAction", actionName, "Expected actionName to be 'TestAction'");

        // Missing actionName key
        String invalidJson = "{}";
        actionName = utils.fetchActionName(invalidJson);
        Assertions.assertEquals(invalidJson, actionName, "Expected the original JSON string when actionName is missing");

        // Null actionName scenario
        String nullActionNameJson = "{\"actionName\":null}";
        actionName = utils.fetchActionName(nullActionNameJson);
        Assertions.assertEquals(nullActionNameJson, actionName, "Expected the original JSON string when actionName is null");


    }

    @Test
    public void testBuildBlankMapJsonObject() throws NoSuchFieldException {
        JsonUtils utils = new JsonUtils();

        // Get the 'testMap' field from the TestClass
        Field testMapField = TestClass.class.getDeclaredField("testMap");

        // Call the method with the 'testMap' field
        JSONObject jsonObject = utils.buildBlankMapJsonObject(testMapField);

        // Check if the resulting JSON object has the correct structure
        Assertions.assertEquals("java.util.Map", jsonObject.getString("className"), "The className should be 'java.util.Map'");


        // Check if the 'fields' array has the correct structure
        Assertions.assertTrue(jsonObject.getJSONArray("fields").getJSONObject(0).has("key"), "The fields array should contain an object with a 'key' key");
        Assertions.assertTrue(jsonObject.getJSONArray("fields").getJSONObject(0).has("value"), "The fields array should contain an object with a 'value' key");
    }

    @Test
    public void testBuildBlankListJsonObject() throws NoSuchFieldException {
        JsonUtils utils = new JsonUtils();

        // Get the 'hobbies' field from the TestClass
        Field testListField = TestClass.class.getDeclaredField("hobbies");

        // Call the method with the 'hobbies' field
        JSONObject jsonObject = utils.buildBlankListJsonObject(testListField);

        // Check if the resulting JSON object has the correct structure
        Assertions.assertEquals("java.util.List", jsonObject.getString("className"), "The className should be 'java.util.List'");
        Assertions.assertEquals("put each value inside fieldValue", jsonObject.getString("prompt"), "The prompt should be 'put each value inside fieldValue'");


        // Check if the 'fields' array has the correct structure
        Assertions.assertTrue(jsonObject.getJSONArray("fields").getJSONObject(0).has("fieldValue"), "The fields array should contain an object with a 'fieldValue' key");
    }
    @Test
    public void testConvertClassToJSONString() {
        JsonUtils utils = new JsonUtils();

        // Test with a class that has primitive types
        String jsonString = utils.convertClassToJSONString(TestClass.class);
        Assertions.assertNotNull(jsonString, "The resulting JSON string should not be null");
        Assertions.assertTrue(jsonString.contains("name"), "The resulting JSON string should contain the 'name' field");
        Assertions.assertTrue(jsonString.contains("age"), "The resulting JSON string should contain the 'age' field");
        Assertions.assertTrue(jsonString.contains("salary"), "The resulting JSON string should contain the 'salary' field");
        Assertions.assertTrue(jsonString.contains("isActive"), "The resulting JSON string should contain the 'isActive' field");

        // Test with a class that has complex types (List and Map)
        Assertions.assertTrue(jsonString.contains("hobbies"), "The resulting JSON string should contain the 'hobbies' field");
        Assertions.assertTrue(jsonString.contains("scores"), "The resulting JSON string should contain the 'scores' field");
    }
    @Test
    public void testPopulateClassFromJson() throws Exception {
        JsonUtils utils = new JsonUtils();

        // Create a JSON string that represents a Person object
        String jsonString = "{\n" +
                "    \"className\": \"com.t4a.test.Person\",\n" +
                "    \"fields\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"name\",\n" +
                "            \"fieldValue\": \"John Doe\",\n" +
                "            \"fieldType\": \"String\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"age\",\n" +
                "            \"fieldValue\": 30,\n" +
                "            \"fieldType\": \"int\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        // Use the populateClassFromJson method to create a Person object from the JSON string
        Object obj = utils.populateClassFromJson(jsonString);

        // Check if the resulting object is an instance of the Person class
        Assertions.assertTrue(obj instanceof Person, "The resulting object should be an instance of the Person class");

        // Cast the object to a Person and check if the properties are as expected
        Person person = (Person) obj;
        Assertions.assertEquals("John Doe", person.getName(), "The name property should be 'John Doe'");
        Assertions.assertEquals(30, person.getAge(), "The age property should be 30");
    }


}
