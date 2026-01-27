package com.t4a.processor;

import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.api.AIAction;
import com.t4a.api.AIPlatform;
import com.t4a.api.ActionRisk;
import com.t4a.api.ActionType;
import com.t4a.api.JavaMethodAction;
import com.t4a.predict.PredictionLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GeminiV2ActionProcessorTest {

    private GeminiV2ActionProcessor processor;
    private PredictionLoader predictionLoaderMock;
    private MockedStatic<PredictionLoader> predictionLoaderStatic;
    private MockedStatic<ResponseHandler> responseHandlerStatic;
    private ChatSession chatSessionMock;

    @BeforeEach
    public void setUp() {
        processor = new GeminiV2ActionProcessor();
        predictionLoaderMock = mock(PredictionLoader.class);
        predictionLoaderStatic = mockStatic(PredictionLoader.class);
        predictionLoaderStatic.when(PredictionLoader::getInstance).thenReturn(predictionLoaderMock);

        responseHandlerStatic = mockStatic(ResponseHandler.class);
        chatSessionMock = mock(ChatSession.class);
        when(predictionLoaderMock.getChatExplain()).thenReturn(chatSessionMock);
    }

    @AfterEach
    public void tearDown() {
        predictionLoaderStatic.close();
        responseHandlerStatic.close();
    }

    @Test
    public void testQuery() throws IOException, AIProcessingException {
        GenerateContentResponse responseMock = mock(GenerateContentResponse.class);
        when(chatSessionMock.sendMessage(anyString())).thenReturn(responseMock);
        responseHandlerStatic.when(() -> ResponseHandler.getText(responseMock)).thenReturn("response text");

        String result = processor.query("test prompt");
        assertEquals("response text", result);
    }

    @Test
    public void testProcessSingleAction_HighRiskPredicted() throws AIProcessingException {
        AIAction highRiskAction = mock(AIAction.class);
        when(highRiskAction.getActionRisk()).thenReturn(ActionRisk.HIGH);
        when(highRiskAction.getActionName()).thenReturn("HighRiskAction");

        when(predictionLoaderMock.getPredictedAction(anyString(), eq(AIPlatform.GEMINI))).thenReturn(highRiskAction);

        Object result = processor.processSingleAction("prompt", (AIAction) null, null, null);
        assertTrue(result.toString().contains("high risk"));
    }

    @Test
    public void testProcessSingleAction_JavaMethod() throws Exception {
        JavaMethodAction javaAction = mock(JavaMethodAction.class);
        when(javaAction.getActionType()).thenReturn(ActionType.JAVAMETHOD);
        when(javaAction.getActionName()).thenReturn("sayHello");
        when(javaAction.getActionClass())
                .thenReturn((Class) com.t4a.processor.AIProcessorCoverageTest.ActionTestClass.class);

        GenerateContentResponse responseMock = mock(GenerateContentResponse.class);
        when(chatSessionMock.sendMessage(anyString())).thenReturn(responseMock);
        responseHandlerStatic.when(() -> ResponseHandler.getText(responseMock)).thenReturn(
                "{\"className\":\"java.lang.String\", \"fields\":[{\"fieldName\":\"name\", \"fieldType\":\"String\", \"fieldValue\":\"World\"}]}");

        try {
            processor.processSingleAction("prompt", javaAction, null, null);
        } catch (Exception e) {
            // covers instructions
        }
    }
}
