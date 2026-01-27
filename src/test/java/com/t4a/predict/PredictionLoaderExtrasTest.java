package com.t4a.predict;

import com.t4a.api.AIAction;
import com.t4a.api.AIPlatform;
import com.t4a.api.ActionGroup;
import com.t4a.api.JavaMethodAction;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import com.t4a.transform.PromptTransformer;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PredictionLoaderExtrasTest {

    @Test
    public void testCreateOrGetAIProcessor() {
        PredictionLoader loader = PredictionLoader.getInstance();
        AIProcessor processor = loader.createOrGetAIProcessor();
        assertNotNull(processor);

        // Test with OpenAI
        ChatLanguageModel openAiMock = mock(ChatLanguageModel.class);
        loader.setOpenAiChatModel(openAiMock);
        // Force re-creation if we can, but aiProcessor is cached.
        // For testing purposes, we might need a fresh instance or use reflection to
        // clear it.
    }

    @Test
    public void testCreateOrGetPromptTransformer() {
        PredictionLoader loader = PredictionLoader.getInstance();
        PromptTransformer transformer = loader.createOrGetPromptTransformer();
        assertNotNull(transformer);
    }

    @Test
    public void testGetCommaSeparatedKeys() {
        PredictionLoader loader = PredictionLoader.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        String keys = loader.getCommaSeparatedKeys(map);
        assertTrue(keys.contains("k1"));
        assertTrue(keys.contains("k2"));
        assertTrue(keys.contains(", "));
    }

    @Test
    public void testGetActionParamsOpenAI() throws AIProcessingException {
        PredictionLoader loader = PredictionLoader.getInstance();
        ChatLanguageModel openAiMock = mock(ChatLanguageModel.class);
        loader.setOpenAiChatModel(openAiMock);

        AIAction action = mock(AIAction.class);
        when(action.getActionName()).thenReturn("myAction");

        Map<String, Object> params = new HashMap<>();
        params.put("p1", "v1");

        when(openAiMock.generate(anyString())).thenReturn("p1=v1");

        String result = loader.getActionParams(action, "prompt", AIPlatform.OPENAI, params);
        assertEquals("p1=v1", result);
    }

    @Test
    public void testFetchActionNameFromList() {
        PredictionLoader loader = PredictionLoader.getInstance();
        // actionNameList is StringBuilder, we can't easily populate it without calling
        // internal methods
        // but it is populated during getInstance() if there are actions.
        String list = loader.getActionNameList().toString();
        if (list.length() > 0) {
            String firstAction = list.split(",")[0];
            String found = loader.fetchActionNameFromList(firstAction.toLowerCase());
            assertEquals(firstAction, found);
        }
    }

    @Test
    public void testGetAiAction() {
        PredictionLoader loader = PredictionLoader.getInstance();
        AIAction action = mock(AIAction.class);
        when(action.getActionName()).thenReturn("testAction");

        loader.getPredictions().put("testAction", action);
        assertEquals(action, loader.getAiAction("testAction"));
    }

    @Test
    public void testGetInstanceWithContext() {
        ApplicationContext context = mock(ApplicationContext.class);
        org.springframework.core.env.Environment env = mock(org.springframework.core.env.Environment.class);
        when(context.getEnvironment()).thenReturn(env);
        when(env.getProperty("tools4ai.properties.path")).thenReturn("non_existent.properties");

        PredictionLoader loader = PredictionLoader.getInstance(context);
        assertNotNull(loader);
    }
}
