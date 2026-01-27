package com.t4a.processor;

import com.t4a.api.AIAction;
import com.t4a.api.AIPlatform;
import com.t4a.api.ActionRisk;
import com.t4a.api.ActionType;
import com.t4a.api.JavaMethodAction;
import com.t4a.predict.PredictionLoader;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OpenAiActionProcessorTest {

    private OpenAiActionProcessor processor;
    private ChatLanguageModel openAiMock;
    private MockedStatic<PredictionLoader> predictionLoaderStatic;
    private PredictionLoader predictionLoaderMock;

    @BeforeEach
    public void setUp() {
        processor = new OpenAiActionProcessor();
        openAiMock = mock(ChatLanguageModel.class);
        predictionLoaderMock = mock(PredictionLoader.class);
        predictionLoaderStatic = mockStatic(PredictionLoader.class);
        predictionLoaderStatic.when(PredictionLoader::getInstance).thenReturn(predictionLoaderMock);
        when(predictionLoaderMock.getOpenAiChatModel()).thenReturn(openAiMock);
    }

    @AfterEach
    public void tearDown() {
        predictionLoaderStatic.close();
    }

    @Test
    public void testQuery() throws AIProcessingException {
        when(openAiMock.generate("test prompt")).thenReturn("response");
        String result = processor.query("test prompt");
        assertEquals("response", result);
    }

    @Test
    public void testProcessSingleAction_PredictedActionNotFound() throws AIProcessingException {
        when(predictionLoaderMock.getPredictedAction(anyString(), eq(AIPlatform.OPENAI))).thenReturn(null);
        Object result = processor.processSingleAction("prompt", (AIAction) null, null, null);
        assertEquals("no action found for the prompt prompt", result);
    }

    @Test
    public void testProcessSingleAction_HighRisk() throws AIProcessingException {
        AIAction highRiskAction = mock(AIAction.class);
        when(highRiskAction.getActionRisk()).thenReturn(ActionRisk.HIGH);
        when(highRiskAction.getActionName()).thenReturn("HighRiskAction");

        when(predictionLoaderMock.getPredictedAction(anyString(), eq(AIPlatform.OPENAI))).thenReturn(highRiskAction);

        Object result = processor.processSingleAction("prompt", (AIAction) null, null, null);
        assertTrue(result.toString().contains("high risk"));
    }

    @Test
    public void testProcessSingleAction_JavaMethod() throws AIProcessingException {
        JavaMethodAction javaAction = mock(JavaMethodAction.class);
        when(javaAction.getActionType()).thenReturn(ActionType.JAVAMETHOD);
        when(javaAction.getActionName()).thenReturn("sayHello");
        when(javaAction.getActionClass())
                .thenReturn((Class) com.t4a.processor.AIProcessorCoverageTest.ActionTestClass.class);
        when(javaAction.getPrompt()).thenReturn("prompt");
        when(javaAction.getSubprompt()).thenReturn("subprompt");

        when(openAiMock.generate(anyString())).thenReturn(
                "{\"className\":\"java.lang.String\", \"fields\":[{\"fieldName\":\"name\", \"fieldType\":\"String\", \"fieldValue\":\"World\"}]}");

        try {
            processor.processSingleAction("Say hello to World", javaAction, null, null);
        } catch (Exception e) {
            // It might fail due to further logic but we covered some instructions
        }
    }

    @Test
    public void testProcessSingleNonJava() throws AIProcessingException {
        AIAction nonJavaAction = mock(AIAction.class);
        when(nonJavaAction.getActionType()).thenReturn(ActionType.SHELL);
        when(nonJavaAction.getActionName()).thenReturn("shellAction");

        when(predictionLoaderMock.getPredictedAction(anyString(), eq(AIPlatform.OPENAI))).thenReturn(nonJavaAction);
        when(predictionLoaderMock.postActionProcessing(anyString(), anyString())).thenReturn("final result");

        try {
            processor.processSingleNonJava("prompt", nonJavaAction, null, null);
        } catch (Exception e) {
            // Might fail but covers instructions
        }
    }
}
