package com.t4a.processor;

import com.t4a.detect.ActionCallback;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProcessorMiscTest {

    @Test
    public void testActionContext() {
        ActionCallback callback = mock(ActionCallback.class);
        AIProcessor processor = mock(AIProcessor.class);

        ActionContext.setCallback(callback);
        ActionContext.setProcessor(processor);

        assertEquals(callback, ActionContext.getCallback());
        assertEquals(processor, ActionContext.getProcessor());

        ActionContext.clear();

        assertNull(ActionContext.getCallback());
        assertNull(ActionContext.getProcessor());
    }

    @Test
    public void testLocalAIActionProcessor() {
        LocalAIActionProcessor processor = new LocalAIActionProcessor();
        assertTrue(processor instanceof OpenAiActionProcessor);
    }

    @Test
    public void testLoggingHumanDecision() {
        LoggingHumanDecision decision = new LoggingHumanDecision();
        assertTrue(decision.allow("test", "action", "params").isAIResponseValid());
    }

    @Test
    public void testLogginggExplainDecision() {
        LogginggExplainDecision decision = new LogginggExplainDecision();
        String result = decision.explain("test", "action", "reason");
        assertEquals("reason", result);
    }

    @Test
    public void testMimeTypeResolver() {
        // Just covering simple cases or mock if needed
    }
}
