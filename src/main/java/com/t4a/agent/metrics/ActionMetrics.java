package com.t4a.agent.metrics;

import java.util.Map;

/**
 * Sink for agent execution measurements — how often each operation ran, how many succeeded,
 * and how long it took.
 *
 * <p>{@link InMemoryActionMetrics} is the built-in implementation and needs no dependencies.
 * Implement this interface to forward the same measurements to Micrometer, Prometheus,
 * OpenTelemetry, or an application's own monitoring stack:
 *
 * <pre>{@code
 * public class MicrometerActionMetrics implements ActionMetrics {
 *     private final MeterRegistry registry;
 *     public void record(String operation, boolean success, long durationMs) {
 *         registry.timer("tools4ai.action", "operation", operation, "outcome", success ? "ok" : "error")
 *                 .record(durationMs, TimeUnit.MILLISECONDS);
 *     }
 *     // snapshot methods can return empty views when the registry owns the data
 * }
 * }</pre>
 *
 * <p>Implementations must be safe to call from multiple threads.
 */
public interface ActionMetrics {

    /**
     * Record one completed invocation.
     *
     * @param operation  logical name of what ran, e.g. {@code processSingleAction} or {@code query}
     * @param success    {@code true} if it returned normally, {@code false} if it threw
     * @param durationMs wall-clock duration in milliseconds
     */
    void record(String operation, boolean success, long durationMs);

    /**
     * Current counters for one operation. Never {@code null} — an operation that has not been
     * recorded yet returns an empty snapshot with zero counts.
     */
    MetricsSnapshot snapshot(String operation);

    /** Current counters for every operation recorded so far, keyed by operation name. */
    Map<String, MetricsSnapshot> snapshotAll();

    /** Discard all collected measurements. */
    void reset();
}
