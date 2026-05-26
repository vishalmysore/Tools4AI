package com.t4a.agent.feedback;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToolResultFeedbackProcessorTest {

    /** Records the last query sent to it and returns a scripted response. */
    static class CapturingProcessor implements AIProcessor {
        String lastActionPrompt;
        String lastQueryPrompt;
        Object actionResult   = "raw tool result";
        String synthesisReply = "synthesised natural-language answer";
        boolean queryThrows   = false;

        @Override
        public String query(String p) throws AIProcessingException {
            lastQueryPrompt = p;
            if (queryThrows) throw new AIProcessingException("synthesis failed");
            return synthesisReply;
        }

        @Override
        public Object processSingleAction(String p) throws AIProcessingException {
            lastActionPrompt = p;
            return actionResult;
        }

        @Override public Object processSingleAction(String p, ActionCallback cb) throws AIProcessingException { return actionResult; }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e) throws AIProcessingException { return actionResult; }
        @Override public Object processSingleAction(String p, HumanInLoop h, ExplainDecision e) throws AIProcessingException { return actionResult; }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e, ActionCallback cb) throws AIProcessingException { return actionResult; }
    }

    private CapturingProcessor delegate;
    private ToolResultFeedbackProcessor processor;

    @BeforeEach
    void setUp() {
        delegate  = new CapturingProcessor();
        processor = new ToolResultFeedbackProcessor(delegate);
    }

    @Test
    void testProcessSingleAction_returnsSynthesisNotRaw() throws AIProcessingException {
        Object result = processor.processSingleAction("What is the weather in Toronto?");
        // Should be the synthesis reply, not the raw tool result
        assertEquals("synthesised natural-language answer", result);
    }

    @Test
    void testProcessSingleAction_queryContainsOriginalPrompt() throws AIProcessingException {
        processor.processSingleAction("How is the weather?");
        // The synthesis query should carry both the original prompt and the raw result
        assertNotNull(delegate.lastQueryPrompt);
        assertTrue(delegate.lastQueryPrompt.contains("How is the weather?"));
        assertTrue(delegate.lastQueryPrompt.contains("raw tool result"));
    }

    @Test
    void testProcessSingleAction_synthesisFails_fallsBackToRaw() throws AIProcessingException {
        delegate.queryThrows = true;
        Object result = processor.processSingleAction("some prompt");
        // Falls back to raw result
        assertEquals("raw tool result", result);
    }

    @Test
    void testProcessSingleAction_nullResult_returnsNull() throws AIProcessingException {
        delegate.actionResult = null;
        Object result = processor.processSingleAction("prompt");
        assertNull(result);
    }

    @Test
    void testQuery_delegatesDirectly() throws AIProcessingException {
        String result = processor.query("plain question");
        assertEquals("synthesised natural-language answer", result);
        assertEquals("plain question", delegate.lastQueryPrompt);
    }

    @Test
    void testNullDelegateThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ToolResultFeedbackProcessor(null));
    }

    @Test
    void testProcessSingleAction_withCallback() throws AIProcessingException {
        Object result = processor.processSingleAction("prompt", (ActionCallback) null);
        assertEquals("synthesised natural-language answer", result);
    }

    @Test
    void testProcessSingleAction_withHumanInLoop() throws AIProcessingException {
        Object result = processor.processSingleAction("prompt", (HumanInLoop) null, (ExplainDecision) null);
        assertEquals("synthesised natural-language answer", result);
    }
}
