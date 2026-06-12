package com.t4a.agent.resilience;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RetryActionProcessorTest {

    /** Fails the first {@code failuresBeforeSuccess} calls, then returns a scripted result. */
    static class FlakyProcessor implements AIProcessor {
        int failuresBeforeSuccess;
        int calls = 0;
        Object result = "ok";

        FlakyProcessor(int failuresBeforeSuccess) {
            this.failuresBeforeSuccess = failuresBeforeSuccess;
        }

        private Object attempt() throws AIProcessingException {
            calls++;
            if (calls <= failuresBeforeSuccess) throw new AIProcessingException("transient failure " + calls);
            return result;
        }

        @Override public Object processSingleAction(String p) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, ActionCallback cb) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, HumanInLoop h, ExplainDecision e) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e, ActionCallback cb) throws AIProcessingException { return attempt(); }
        @Override public String query(String p) throws AIProcessingException { return (String) attempt(); }
    }

    @Test
    void testSucceedsFirstTry_noRetry() throws AIProcessingException {
        FlakyProcessor delegate = new FlakyProcessor(0);
        RetryActionProcessor retry = new RetryActionProcessor(delegate, 3, 0, null);
        assertEquals("ok", retry.processSingleAction("prompt"));
        assertEquals(1, delegate.calls);
    }

    @Test
    void testRecoversAfterTransientFailures() throws AIProcessingException {
        FlakyProcessor delegate = new FlakyProcessor(2);
        RetryActionProcessor retry = new RetryActionProcessor(delegate, 3, 0, null);
        assertEquals("ok", retry.processSingleAction("prompt"));
        assertEquals(3, delegate.calls);
    }

    @Test
    void testExhaustedRetries_throwsLastFailure() {
        FlakyProcessor delegate = new FlakyProcessor(10);
        RetryActionProcessor retry = new RetryActionProcessor(delegate, 3, 0, null);
        AIProcessingException ex = assertThrows(AIProcessingException.class,
                () -> retry.processSingleAction("prompt"));
        assertTrue(ex.getMessage().contains("transient failure 3"));
        assertEquals(3, delegate.calls);
    }

    @Test
    void testFallbackUsedAfterExhaustion() throws AIProcessingException {
        FlakyProcessor delegate = new FlakyProcessor(10);
        FlakyProcessor fallback = new FlakyProcessor(0);
        fallback.result = "from-fallback";
        RetryActionProcessor retry = new RetryActionProcessor(delegate, 2, 0, fallback);
        assertEquals("from-fallback", retry.processSingleAction("prompt"));
        assertEquals(2, delegate.calls);
        assertEquals(1, fallback.calls);
    }

    @Test
    void testFallbackFailure_propagates() {
        FlakyProcessor delegate = new FlakyProcessor(10);
        FlakyProcessor fallback = new FlakyProcessor(10);
        RetryActionProcessor retry = new RetryActionProcessor(delegate, 2, 0, fallback);
        assertThrows(AIProcessingException.class, () -> retry.processSingleAction("prompt"));
    }

    @Test
    void testQuery_isAlsoRetried() throws AIProcessingException {
        FlakyProcessor delegate = new FlakyProcessor(1);
        delegate.result = "answer";
        RetryActionProcessor retry = new RetryActionProcessor(delegate, 3, 0, null);
        assertEquals("answer", retry.query("question"));
        assertEquals(2, delegate.calls);
    }

    @Test
    void testInvalidConstructorArgs() {
        FlakyProcessor delegate = new FlakyProcessor(0);
        assertThrows(IllegalArgumentException.class, () -> new RetryActionProcessor(null));
        assertThrows(IllegalArgumentException.class, () -> new RetryActionProcessor(delegate, 0, 0, null));
        assertThrows(IllegalArgumentException.class, () -> new RetryActionProcessor(delegate, 1, -1, null));
    }

    @Test
    void testDefaultConstructor_threeAttempts() {
        FlakyProcessor delegate = new FlakyProcessor(10);
        // default backoff is 500ms — keep failures cheap by asserting call count only
        RetryActionProcessor retry = new RetryActionProcessor(delegate);
        assertThrows(AIProcessingException.class, () -> retry.processSingleAction("p"));
        assertEquals(3, delegate.calls);
    }
}
