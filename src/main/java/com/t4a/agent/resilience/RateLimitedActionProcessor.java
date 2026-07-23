package com.t4a.agent.resilience;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * Decorator around any {@link AIProcessor} that throttles calls with a token bucket, keeping an
 * agent inside its provider's requests-per-second quota.
 *
 * <p>Retrying after a 429 is reactive and wasteful — every rejected call still cost a round trip
 * and may extend the provider's cooldown. Rate limiting is the proactive half of the same
 * problem: shape the traffic so the 429 never happens.
 *
 * <pre>{@code
 * // 5 calls/second, allowing a burst of 10, waiting up to 2s for a permit
 * AIProcessor throttled = new RateLimitedActionProcessor(
 *         new OpenAiActionProcessor(), 5.0, 10, 2000);
 *
 * throttled.processSingleAction("Book a flight to Bangalore");
 * }</pre>
 *
 * <p>Callers that exceed the rate block until a permit is available. If the required wait exceeds
 * {@code maxWaitMillis} the call fails fast with {@link RateLimitExceededException} instead —
 * useful for shedding load on a request-serving thread rather than queueing behind it.
 *
 * <p>Pairs naturally with {@link RetryActionProcessor}. Put the rate limiter <em>inside</em> the
 * retry decorator so each retry attempt takes its own permit:
 * <pre>{@code
 * AIProcessor agent = new RetryActionProcessor(
 *         new RateLimitedActionProcessor(new OpenAiActionProcessor(), 5.0), 3, 500, null);
 * }</pre>
 */
@Slf4j
public class RateLimitedActionProcessor implements AIProcessor {

    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    private final AIProcessor delegate;
    private final double permitsPerSecond;
    private final double burstCapacity;
    private final long maxWaitMillis;

    private final Object lock = new Object();
    private double availableTokens;
    private long lastRefillNanos;

    /** Throttle to {@code permitsPerSecond}, no burst allowance, waiting as long as it takes. */
    public RateLimitedActionProcessor(AIProcessor delegate, double permitsPerSecond) {
        this(delegate, permitsPerSecond, 1, Long.MAX_VALUE);
    }

    /**
     * @param delegate         the real processor to call once a permit is granted
     * @param permitsPerSecond sustained rate; must be &gt; 0
     * @param burstCapacity    permits that may accumulate while idle, allowing a short burst; must be &gt;= 1
     * @param maxWaitMillis    longest a caller will block for a permit before
     *                         {@link RateLimitExceededException} is thrown; 0 means fail immediately
     */
    public RateLimitedActionProcessor(AIProcessor delegate, double permitsPerSecond,
                                      int burstCapacity, long maxWaitMillis) {
        if (delegate == null) throw new IllegalArgumentException("delegate must not be null");
        if (permitsPerSecond <= 0) throw new IllegalArgumentException("permitsPerSecond must be > 0");
        if (burstCapacity < 1) throw new IllegalArgumentException("burstCapacity must be >= 1");
        if (maxWaitMillis < 0) throw new IllegalArgumentException("maxWaitMillis must be >= 0");
        this.delegate = delegate;
        this.permitsPerSecond = permitsPerSecond;
        this.burstCapacity = burstCapacity;
        this.maxWaitMillis = maxWaitMillis;
        this.availableTokens = burstCapacity;
        this.lastRefillNanos = System.nanoTime();
    }

    /**
     * Reserve one permit, blocking until it is due.
     *
     * <p>The reservation is made under the lock and the waiting happens outside it, so concurrent
     * callers queue in arrival order instead of all sleeping on the same deadline.
     */
    private void acquire() throws AIProcessingException {
        long waitMillis;
        synchronized (lock) {
            long now = System.nanoTime();
            double refill = (now - lastRefillNanos) / (double) NANOS_PER_SECOND * permitsPerSecond;
            lastRefillNanos = now;
            availableTokens = Math.min(burstCapacity, availableTokens + refill);

            if (availableTokens >= 1) {
                waitMillis = 0;
            } else {
                double deficit = 1 - availableTokens;
                waitMillis = (long) Math.ceil(deficit / permitsPerSecond * 1000);
                if (waitMillis > maxWaitMillis) {
                    throw new RateLimitExceededException(
                            "Rate limit of " + permitsPerSecond + "/s exceeded; a permit needs "
                                    + waitMillis + "ms but maxWait is " + maxWaitMillis + "ms", waitMillis);
                }
            }
            availableTokens -= 1;
        }

        if (waitMillis > 0) {
            log.debug("Rate limited, waiting {}ms for a permit", waitMillis);
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AIProcessingException("Interrupted while waiting for a rate limit permit");
            }
        }
    }

    @FunctionalInterface
    private interface Invocation<T> {
        T run() throws AIProcessingException;
    }

    private <T> T throttled(Invocation<T> invocation) throws AIProcessingException {
        acquire();
        return invocation.run();
    }

    @Override
    public Object processSingleAction(String promptText) throws AIProcessingException {
        return throttled(() -> delegate.processSingleAction(promptText));
    }

    @Override
    public Object processSingleAction(String promptText, ActionCallback callback) throws AIProcessingException {
        return throttled(() -> delegate.processSingleAction(promptText, callback));
    }

    @Override
    public Object processSingleAction(String promptText, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        return throttled(() -> delegate.processSingleAction(promptText, action, humanVerification, explain));
    }

    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        return throttled(() -> delegate.processSingleAction(promptText, humanVerification, explain));
    }

    @Override
    public Object processSingleAction(String prompt, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain,
                                      ActionCallback callback) throws AIProcessingException {
        return throttled(() -> delegate.processSingleAction(prompt, action, humanVerification, explain, callback));
    }

    @Override
    public String query(String promptText) throws AIProcessingException {
        return throttled(() -> delegate.query(promptText));
    }
}
