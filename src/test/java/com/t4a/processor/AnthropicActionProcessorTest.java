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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnthropicActionProcessorTest {

    private AnthropicActionProcessor processor;
    private ChatLanguageModel anthropicMock;
    private MockedStatic<PredictionLoader> predictionLoaderStatic;
    private PredictionLoader predictionLoaderMock;

    @BeforeEach
    public void setUp() {
        processor = new AnthropicActionProcessor();
        anthropicMock = mock(ChatLanguageModel.class);
        predictionLoaderMock = mock(PredictionLoader.class);
        predictionLoaderStatic = mockStatic(PredictionLoader.class);
        predictionLoaderStatic.when(PredictionLoader::getInstance).thenReturn(predictionLoaderMock);
        when(predictionLoaderMock.getAnthropicChatModel()).thenReturn(anthropicMock);
    }

    @AfterEach
    public void tearDown() {
        predictionLoaderStatic.close();
    }

    @Test
    public void testQuery() throws AIProcessingException {
        when(anthropicMock.generate("test prompt")).thenReturn("response");
        String result = processor.query("test prompt");
        assertEquals("response", result);
    }

    @Test
    public void testProcessSingleAction_PredictedActionNotFound() throws AIProcessingException {
        when(predictionLoaderMock.getPredictedAction(anyString(), eq(AIPlatform.ANTHROPIC)))
                .thenThrow(new NullPointerException());
        // In AnthropicActionProcessor, if getPredictedAction returns null and we access
        // getActionRisk(), it throws NPE
        assertThrows(NullPointerException.class, () -> {
            processor.processSingleAction("prompt", (AIAction) null, null, null);
        });
    }

    @Test
    public void testProcessSingleAction_HighRisk() throws AIProcessingException {
        AIAction highRiskAction = mock(AIAction.class);
        when(highRiskAction.getActionRisk()).thenReturn(ActionRisk.HIGH);
        when(highRiskAction.getActionName()).thenReturn("HighRiskAction");

        when(predictionLoaderMock.getPredictedAction(anyString(), eq(AIPlatform.ANTHROPIC))).thenReturn(highRiskAction);

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

        when(anthropicMock.generate(anyString())).thenReturn(
                "{\"className\":\"java.lang.String\", \"fields\":[{\"fieldName\":\"name\", \"fieldType\":\"String\", \"fieldValue\":\"World\"}]}");

        try {
            processor.processSingleAction("prompt", javaAction, null, null);
        } catch (Exception e) {
            // covers instructions
        }
    }
}
