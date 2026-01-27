package com.t4a.transform;

import com.t4a.processor.AIProcessingException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PromptTransformerMoreCoverageTest {

    public static class Sample {
        private String value;

        public Sample() {
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private static class TestTransformer implements PromptTransformer {
        @Override
        public String transformIntoJson(String jsonString, String promptText, String funName, String description)
                throws AIProcessingException {
            return "{\"className\":\"" + Sample.class.getName()
                    + "\", \"fields\":[{\"fieldName\":\"value\", \"fieldType\":\"String\", \"fieldValue\":\""
                    + promptText + "\"}]}";
        }

        @Override
        public String getJSONResponseFromAI(String prompt, String jsonStr) throws AIProcessingException {
            return "{\"className\":\"" + Sample.class.getName()
                    + "\", \"fields\":[{\"fieldName\":\"value\", \"fieldType\":\"String\", \"fieldValue\":\"" + prompt
                    + "\"}]}";
        }
    }

    @Test
    public void testDefaultMethods() throws AIProcessingException {
        TestTransformer transformer = new TestTransformer();

        String json = transformer.transformIntoJson("{}", "test prompt");
        assertTrue(json.contains("test prompt"));

        // transformIntoPojo uses JsonUtils and Class.forName, so it might fail if
        // Sample is not findable or complex
        // But let's try SamplePojo
        Object pojo = transformer.transformIntoPojo("val1", Sample.class);
        assertNotNull(pojo);
        assertTrue(pojo instanceof Sample);
        assertEquals("val1", ((Sample) pojo).getValue());
    }

    @Test
    public void testMapAndList() throws AIProcessingException {
        TestTransformer transformer = new TestTransformer() {
            @Override
            public String getJSONResponseFromAI(String prompt, String jsonStr) throws AIProcessingException {
                return "{\"className\":\"java.util.Map\", \"fields\":[{\"key\":\"key\", \"value\":\"" + prompt
                        + "\"}]}";
            }
        };

        Object map = transformer.transformIntoPojo("valX", "java.util.Map");
        assertNotNull(map);
        assertTrue(map instanceof Map);
        assertEquals("valX", ((Map) map).get("key"));
    }

    @Test
    public void testGeminiV2TransformerGetters() {
        GeminiV2PromptTransformer transformer = new GeminiV2PromptTransformer();
        assertNotNull(transformer.getGson());

        GeminiV2PromptTransformer transformer2 = new GeminiV2PromptTransformer(null);
        assertNull(transformer2.getGson());
    }
}
