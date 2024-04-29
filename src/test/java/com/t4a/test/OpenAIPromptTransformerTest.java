package com.t4a.test;

import com.t4a.examples.basic.RestaurantPojo;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.transform.OpenAIPromptTransformer;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class OpenAIPromptTransformerTest {

    String jsonStr = "{\n" +
            "    \"className\": \"com.t4a.examples.basic.RestaurantPojo\",\n" +
            "    \"fields\": [\n" +
            "        {\n" +
            "            \"fieldName\": \"name\",\n" +
            "            \"fieldType\": \"String\",\n" +
            "            \"fieldValue\": \"Vishal\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"fieldName\": \"numberOfPeople\",\n" +
            "            \"fieldType\": \"int\",\n" +
            "            \"fieldValue\": 4\n" +
            "        },\n" +
            "        {\n" +
            "            \"fieldName\": \"restaurantDetails\",\n" +
            "            \"className\": \"com.t4a.examples.basic.RestaurantDetails\",\n" +
            "            \"fields\": [\n" +
            "                {\n" +
            "                    \"fieldName\": \"name\",\n" +
            "                    \"fieldType\": \"String\",\n" +
            "                    \"fieldValue\": \"Maharaj\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"fieldName\": \"location\",\n" +
            "                    \"fieldType\": \"String\",\n" +
            "                    \"fieldValue\": \"Toronto\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"fieldType\": \"com.t4a.examples.basic.RestaurantDetails\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"fieldName\": \"cancel\",\n" +
            "            \"fieldType\": \"boolean\",\n" +
            "            \"fieldValue\": true\n" +
            "        },\n" +
            "        {\n" +
            "            \"fieldName\": \"reserveDate\",\n" +
            "            \"fieldType\": \"String\",\n" +
            "            \"fieldValue\": \"Indian Independence day\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    @Test
    void testTransform() {

        try (MockedStatic<PredictionLoader> responseHandlerMock = Mockito.mockStatic(PredictionLoader.class)) {

            PredictionLoader mockGRP = Mockito.mock(PredictionLoader.class);
            responseHandlerMock.when(() -> PredictionLoader.getInstance()).thenReturn(mockGRP);
            OpenAiChatModel mockOpenAiChatModel = Mockito.mock(OpenAiChatModel.class);
            Mockito.when(mockOpenAiChatModel.generate(Mockito.anyString())).thenReturn("{'response':'success'}");
            Mockito.when(mockGRP.getOpenAiChatModel()).thenReturn(mockOpenAiChatModel);
            OpenAIPromptTransformer transformer = new OpenAIPromptTransformer();
            String json = transformer.getJSONResponseFromAI("My name is vishal and I am from India. I love vegetarian food","{}");
            Assertions.assertEquals("{'response':'success'}", json);
        }
    }

    @Test
    void testTransformPojo() {

        try (MockedStatic<PredictionLoader> responseHandlerMock = Mockito.mockStatic(PredictionLoader.class)) {

            PredictionLoader mockGRP = Mockito.mock(PredictionLoader.class);
            responseHandlerMock.when(() -> PredictionLoader.getInstance()).thenReturn(mockGRP);
            OpenAiChatModel mockOpenAiChatModel = Mockito.mock(OpenAiChatModel.class);
            Mockito.when(mockOpenAiChatModel.generate(Mockito.anyString())).thenReturn("{'response':'success'}");
            Mockito.when(mockGRP.getOpenAiChatModel()).thenReturn(mockOpenAiChatModel);
            OpenAIPromptTransformer transformer = new OpenAIPromptTransformer() {
                @Override
                public String getJSONResponseFromAI(String prompt, String jsonStr1) {
                    return jsonStr;
                }

            };

            RestaurantPojo pojo =(RestaurantPojo) transformer.transformIntoPojo("My Name is Vishal, I like Bollywood movies", RestaurantPojo.class);
            Assertions.assertEquals("Vishal", pojo.getName());
        } catch (AIProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
