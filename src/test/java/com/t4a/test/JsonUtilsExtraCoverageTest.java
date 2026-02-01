package com.t4a.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.t4a.JsonUtils;
import com.t4a.annotations.ListType;
import com.t4a.annotations.MapKeyType;
import com.t4a.annotations.MapValueType;
import com.t4a.annotations.Prompt;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonUtilsExtraCoverageTest {

    public static class ComplexType {
        private String innerName;

        public String getInnerName() {
            return innerName;
        }

        public void setInnerName(String name) {
            this.innerName = name;
        }
    }

    public static class AllTypesClass {
        private float primitiveFloat;
        @Prompt(dateFormat = "yyyy/MM/dd")
        private Date dateField;
        private ComplexType complexField;
        @Prompt(ignore = true)
        private String ignoredField;

        public float getPrimitiveFloat() {
            return primitiveFloat;
        }

        public void setPrimitiveFloat(float f) {
            this.primitiveFloat = f;
        }

        public Date getDateField() {
            return dateField;
        }

        public void setDateField(Date dateField) {
            this.dateField = dateField;
        }

        public ComplexType getComplexField() {
            return complexField;
        }

        public void setComplexField(ComplexType complexField) {
            this.complexField = complexField;
        }
    }

    @Test
    void testPopulateFloatAndDate() throws Exception {
        JsonUtils utils = new JsonUtils();
        String json = "{" +
                "\"className\":\"com.t4a.test.JsonUtilsExtraCoverageTest$AllTypesClass\"," +
                "\"fields\":[" +
                "{\"fieldName\":\"primitiveFloat\", \"fieldType\":\"Float\", \"fieldValue\":10}," +
                "{\"fieldName\":\"dateField\", \"fieldType\":\"date\", \"fieldValue\":\"2023/10/10\", \"dateFormat\":\"yyyy/MM/dd\"}"
                +
                "]" +
                "}";
        AllTypesClass obj = (AllTypesClass) utils.populateClassFromJson(json);
        Assertions.assertEquals(10.0f, obj.getPrimitiveFloat());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Assertions.assertEquals(sdf.parse("2023/10/10"), obj.getDateField());
    }

    @Test
    void testProcessPropertiesWithNestedObject() {
        JsonUtils utils = new JsonUtils();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        Map<String, Schema> properties = new HashMap<>();
        ObjectSchema nestedSchema = new ObjectSchema();
        Map<String, Schema> nestedProps = new HashMap<>();
        Schema stringProp = new Schema();
        stringProp.setType("string");
        stringProp.setDefault("defaultValue");
        nestedProps.put("innerProp", stringProp);
        nestedSchema.setProperties(nestedProps);
        properties.put("outerProp", nestedSchema);

        utils.processProperties(root, properties);

        Assertions.assertTrue(root.has("outerProp"));
        Assertions.assertEquals("defaultValue", root.get("outerProp").get("innerProp").asText());
    }

    @Test
    void testConvertMethodToJsonWithVariousParams() throws Exception {
        JsonUtils utils = new JsonUtils();
        Method method = TestMethods.class.getMethod("sampleMethod", String.class, BigDecimal.class, int[].class,
                List.class, Map.class);
        String json = utils.convertMethodTOJsonString(method);

        Assertions.assertTrue(json.contains("sampleMethod"));
        Assertions.assertTrue(json.contains("java.math.BigDecimal"));
        Assertions.assertTrue(json.contains("int[]"));
    }

    public static class TestMethods {
        public void sampleMethod(String name, BigDecimal amount, int[] counts,
                @ListType(String.class) List<String> tags,
                @MapKeyType(String.class) @MapValueType(Integer.class) Map<String, Integer> scores) {
        }
    }

    @Test
    void testCreateJsonExceptions() {
        JsonUtils utils = new JsonUtils();
        Assertions.assertThrows(IllegalArgumentException.class, () -> utils.createJson((String[]) null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> utils.createJson());
    }

    @Test
    void testGetJsonObjectWithIgnoredField() {
        JsonUtils utils = new JsonUtils();
        JSONObject json = utils.getJsonObject(AllTypesClass.class);
        String jsonStr = json.toString();
        Assertions.assertFalse(jsonStr.contains("ignoredField"));
    }

    @Test
    void testFetchActionNameExceptions() {
        JsonUtils utils = new JsonUtils();
        String input = "not a json";
        Assertions.assertThrows(JSONException.class, () -> utils.fetchActionName(input));
    }

    @Test
    void testGetFieldValueFromMultipleFields() {
        JsonUtils utils = new JsonUtils();
        String json = "{\"fields\":[{\"fieldName\":\"testField\",\"fieldValue\":\"testValue\"}]}";
        String val = utils.getFieldValueFromMultipleFields(json, "testField");
        Assertions.assertEquals("testValue", val);

        String valNull = utils.getFieldValueFromMultipleFields(json, "nonExistent");
        Assertions.assertNull(valNull);
    }

    @Test
    void testGetFieldValueFallback() {
        JsonUtils utils = new JsonUtils();
        String json = "{\"fieldValue\":\"justValue\"}";
        Assertions.assertEquals("justValue", utils.getFieldValue(json, "any"));

        String input = "{\"key\":\"val\"}";
        // if fieldValue is missing, it returns the input
        Assertions.assertEquals(input, utils.getFieldValue(input, "any"));
    }
}
