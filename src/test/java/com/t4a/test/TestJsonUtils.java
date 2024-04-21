package com.t4a.test;

import com.t4a.JsonUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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





}
