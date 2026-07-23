package com.t4a.agent.metrics;

/**
 * Immutable point-in-time view of the counters collected for one operation.
 *
 * <p>Read it from {@link ActionMetrics#snapshot(String)} or {@link ActionMetrics#snapshotAll()};
 * the numbers never change afterwards, so it is safe to hand to a reporting thread.
 */
public class MetricsSnapshot {

    private final String operation;
    private final long totalCount;
    private final long successCount;
    private final long failureCount;
    private final long totalDurationMs;
    private final long minDurationMs;
    private final long maxDurationMs;

    public MetricsSnapshot(String operation, long totalCount, long successCount, long failureCount,
                           long totalDurationMs, long minDurationMs, long maxDurationMs) {
        this.operation = operation;
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.totalDurationMs = totalDurationMs;
        this.minDurationMs = minDurationMs;
        this.maxDurationMs = maxDurationMs;
    }

    /** Name of the measured operation, e.g. {@code processSingleAction}. */
    public String getOperation() {
        return operation;
    }

    /** Total invocations recorded, successful and failed. */
    public long getTotalCount() {
        return totalCount;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public long getFailureCount() {
        return failureCount;
    }

    public long getTotalDurationMs() {
        return totalDurationMs;
    }

    /** Fastest recorded invocation, or 0 when nothing has been recorded. */
    public long getMinDurationMs() {
        return totalCount == 0 ? 0 : minDurationMs;
    }

    /** Slowest recorded invocation, or 0 when nothing has been recorded. */
    public long getMaxDurationMs() {
        return totalCount == 0 ? 0 : maxDurationMs;
    }

    /** Mean latency in milliseconds, or 0 when nothing has been recorded. */
    public double getAverageDurationMs() {
        return totalCount == 0 ? 0d : (double) totalDurationMs / totalCount;
    }

    /** Fraction of invocations that succeeded, in the range 0.0–1.0; 0 when nothing recorded. */
    public double getSuccessRate() {
        return totalCount == 0 ? 0d : (double) successCount / totalCount;
    }

    /** Fraction of invocations that failed, in the range 0.0–1.0; 0 when nothing recorded. */
    public double getErrorRate() {
        return totalCount == 0 ? 0d : (double) failureCount / totalCount;
    }

    @Override
    public String toString() {
        return String.format("%s{count=%d, success=%d, failure=%d, avgMs=%.1f, minMs=%d, maxMs=%d}",
                operation, totalCount, successCount, failureCount,
                getAverageDurationMs(), getMinDurationMs(), getMaxDurationMs());
    }
}
