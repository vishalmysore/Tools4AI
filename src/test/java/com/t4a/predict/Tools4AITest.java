package com.t4a.predict;

import com.t4a.api.AIAction;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.processor.AIProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class Tools4AITest {

    private MockedStatic<PredictionLoader> predictionLoaderStatic;
    private PredictionLoader predictionLoaderMock;

    @BeforeEach
    public void setUp() {
        predictionLoaderMock = mock(PredictionLoader.class);
        predictionLoaderStatic = mockStatic(PredictionLoader.class);
        predictionLoaderStatic.when(PredictionLoader::getInstance).thenReturn(predictionLoaderMock);
    }

    @AfterEach
    public void tearDown() {
        predictionLoaderStatic.close();
    }

    @Test
    public void testGetActionListAsJSONRPC() {
        Map<String, AIAction> predictions = new HashMap<>();
        AIAction actionMock = mock(AIAction.class);
        when(actionMock.getJsonRPC()).thenReturn("{\"name\":\"testAction\"}");
        predictions.put("testAction", actionMock);

        when(predictionLoaderMock.getPredictions()).thenReturn(predictions);

        String json = Tools4AI.getActionListAsJSONRPC();
        assertTrue(json.contains("testAction"));
    }

    @Test
    public void testExecuteAction_NotFound() throws AIProcessingException {
        when(predictionLoaderMock.getPredictions()).thenReturn(new HashMap<>());
        Object result = Tools4AI.executeAction("nonExistent", "{}");
        assertEquals("Action not found", result);
    }

    @Test
    public void testExecuteAction_Success() throws Exception {
        Map<String, AIAction> predictions = new HashMap<>();
        GenericJavaMethodAction actionMock = mock(GenericJavaMethodAction.class);
        when(actionMock.getActionClass())
                .thenReturn((Class) com.t4a.processor.AIProcessorCoverageTest.ActionTestClass.class);

        com.t4a.processor.AIProcessorCoverageTest.ActionTestClass instance = new com.t4a.processor.AIProcessorCoverageTest.ActionTestClass();
        when(actionMock.getActionInstance()).thenReturn(instance);

        predictions.put("sayHello", actionMock);
        when(predictionLoaderMock.getPredictions()).thenReturn(predictions);

        // jsonStr for JavaMethodInvoker.parse
        String jsonStr = "{" +
                "\"methodName\":\"sayHello\"," +
                "\"returnType\":\"java.lang.String\"," +
                "\"parameters\":[" +
                "{\"name\":\"name\", \"type\":\"java.lang.String\", \"fieldValue\":\"World\"}" +
                "]" +
                "}";

        Object result = Tools4AI.executeAction("sayHello", jsonStr);
        assertEquals("Hello World", result);
    }
}
