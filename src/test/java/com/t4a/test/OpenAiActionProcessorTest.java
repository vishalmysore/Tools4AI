package com.t4a.test;

import com.t4a.api.*;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.OpenAiActionProcessor;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenAiActionProcessorTest {
    private OpenAiActionProcessor processor;
    private AIAction mockAction;
    private HumanInLoop mockHumanInLoop;
    private ExplainDecision mockExplainDecision;
    private PredictionLoader mockPredictionLoader;
    private OpenAiChatModel mockOpenAiChatModel;
    private JavaMethodInvoker mockJavaMethodInvoker;
    private JavaMethodExecutor mockJavaMethodExecutor;

    @BeforeEach
    void setUp() throws InvocationTargetException, IllegalAccessException {
        processor = new OpenAiActionProcessor();
        mockAction = mock(AIAction.class);
        mockHumanInLoop = mock(HumanInLoop.class);
        mockExplainDecision = mock(ExplainDecision.class);
        mockPredictionLoader = mock(PredictionLoader.class);
        mockOpenAiChatModel = mock(OpenAiChatModel.class);
        mockJavaMethodInvoker = mock(JavaMethodInvoker.class);
        mockJavaMethodExecutor = mock(JavaMethodExecutor.class);
        try (MockedStatic<PredictionLoader> mocked = Mockito.mockStatic(PredictionLoader.class)) {
                mocked.when(PredictionLoader::getInstance).thenReturn(mockPredictionLoader);
          }
        // Mock the static method getPredictedAction

        when(mockPredictionLoader.getPredictedAction(anyString(), any(AIPlatform.class))).thenReturn(mockAction);
        when(mockPredictionLoader.getOpenAiChatModel()).thenReturn(mockOpenAiChatModel);
        when(mockAction.getActionRisk()).thenReturn(ActionRisk.LOW);
        when(mockAction.getActionType()).thenReturn(ActionType.JAVAMETHOD);
        when(mockAction.getActionName()).thenReturn("Test Action");

        when(mockJavaMethodInvoker.parse(anyString())).thenReturn(new Object[]{new Class<?>[0], new Object[0]});
        when(mockJavaMethodExecutor.action(anyString(), any(AIAction.class))).thenReturn("Test Result");
    }
    @Test
    void testQuery() {
        String promptText = "Test Prompt";

        try {
            String result = processor.query(promptText);
            assertNotNull(result, "Result should not be null");
            // Add more assertions based on your expected output
        } catch (AIProcessingException e) {
            fail("Exception should not be thrown");
        }
    }


    @Test
    void testProcessSingleAction() {
        String promptText = "Test Prompt";
        String json = "{\n" +
                "  \"methodName\": \"mockAction\",\n" +
                "  \"returnType\": \"String\",\n" +
                "  \"parameters\": [\n" +
                "    {\n" +
                "      \"type\": \"String\",\n" +
                "      \"name\": \"Key1\",\n" +
                "      \"fieldValue\": \"Val1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"com.t4a.test.Person\",\n" +
                "      \"name\": \"person\",\n" +
                "      \"fields\": [\n" +
                "        {\n" +
                "          \"fieldName\": \"name\",\n" +
                "          \"fieldType\": \"String\",\n" +
                "          \"fieldValue\": \"Vishal\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        try (MockedStatic<PredictionLoader> mocked = Mockito.mockStatic(PredictionLoader.class)) {
            mocked.when(PredictionLoader::getInstance).thenReturn(mockPredictionLoader);
            when(mockPredictionLoader.getOpenAiChatModel()).thenReturn(mockOpenAiChatModel);
            when(mockOpenAiChatModel.generate(anyString())).thenReturn(json);
//            when(mockJavaMethodInvoker.parse(anyString())).thenReturn(new Object[]{new Class<?>[0], new Object[0]});
            MockAction action = new MockAction();
            Object result = processor.processSingleAction(promptText, action);
            assertNotNull(result, "Result should not be null");
            assertEquals("mockAction", action.getActionName());
            assertEquals("Val1", action.name);
            assertEquals("Vishal", action.p.name);
                // Add more assertions based on your expected output
        } catch (AIProcessingException e) {
            e.printStackTrace();
            fail("Exception should not be thrown");
        }
    }
}