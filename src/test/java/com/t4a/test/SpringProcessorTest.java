package com.t4a.test;

import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.examples.SingletonResetter;
import com.t4a.examples.actions.ComplexAction;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AnthropicActionProcessor;
import com.t4a.processor.GeminiV2ActionProcessor;
import com.t4a.processor.OpenAiActionProcessor;
import com.t4a.processor.spring.SpringAnthropicProcessor;
import com.t4a.processor.spring.SpringGeminiProcessor;
import com.t4a.processor.spring.SpringOpenAIProcessor;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
public class SpringProcessorTest {

    String json = "{\n" +
            "    \"methodName\": \"computerRepair\",\n" +
            "    \"parameters\": [{\n" +
            "        \"name\": \"customer\",\n" +
            "        \"fields\": [\n" +
            "            {\n" +
            "                \"fieldName\": \"firstName\",\n" +
            "                \"fieldType\": \"String\",\n" +
            "                \"fieldValue\": \"vishal\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"fieldName\": \"lastName\",\n" +
            "                \"fieldType\": \"String\",\n" +
            "                \"fieldValue\": \"mysore\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"fieldName\": \"reasonForCalling\",\n" +
            "                \"fieldDescription\": \"convert this to Hindi\",\n" +
            "                \"fieldType\": \"String\",\n" +
            "                \"fieldValue\": \"phone is not working\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"fieldName\": \"dateJoined\",\n" +
            "                \"dateFormat\": \"yyyy-MM-dd\",\n" +
            "                \"fieldDescription\": \"if you dont find date provide todays date in fieldValue\",\n" +
            "                \"fieldType\": \"Date\",\n" +
            "                \"fieldValue\": \"2017-01-07\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"type\": \"com.t4a.examples.actions.Customer\"\n" +
            "    }],\n" +
            "    \"returnType\": \"String\"\n" +
            "}";
    @Test
    void testContext() throws Exception {
        SingletonResetter.resetSingleton(PredictionLoader.class);
        ComplexAction.COUNTER =0;
         ChatLanguageModel antropicModel = Mockito.mock(ChatLanguageModel.class);
         PredictionLoader.getInstance().setAnthropicChatModel(antropicModel);
         Mockito.when(antropicModel.generate(anyString())).thenReturn(json);
         ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
         AnthropicActionProcessor processor = new AnthropicActionProcessor();
         processor.processSingleAction("test","computerRepair");
        Assertions.assertEquals(2,ComplexAction.COUNTER);
    }
    @Test
    void testContextSpring() throws AIProcessingException {
        ComplexAction.COUNTER =0;
        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        SpringAnthropicProcessor processor = new SpringAnthropicProcessor(context);
        ChatLanguageModel antropicModel = Mockito.mock(ChatLanguageModel.class);
        PredictionLoader.getInstance().setAnthropicChatModel(antropicModel);
        Mockito.when(antropicModel.generate(anyString())).thenReturn(json);
        processor.processSingleAction("test","computerRepair");
        Assertions.assertEquals(1,ComplexAction.COUNTER);
    }

    @Test
    void testContextOI() throws Exception {
        SingletonResetter.resetSingleton(PredictionLoader.class);
        ComplexAction.COUNTER =0;
        ChatLanguageModel antropicModel = Mockito.mock(ChatLanguageModel.class);
        PredictionLoader.getInstance().setOpenAiChatModel(antropicModel);
        Mockito.when(antropicModel.generate(anyString())).thenReturn(json);
        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        processor.processSingleAction("test","computerRepair");
        Assertions.assertEquals(2,ComplexAction.COUNTER);
    }
    @Test
    void testContextSpringOI() throws AIProcessingException {
        ComplexAction.COUNTER =0;
        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        SpringOpenAIProcessor processor = new SpringOpenAIProcessor(context);
        ChatLanguageModel antropicModel = Mockito.mock(ChatLanguageModel.class);
        PredictionLoader.getInstance().setOpenAiChatModel(antropicModel);
        Mockito.when(antropicModel.generate(anyString())).thenReturn(json);
        processor.processSingleAction("test","computerRepair");
        Assertions.assertEquals(1,ComplexAction.COUNTER);
    }


    @Test
    void testContextGem() throws Exception {
        SingletonResetter.resetSingleton(PredictionLoader.class);
        ComplexAction.COUNTER =0;
        ChatSession antropicModel = Mockito.mock(ChatSession.class);
        ResponseHandler responseHandler = Mockito.mock(ResponseHandler.class);

        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        PredictionLoader.getInstance().setChat(antropicModel);
        PredictionLoader.getInstance().setChatGroupFinder(antropicModel);
        PredictionLoader.getInstance().setChatExplain(antropicModel);
        Mockito.when(antropicModel.sendMessage(anyString())).thenReturn(response);
        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        GeminiV2ActionProcessor processor = new GeminiV2ActionProcessor();
        String mockGRPSTR = "{'groupName':'No Group','explanation':'mock'}";
        try (MockedStatic<ResponseHandler> geminiMock = Mockito.mockStatic(ResponseHandler.class)) {
            geminiMock.when(() -> ResponseHandler.getText(any(GenerateContentResponse.class))).thenReturn(json);
            processor.processSingleAction("test", "computerRepair");
            Assertions.assertEquals(2, ComplexAction.COUNTER);
        }
    }
    @Test
    void testContextGemSpring() throws AIProcessingException, IOException {
        ComplexAction.COUNTER =0;
        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        SpringGeminiProcessor processor = new SpringGeminiProcessor(context);
        ChatSession antropicModel = Mockito.mock(ChatSession.class);
        ResponseHandler responseHandler = Mockito.mock(ResponseHandler.class);

        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        PredictionLoader.getInstance().setChat(antropicModel);
        PredictionLoader.getInstance().setChatGroupFinder(antropicModel);
        PredictionLoader.getInstance().setChatExplain(antropicModel);
        Mockito.when(antropicModel.sendMessage(anyString())).thenReturn(response);

        String mockGRPSTR = "{'groupName':'No Group','explanation':'mock'}";
        try (MockedStatic<ResponseHandler> geminiMock = Mockito.mockStatic(ResponseHandler.class)) {
            geminiMock.when(() -> ResponseHandler.getText(any(GenerateContentResponse.class))).thenReturn(json);
            processor.processSingleAction("test", "computerRepair");
            Assertions.assertEquals(1, ComplexAction.COUNTER);
        }
    }
}
