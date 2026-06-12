package com.t4a.agent.resilience;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * Decorator around any {@link AIProcessor} that retries failed calls with exponential backoff,
 * optionally falling back to a secondary processor when all retries are exhausted.
 *
 * <p>LLM calls fail transiently all the time — rate limits, network blips, model overload.
 * Without this decorator a single {@link AIProcessingException} propagates straight to the
 * caller. With it:
 * <pre>
 *   prompt → [attempt 1 fails] → wait → [attempt 2 fails] → wait → [attempt 3 succeeds] → result
 *   prompt → [all attempts fail] → [fallback processor, e.g. a different LLM] → result
 * </pre>
 *
 * <p>Example — retry OpenAI 3 times, then fall back to Gemini:
 * <pre>{@code
 * AIProcessor resilient = new RetryActionProcessor(
 *         new OpenAiActionProcessor(),
 *         3,                              // max attempts
 *         500,                            // initial backoff ms (doubles each retry)
 *         new GeminiV2ActionProcessor()); // fallback (may be null)
 *
 * Object result = resilient.processSingleAction("Book a flight to Bangalore");
 * }</pre>
 *
 * <p>All other {@link AIProcessor} methods are wrapped with the same retry semantics.
 */
@Slf4j
public class RetryActionProcessor implements AIProcessor {

    private final AIProcessor delegate;
    private final int maxAttempts;
    private final long initialBackoffMillis;
    private final AIProcessor fallback;

    /** Retry up to 3 times with 500 ms initial backoff and no fallback. */
    public RetryActionProcessor(AIProcessor delegate) {
        this(delegate, 3, 500, null);
    }

    /**
     * @param delegate             the real processor to call
     * @param maxAttempts          total attempts including the first one (must be &gt;= 1)
     * @param initialBackoffMillis wait before the first retry; doubles on each subsequent retry
     * @param fallback             processor to try once after all attempts fail; may be {@code null}
     */
    public RetryActionProcessor(AIProcessor delegate, int maxAttempts, long initialBackoffMillis, AIProcessor fallback) {
        if (delegate == null) throw new IllegalArgumentException("delegate must not be null");
        if (maxAttempts < 1) throw new IllegalArgumentException("maxAttempts must be >= 1");
        if (initialBackoffMillis < 0) throw new IllegalArgumentException("initialBackoffMillis must be >= 0");
        this.delegate = delegate;
        this.maxAttempts = maxAttempts;
        this.initialBackoffMillis = initialBackoffMillis;
        this.fallback = fallback;
    }

    @FunctionalInterface
    private interface ProcessorCall<T> {
        T call(AIProcessor processor) throws AIProcessingException;
    }

    private <T> T withRetry(String prompt, ProcessorCall<T> call) throws AIProcessingException {
        AIProcessingException lastFailure = null;
        long backoff = initialBackoffMillis;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return call.call(delegate);
            } catch (AIProcessingException e) {
                lastFailure = e;
                log.warn("Attempt {}/{} failed for prompt '{}': {}", attempt, maxAttempts, prompt, e.getMessage());
                if (attempt < maxAttempts) {
                    sleep(backoff);
                    backoff *= 2;
                }
            }
        }
        if (fallback != null) {
            log.info("All {} attempts failed, invoking fallback processor {}", maxAttempts, fallback.getClass().getSimpleName());
            return call.call(fallback);
        }
        throw lastFailure;
    }

    private void sleep(long millis) throws AIProcessingException {
        if (millis <= 0) return;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new AIProcessingException("Retry interrupted while backing off");
        }
    }

    @Override
    public Object processSingleAction(String promptText) throws AIProcessingException {
        return withRetry(promptText, p -> p.processSingleAction(promptText));
    }

    @Override
    public Object processSingleAction(String promptText, ActionCallback callback) throws AIProcessingException {
        return withRetry(promptText, p -> p.processSingleAction(promptText, callback));
    }

    @Override
    public Object processSingleAction(String promptText, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        return withRetry(promptText, p -> p.processSingleAction(promptText, action, humanVerification, explain));
    }

    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        return withRetry(promptText, p -> p.processSingleAction(promptText, humanVerification, explain));
    }

    @Override
    public Object processSingleAction(String prompt, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain,
                                      ActionCallback callback) throws AIProcessingException {
        return withRetry(prompt, p -> p.processSingleAction(prompt, action, humanVerification, explain, callback));
    }

    @Override
    public String query(String promptText) throws AIProcessingException {
        return withRetry(promptText, p -> p.query(promptText));
    }
}
