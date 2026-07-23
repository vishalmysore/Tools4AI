package com.t4a.agent.resilience;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class RateLimitedActionProcessorTest {

    static class CountingProcessor implements AIProcessor {
        final AtomicInteger calls = new AtomicInteger();

        private Object attempt() {
            calls.incrementAndGet();
            return "ok";
        }

        @Override public Object processSingleAction(String p) { return attempt(); }
        @Override public Object processSingleAction(String p, ActionCallback cb) { return attempt(); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e) { return attempt(); }
        @Override public Object processSingleAction(String p, HumanInLoop h, ExplainDecision e) { return attempt(); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e, ActionCallback cb) { return attempt(); }
        @Override public String query(String p) { return (String) attempt(); }
    }

    @Test
    void testCallsWithinBurstPassImmediately() throws AIProcessingException {
        CountingProcessor delegate = new CountingProcessor();
        RateLimitedActionProcessor limited = new RateLimitedActionProcessor(delegate, 1.0, 3, 0);

        limited.processSingleAction("a");
        limited.processSingleAction("b");
        limited.processSingleAction("c");

        assertEquals(3, delegate.calls.get());
    }

    @Test
    void testExceedingBurstFailsFastWhenNoWaitAllowed() throws AIProcessingException {
        CountingProcessor delegate = new CountingProcessor();
        RateLimitedActionProcessor limited = new RateLimitedActionProcessor(delegate, 1.0, 1, 0);

        limited.processSingleAction("first");

        RateLimitExceededException ex = assertThrows(RateLimitExceededException.class,
                () -> limited.processSingleAction("second"));

        assertTrue(ex.getWaitedMillis() > 0);
        assertEquals(1, delegate.calls.get(), "the rejected call must not reach the delegate");
    }

    @Test
    void testRateLimitExceededIsAnAIProcessingException() {
        CountingProcessor delegate = new CountingProcessor();
        RateLimitedActionProcessor limited = new RateLimitedActionProcessor(delegate, 1.0, 1, 0);

        assertThrows(AIProcessingException.class, () -> {
            limited.processSingleAction("first");
            limited.processSingleAction("second");
        });
    }

    @Test
    void testCallerWaitsWhenBudgetAllows() throws AIProcessingException {
        CountingProcessor delegate = new CountingProcessor();
        // 20 permits/sec => a permit becomes available roughly every 50ms
        RateLimitedActionProcessor limited = new RateLimitedActionProcessor(delegate, 20.0, 1, 2000);

        long start = System.currentTimeMillis();
        limited.processSingleAction("a");
        limited.processSingleAction("b");
        long elapsed = System.currentTimeMillis() - start;

        assertEquals(2, delegate.calls.get());
        assertTrue(elapsed >= 30, "second call should have waited for a permit, elapsed " + elapsed + "ms");
    }

    @Test
    void testTokensRefillWhileIdle() throws AIProcessingException, InterruptedException {
        CountingProcessor delegate = new CountingProcessor();
        RateLimitedActionProcessor limited = new RateLimitedActionProcessor(delegate, 20.0, 1, 0);

        limited.processSingleAction("a");
        Thread.sleep(120);                  // > 50ms, so a fresh permit has accrued
        limited.processSingleAction("b");   // must not throw

        assertEquals(2, delegate.calls.get());
    }

    @Test
    void testEveryOverloadIsThrottled() throws AIProcessingException {
        CountingProcessor delegate = new CountingProcessor();
        RateLimitedActionProcessor limited = new RateLimitedActionProcessor(delegate, 1.0, 6, 0);

        limited.processSingleAction("a");
        limited.processSingleAction("b", (ActionCallback) null);
        limited.processSingleAction("c", null, null, null);
        limited.processSingleAction("d", (HumanInLoop) null, (ExplainDecision) null);
        limited.processSingleAction("e", null, null, null, null);
        limited.query("f");

        assertEquals(6, delegate.calls.get());
        assertThrows(RateLimitExceededException.class, () -> limited.query("g"));
    }

    @Test
    void testConstructorValidation() {
        CountingProcessor delegate = new CountingProcessor();
        assertThrows(IllegalArgumentException.class, () -> new RateLimitedActionProcessor(null, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new RateLimitedActionProcessor(delegate, 0));
        assertThrows(IllegalArgumentException.class, () -> new RateLimitedActionProcessor(delegate, -1));
        assertThrows(IllegalArgumentException.class, () -> new RateLimitedActionProcessor(delegate, 1.0, 0, 100));
        assertThrows(IllegalArgumentException.class, () -> new RateLimitedActionProcessor(delegate, 1.0, 1, -1));
    }

    @Test
    void testConcurrentCallersNeverExceedTheBudget() throws InterruptedException {
        CountingProcessor delegate = new CountingProcessor();
        // burst of 4 permits and no waiting: exactly 4 of the 12 concurrent callers may proceed
        RateLimitedActionProcessor limited = new RateLimitedActionProcessor(delegate, 0.001, 4, 0);

        int threads = 12;
        AtomicInteger rejected = new AtomicInteger();
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                try {
                    limited.processSingleAction("concurrent");
                } catch (RateLimitExceededException expected) {
                    rejected.incrementAndGet();
                } catch (AIProcessingException e) {
                    fail("unexpected " + e);
                }
            });
            workers[i].start();
        }
        for (Thread worker : workers) worker.join();

        assertEquals(4, delegate.calls.get());
        assertEquals(threads - 4, rejected.get());
    }
}
