package com.t4a.agent.async;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class AsyncActionProcessorTest {

    /** Sleeps for a fixed time then echoes the prompt, so overlap is measurable. */
    static class SlowProcessor implements AIProcessor {
        long sleepMillis;
        boolean throwError = false;
        final AtomicInteger calls = new AtomicInteger();

        SlowProcessor(long sleepMillis) {
            this.sleepMillis = sleepMillis;
        }

        private Object attempt(String prompt) throws AIProcessingException {
            calls.incrementAndGet();
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (throwError) throw new AIProcessingException("boom");
            return "handled:" + prompt;
        }

        @Override public Object processSingleAction(String p) throws AIProcessingException { return attempt(p); }
        @Override public Object processSingleAction(String p, ActionCallback cb) throws AIProcessingException { return attempt(p); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e) throws AIProcessingException { return attempt(p); }
        @Override public Object processSingleAction(String p, HumanInLoop h, ExplainDecision e) throws AIProcessingException { return attempt(p); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e, ActionCallback cb) throws AIProcessingException { return attempt(p); }
        @Override public String query(String p) throws AIProcessingException { return (String) attempt(p); }
    }

    @Test
    void testSingleActionCompletesWithDelegateResult() throws Exception {
        try (AsyncActionProcessor async = new AsyncActionProcessor(new SlowProcessor(0))) {
            assertEquals("handled:book a flight", async.processSingleActionAsync("book a flight").get(5, TimeUnit.SECONDS));
        }
    }

    @Test
    void testQueryCompletesAsynchronously() throws Exception {
        try (AsyncActionProcessor async = new AsyncActionProcessor(new SlowProcessor(0))) {
            assertEquals("handled:summarise", async.queryAsync("summarise").get(5, TimeUnit.SECONDS));
        }
    }

    @Test
    void testIndependentCallsRunConcurrently() throws Exception {
        SlowProcessor delegate = new SlowProcessor(200);
        try (AsyncActionProcessor async = new AsyncActionProcessor(delegate, 3)) {
            long start = System.currentTimeMillis();

            CompletableFuture<Object> a = async.processSingleActionAsync("a");
            CompletableFuture<Object> b = async.processSingleActionAsync("b");
            CompletableFuture<Object> c = async.processSingleActionAsync("c");
            CompletableFuture.allOf(a, b, c).get(5, TimeUnit.SECONDS);

            long elapsed = System.currentTimeMillis() - start;
            assertEquals(3, delegate.calls.get());
            assertTrue(elapsed < 500,
                    "3 x 200ms calls on 3 threads should overlap, took " + elapsed + "ms");
        }
    }

    @Test
    void testProcessAllPreservesPromptOrder() throws Exception {
        try (AsyncActionProcessor async = new AsyncActionProcessor(new SlowProcessor(20), 4)) {
            List<String> prompts = Arrays.asList("first", "second", "third", "fourth");

            List<Object> results = async.processAllAsync(prompts).get(5, TimeUnit.SECONDS);

            assertEquals(Arrays.asList("handled:first", "handled:second", "handled:third", "handled:fourth"), results);
        }
    }

    @Test
    void testProcessAllWithEmptyOrNullInput() throws Exception {
        try (AsyncActionProcessor async = new AsyncActionProcessor(new SlowProcessor(0))) {
            assertEquals(new ArrayList<>(), async.processAllAsync(Collections.emptyList()).get(5, TimeUnit.SECONDS));
            assertEquals(new ArrayList<>(), async.processAllAsync(null).get(5, TimeUnit.SECONDS));
        }
    }

    @Test
    void testFailureSurfacesAsCompletionOfOriginalException() {
        SlowProcessor delegate = new SlowProcessor(0);
        delegate.throwError = true;

        try (AsyncActionProcessor async = new AsyncActionProcessor(delegate)) {
            CompletableFuture<Object> future = async.processSingleActionAsync("boom");

            ExecutionException ex = assertThrows(ExecutionException.class, () -> future.get(5, TimeUnit.SECONDS));
            assertTrue(ex.getCause() instanceof AIProcessingException, "cause was " + ex.getCause());
            assertEquals("boom", ex.getCause().getMessage());
        }
    }

    @Test
    void testCallerSuppliedExecutorSurvivesClose() throws Exception {
        ExecutorService shared = Executors.newFixedThreadPool(2);
        try {
            AsyncActionProcessor async = new AsyncActionProcessor(new SlowProcessor(0), shared);
            assertEquals("handled:a", async.processSingleActionAsync("a").get(5, TimeUnit.SECONDS));
            async.close();

            assertFalse(shared.isShutdown(), "a caller-managed executor must not be shut down by close()");
            assertEquals("still usable", shared.submit(() -> "still usable").get(5, TimeUnit.SECONDS));
        } finally {
            shared.shutdownNow();
        }
    }

    @Test
    void testOwnedExecutorIsShutDownOnClose() {
        AsyncActionProcessor async = new AsyncActionProcessor(new SlowProcessor(0), 1);
        async.close();

        // the owned pool is gone, so new work is rejected rather than silently queued
        assertThrows(RuntimeException.class, () -> async.processSingleActionAsync("after close").join());
    }

    @Test
    void testDelegateIsExposed() {
        SlowProcessor delegate = new SlowProcessor(0);
        try (AsyncActionProcessor async = new AsyncActionProcessor(delegate)) {
            assertSame(delegate, async.getDelegate());
        }
    }

    @Test
    void testConstructorValidation() {
        assertThrows(IllegalArgumentException.class, () -> new AsyncActionProcessor(null));
        assertThrows(IllegalArgumentException.class, () -> new AsyncActionProcessor(new SlowProcessor(0), 0));
        assertThrows(IllegalArgumentException.class,
                () -> new AsyncActionProcessor(new SlowProcessor(0), (ExecutorService) null));
    }
}
