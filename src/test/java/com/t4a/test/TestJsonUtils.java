package com.t4a.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.t4a.JsonUtils;
import com.t4a.annotations.MapKeyType;
import com.t4a.annotations.MapValueType;
import com.t4a.examples.pojo.Organization;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.*;

class TestJsonUtils {
    @Test
     void testExtractJson() {
        JsonUtils utils = new JsonUtils();
        String input = "Some text before {\"key\":\"value\"} some text after";
        String extracted = utils.extractJson(input);
        Assertions.assertEquals("{\"key\":\"value\"}", extracted, "Extracted JSON does not match expected output");

        String invalidInput = "No brackets here";
        Assertions.assertEquals(invalidInput, utils.extractJson(invalidInput), "Extracted JSON should be same as input");
    }
    @Test
     void testFetchGroupName() {
        JsonUtils utils = new JsonUtils();
        String groupJson = "{\"groupName\":\"Test Group\"}";
        String groupName = utils.fetchGroupName(groupJson);
        Assertions.assertEquals("Test Group", groupName, "Group name should match the expected value");


    }

    @Test
     void testFetchActionNameWithEmptyJson() {
        JsonUtils utils = new JsonUtils();
        String emptyJson = "";
        Assertions.assertThrows(IllegalArgumentException.class, () -> utils.fetchActionName(emptyJson), "An exception should be thrown when the JSON string is empty");

    }

    @Test
     void testFetchActionNameWithNullJson() {
        JsonUtils utils = new JsonUtils();
        String nullJson = null;
        Assertions.assertThrows(java.lang.IllegalArgumentException.class, () -> utils.fetchActionName(nullJson), "An exception should be thrown when the JSON string is null");
    }

    @Test
     void testFetchActionNameWithInvalidJson() {
        JsonUtils utils = new JsonUtils();
        String invalidJson = "This is not a valid JSON string";
        Assertions.assertThrows(JSONException.class, () -> utils.fetchActionName(invalidJson), "An exception should be thrown when the JSON string is not valid");
    }

    @Test
     void testFetchActionNameWithMultipleKeys() {
        JsonUtils utils = new JsonUtils();
        String jsonWithMultipleKeys = "{\"actionName\":\"TestAction\", \"anotherKey\":\"AnotherValue\"}";
        String actionName = utils.fetchActionName(jsonWithMultipleKeys);
        Assertions.assertEquals("TestAction", actionName, "Action name should match the expected value even when there are multiple keys in the JSON string");
    }

    @Test
     void testFetchActionNameWithNestedJson() {
        JsonUtils utils = new JsonUtils();
        String jsonWithNestedJson = "{\"actionName\":\"TestAction\", \"nestedJson\":{\"anotherKey\":\"AnotherValue\"}}";
        String actionName = utils.fetchActionName(jsonWithNestedJson);
        Assertions.assertEquals("TestAction", actionName, "Action name should match the expected value even when the JSON string contains nested JSON objects");
    }


    @Test
     void testFetchActionNameNoAction() {
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
     void testFetchActionName() {
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
     void testBuildBlankMapJsonObject() throws NoSuchFieldException {
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
     void testBuildBlankListJsonObject() throws NoSuchFieldException {
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
    void testFieldWithMapKeyTypeAnnotation() throws Exception {
        JsonUtils utils = new JsonUtils();

        // Create a test class with a field that has the MapKeyType annotation
        class TestClass {
            @MapKeyType(String.class)
            Map<String, Integer> testMap;
        }

        // Get the 'testMap' field from the TestClass
        Field testMapField = TestClass.class.getDeclaredField("testMap");

        // Call the buildBlankMapJsonObject method with the 'testMap' field
        JSONObject jsonObject = utils.buildBlankMapJsonObject(testMapField);

        // Assert that the resulting JSON object has the correct structure
        Assertions.assertEquals("java.util.Map", jsonObject.getString("className"), "The className should be 'java.util.Map'");
        Assertions.assertEquals("java.lang.String", jsonObject.getString("keyType"), "The keyType should be 'String'");
    }

    @Test
    void testFieldWithMapValueTypeAnnotation() throws Exception {
        JsonUtils utils = new JsonUtils();

        // Create a test class with a field that has the MapValueType annotation
        class TestClass {
            @MapValueType(String.class)
            Map<Integer, String> testMap;
        }

        // Get the 'testMap' field from the TestClass
        Field testMapField = TestClass.class.getDeclaredField("testMap");

        // Call the buildBlankMapJsonObject method with the 'testMap' field
        JSONObject jsonObject = utils.buildBlankMapJsonObject(testMapField);

        // Assert that the resulting JSON object has the correct structure
        Assertions.assertEquals("java.util.Map", jsonObject.getString("className"), "The className should be 'java.util.Map'");
        Assertions.assertEquals("java.lang.String", jsonObject.getString("valueType"), "The valueType should be 'String'");
    }

    @Test
    void testAddMap() throws Exception {
        JsonUtils utils = new JsonUtils();

        // Create a test class with a field that has the MapKeyType and MapValueType annotations


        // Get the 'testMap' field from the TestClass
        Field testMapField = TestClass.class.getDeclaredField("testMap");

        // Convert the Field to Parameter
        Parameter testMapParameter = TestClass.class.getDeclaredMethod("testAddMap", Map.class).getParameters()[0];

        // Create a new JSONObject
        JSONObject fieldJson = new JSONObject();

        // Call the addMap method with the 'testMap' parameter and fieldJson
        utils.addMap(testMapParameter, fieldJson);

        // Assert that the resulting JSON object has the correct structure
        Assertions.assertEquals("map", fieldJson.getString("fieldType"), "The fieldType should be 'map'");
        Assertions.assertEquals("create the key value pair and put in fields", fieldJson.getString("prompt"), "The prompt should be 'create the key value pair and put in fields'");
        Assertions.assertEquals("java.util.Map", fieldJson.getString("type"), "The type should be 'java.util.Map'");
        Assertions.assertEquals("java.lang.String", fieldJson.getString("keyType"), "The keyType should be 'java.lang.String'");
        Assertions.assertEquals("java.lang.Integer", fieldJson.getString("valueType"), "The valueType should be 'java.lang.Integer'");
    }
    @Test
    void testGetJsonObjectForList() throws Exception {
        JsonUtils utils = new JsonUtils();

        // Create a test class with a List field
        class TestClass {
            List<String> testList;
        }

        // Get the 'testList' field from the TestClass
        Field testListField = TestClass.class.getDeclaredField("testList");

        // Call the getJsonObjectForList method with the 'testList' field
        JSONObject jsonObject = utils.getJsonObjectForList(testListField.getType(), testListField.getName());

        // Assert that the resulting JSON object has the correct structure
        Assertions.assertEquals("java.util.List", jsonObject.getString("className"), "The className should be 'java.util.List'");
        Assertions.assertEquals("testList", jsonObject.getString("fieldName"), "The fieldName should be 'testList'");
        Assertions.assertEquals("java.util.List", jsonObject.getString("fieldType"), "The fieldType should be 'java.util.List'");
    }
    @Test
     void testConvertClassToJSONString() {
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
     void testPopulateClassFromJson() throws Exception {
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

   @Test
   void testPopulateObjectWithMap() throws Exception {
      JsonUtils utils = new JsonUtils();

      // Create a JSON string that represents a Map
      String jsonString = "{\n" +
              "    \"className\": \"java.util.Map\",\n" +
              "    \"fields\": [\n" +
              "        {\n" +
              "            \"key\": \"name\",\n" +
              "            \"value\": \"John Doe\"\n" +
              "        },\n" +
              "        {\n" +
              "            \"key\": \"age\",\n" +
              "            \"value\": \"30\"\n" +
              "        }\n" +
              "    ]\n" +
              "}";

      // Call the populateObject method
      Object result = utils.populateClassFromJson(jsonString);

      // Assert that the returned object is of the Map class
      Assertions.assertTrue(result instanceof Map);

      // Cast the result to Map and assert the key-value pairs
      Map<String, String> map = (Map<String, String>) result;
      Assertions.assertEquals("John Doe", map.get("name"));
      Assertions.assertEquals("30", map.get("age"));
   }


   @Test
   void testPopulateObjectWithIntArray() throws Exception {
      JsonUtils utils = new JsonUtils();

      // Create a JSON string that represents an object of the TestClass with an int array field
      String jsonString = "{\n" +
              "    \"className\": \"com.t4a.test.TestClass\",\n" +
              "    \"fields\": [\n" +
              "        {\n" +
              "            \"fieldName\": \"intArray\",\n" +
              "            \"fieldType\": \"int[]\",\n" +
              "            \"fieldValue\": [1, 2, 3]\n" +
              "        }\n" +
              "    ]\n" +
              "}";

      // Call the populateObject method
      Object result = utils.populateClassFromJson(jsonString);

      // Assert that the returned object is of the TestClass class
      Assertions.assertTrue(result instanceof TestClass);

      // Cast the result to TestClass and assert the int array field values
      TestClass testClass = (TestClass) result;
      Assertions.assertArrayEquals(new int[]{1, 2, 3}, testClass.getIntArray());
   }
   //Repeat similar tests for double[], long[], and boolean[] fields

   @Test
   void testPopulateObjectWithDoubleArray() throws Exception {
      JsonUtils utils = new JsonUtils();

      // Create a JSON string that represents an object of the TestClass with a double array field
      String jsonString = "{\n" +
              "    \"className\": \"com.t4a.test.TestClass\",\n" +
              "    \"fields\": [\n" +
              "        {\n" +
              "            \"fieldName\": \"doubleArray\",\n" +
              "            \"fieldType\": \"double[]\",\n" +
              "            \"fieldValue\": [1.1, 2.2, 3.3]\n" +
              "        }\n" +
              "    ]\n" +
              "}";

      // Call the populateObject method
      Object result = utils.populateClassFromJson(jsonString);

      // Assert that the returned object is of the TestClass class
      Assertions.assertTrue(result instanceof TestClass);

      // Cast the result to TestClass and assert the double array field values
      TestClass testClass = (TestClass) result;
      Assertions.assertArrayEquals(new double[]{1.1, 2.2, 3.3}, testClass.getDoubleArray(), 0.0001);
   }

   @Test
   void testPopulateObjectWithLongArray() throws Exception {
      JsonUtils utils = new JsonUtils();

      // Create a JSON string that represents an object of the TestClass with a long array field
      String jsonString = "{\n" +
              "    \"className\": \"com.t4a.test.TestClass\",\n" +
              "    \"fields\": [\n" +
              "        {\n" +
              "            \"fieldName\": \"longArray\",\n" +
              "            \"fieldType\": \"long[]\",\n" +
              "            \"fieldValue\": [100, 200, 300]\n" +
              "        }\n" +
              "    ]\n" +
              "}";

      // Call the populateObject method
      Object result = utils.populateClassFromJson(jsonString);

      // Assert that the returned object is of the TestClass class
      Assertions.assertTrue(result instanceof TestClass);

      // Cast the result to TestClass and assert the long array field values
      TestClass testClass = (TestClass) result;
      Assertions.assertArrayEquals(new long[]{100, 200, 300}, testClass.getLongArray());
   }

   @Test
   void testPopulateObjectWithBooleanArray() throws Exception {
      JsonUtils utils = new JsonUtils();

      // Create a JSON string that represents an object of the TestClass with a boolean array field
      String jsonString = "{\n" +
              "    \"className\": \"com.t4a.test.TestClass\",\n" +
              "    \"fields\": [\n" +
              "        {\n" +
              "            \"fieldName\": \"booleanArray\",\n" +
              "            \"fieldType\": \"boolean[]\",\n" +
              "            \"fieldValue\": [true, false, true]\n" +
              "        }\n" +
              "    ]\n" +
              "}";

      // Call the populateObject method
      Object result = utils.populateClassFromJson(jsonString);

      // Assert that the returned object is of the TestClass class
      Assertions.assertTrue(result instanceof TestClass);

      // Cast the result to TestClass and assert the boolean array field values
      TestClass testClass = (TestClass) result;
      Assertions.assertArrayEquals(new boolean[]{true, false, true}, testClass.getBooleanArray());
   }

    @Test
    void testPopulateObjectWithDateArray() throws Exception {
        JsonUtils utils = new JsonUtils();

        // Create a JSON string that represents an object of the TestClass with a Date array field
        String jsonString = "{\n" +
                "    \"className\": \"com.t4a.test.TestClass\",\n" +
                "    \"fields\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"dateArray\",\n" +
                "            \"fieldType\": \"Date[]\",\n" +
                "            \"fieldValue\": [\"2022-01-01\", \"2022-02-02\", \"2022-03-03\"],\n" +
                "            \"dateFormat\": \"yyyy-MM-dd\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        // Call the populateObject method
        Object result = utils.populateClassFromJson(jsonString);

        // Assert that the returned object is of the TestClass class
        Assertions.assertTrue(result instanceof TestClass);

        // Cast the result to TestClass and assert the Date array field values
        TestClass testClass = (TestClass) result;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date[] expectedDates = {format.parse("2022-01-01"), format.parse("2022-02-02"), format.parse("2022-03-03")};
        Assertions.assertArrayEquals(expectedDates, testClass.getDateArray());
    }

    @Test
    void testPopulateObjectWithList() throws Exception {
        JsonUtils utils = new JsonUtils();

        // Create a JSON string that represents an object of the TestClass with a List field
        String jsonString = "{\n" +
                "    \"className\": \"com.t4a.test.TestClass\",\n" +
                "    \"fields\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"testList\",\n" +
                "            \"fieldType\": \"list\",\n" +
                "            \"fieldValue\": [\"item1\", \"item2\", \"item3\"],\n" +
                "            \"className\": \"java.lang.String\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        // Call the populateObject method
        Object result = utils.populateClassFromJson(jsonString);

        // Assert that the returned object is of the TestClass class
        Assertions.assertTrue(result instanceof TestClass);

        // Cast the result to TestClass and assert the List field values
        TestClass testClass = (TestClass) result;
        Assertions.assertEquals(Arrays.asList("item1", "item2", "item3"), testClass.getTestList());
    }

    void testComplexList() throws Exception {
        String jsonStr = "{\n" +
                "    \"className\": \"com.t4a.examples.pojo.Organization\",\n" +
                "    \"fields\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"name\",\n" +
                "            \"fieldType\": \"String\",\n" +
                "            \"fieldValue\": \"Gulab Movies Inc\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"em\",\n" +
                "            \"className\": \"com.t4a.examples.pojo.Employee\",\n" +
                "            \"fieldType\": \"list\",\n" +
                "            \"prompt\": \"If you find more than 1 Employee add it as another object inside fieldValue\",\n" +
                "            \"fieldValue\": [\n" +
                "                {\n" +
                "                    \"fieldName\": \"name\",\n" +
                "                    \"fieldType\": \"String\",\n" +
                "                    \"fieldValue\": \"Amitabh Kapoor\",\n" +
                "                    \"department\": \"Actor\",\n" +
                "                    \"salary\": 1000000,\n" +
                "                    \"location\": \"Mumbai\",\n" +
                "                    \"dateJoined\": \"01-01-2020\",\n" +
                "                    \"tasks\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"name\",\n" +
                "                    \"fieldType\": \"String\",\n" +
                "                    \"fieldValue\": \"Anil Bacchan\",\n" +
                "                    \"department\": \"Director\",\n" +
                "                    \"salary\": 1500000,\n" +
                "                    \"location\": \"Bangalore\",\n" +
                "                    \"dateJoined\": \"15-01-2021\",\n" +
                "                    \"tasks\": []\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"locations\",\n" +
                "            \"description\": \"there could be multiple String\",\n" +
                "            \"className\": \"java.lang.String\",\n" +
                "            \"fieldType\": \"list\",\n" +
                "            \"fieldValue\": [\"Mumbai\", \"Bangalore\"]\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"customers\",\n" +
                "            \"isArray\": true,\n" +
                "            \"fieldType\": \"Customer[]\",\n" +
                "            \"fieldValue\": []\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JsonUtils utils = new JsonUtils();
        Organization org = (Organization) utils.populateClassFromJson(jsonStr);
        Assertions.assertEquals("Gulab Movies Inc", org.getName());
        Assertions.assertEquals(2, org.getEm().size());
        Assertions.assertEquals("Amitabh Kapoor", org.getEm().get(0).getName());
        Assertions.assertEquals("Anil Bacchan", org.getEm().get(1).getName());

    }

    @Test
    void testCreateJson() {
        JsonUtils utils = new JsonUtils();
        String result = "{\"fields\":[{\"fieldName\":\"key1\",\"fieldType\":\"String\",\"fieldValue\":\"\"},{\"fieldName\":\"key2\",\"fieldType\":\"String\",\"fieldValue\":\"\"},{\"fieldName\":\"key3\",\"fieldType\":\"String\",\"fieldValue\":\"\"}]}";
        // Test with multiple keys
        String jsonString = utils.createJson("key1", "key2", "key3");
        Assertions.assertEquals(result, jsonString, "The resulting JSON string should match the expected output");

    }

    @Test
    void testMapAnnotations() throws Exception {
        JsonUtils utils = new JsonUtils();

        // Create a class with Map field annotated with MapKeyType and MapValueType

        String expectedOutput = "{\"className\":\"java.util.List\",\"fields\":[{\"fieldValue\":\"\"}],\"type\":\"java.util.Map\",\"prompt\":\"put each value inside fieldValue\"}";
        // Call the method that uses the annotations
        JSONObject jsonObject = utils.buildBlankListJsonObject(TestClass.class.getDeclaredField("testMap"));
        Assertions.assertEquals(expectedOutput, jsonObject.toString());

    }

    @Test
    void testMapAnnotationsValues() throws Exception {
        JsonUtils utils = new JsonUtils();

        // Create a class with Map field annotated with MapKeyType and MapValueType


        // Call the method that uses the annotations
        JSONObject jsonObject = utils.buildBlankListJsonObject(TestClass.class.getDeclaredField("testCustomers"));

        // Assert that the keyType and valueType are correctly set in the JSON object
        Assertions.assertEquals("java.lang.String",jsonObject.getString("keyType"));
        Assertions.assertEquals("java.lang.Integer", jsonObject.getString("valueType"));
    }
    @Test
    void testProcessProperties() {
        JsonUtils utils = new JsonUtils();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode parentNode = mapper.createObjectNode();

        // Create a map of properties
        Map<String, Schema> properties = new HashMap<>();
        Schema schema1 = new Schema();
        schema1.setType("string");
        properties.put("property1", schema1);

        Schema schema2 = new Schema();
        schema2.setType("integer");
        schema2.setDefault("10");
        properties.put("property2", schema2);

        ObjectSchema schema3 = new ObjectSchema();
        Map<String, Schema> subProperties = new HashMap<>();
        Schema subSchema = new Schema();
        subSchema.setType("boolean");
        subProperties.put("subProperty", subSchema);
        schema3.setProperties(subProperties);
        properties.put("property3", schema3);

        // Call the method
        utils.processProperties(parentNode, properties);

        // Assert the properties of the parentNode
        Assertions.assertEquals("string", parentNode.get("property1").asText());
        Assertions.assertEquals("10", parentNode.get("property2").asText());
        Assertions.assertTrue(parentNode.get("property3").isObject());
        Assertions.assertEquals("boolean", parentNode.get("property3").get("subProperty").asText());
    }
}
