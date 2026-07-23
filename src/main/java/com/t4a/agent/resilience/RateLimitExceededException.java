package com.t4a.agent.resilience;

import com.t4a.processor.AIProcessingException;

/**
 * Thrown by {@link RateLimitedActionProcessor} when a permit could not be obtained within the
 * configured maximum wait.
 *
 * <p>Distinguishing this from a generic {@link AIProcessingException} matters when the two
 * decorators are stacked: a rate-limit rejection is a local back-pressure signal, not a provider
 * failure, so a surrounding {@code RetryActionProcessor} retrying it immediately only makes the
 * congestion worse.
 */
public class RateLimitExceededException extends AIProcessingException {

    private final long waitedMillis;

    public RateLimitExceededException(String message, long waitedMillis) {
        super(message);
        this.waitedMillis = waitedMillis;
    }

    /** How long the caller would have had to wait for a permit. */
    public long getWaitedMillis() {
        return waitedMillis;
    }
}
