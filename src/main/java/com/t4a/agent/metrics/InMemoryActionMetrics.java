package com.t4a.agent.metrics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dependency-free, thread-safe {@link ActionMetrics} that keeps running counters in memory.
 *
 * <p>Memory use is bounded by the number of distinct operation names, not by the number of
 * invocations — each operation holds seven longs regardless of traffic, so it is safe to leave
 * enabled in production.
 *
 * <pre>{@code
 * InMemoryActionMetrics metrics = new InMemoryActionMetrics();
 * AIProcessor metered = new MeteredActionProcessor(new OpenAiActionProcessor(), metrics);
 *
 * metered.processSingleAction("Book a flight to Bangalore");
 * System.out.println(metrics.snapshot("processSingleAction"));
 * // processSingleAction{count=1, success=1, failure=0, avgMs=812.0, minMs=812, maxMs=812}
 * }</pre>
 */
public class InMemoryActionMetrics implements ActionMetrics {

    private final Map<String, Counters> byOperation = new ConcurrentHashMap<>();

    /** Mutable counter set for a single operation; every mutation and read is synchronized. */
    private static final class Counters {
        long total;
        long success;
        long failure;
        long totalDuration;
        long minDuration = Long.MAX_VALUE;
        long maxDuration = Long.MIN_VALUE;

        synchronized void add(boolean ok, long durationMs) {
            total++;
            if (ok) success++; else failure++;
            totalDuration += durationMs;
            if (durationMs < minDuration) minDuration = durationMs;
            if (durationMs > maxDuration) maxDuration = durationMs;
        }

        synchronized MetricsSnapshot toSnapshot(String operation) {
            return new MetricsSnapshot(operation, total, success, failure,
                    totalDuration, minDuration, maxDuration);
        }
    }

    @Override
    public void record(String operation, boolean success, long durationMs) {
        if (operation == null) operation = "unknown";
        byOperation.computeIfAbsent(operation, k -> new Counters()).add(success, durationMs);
    }

    @Override
    public MetricsSnapshot snapshot(String operation) {
        Counters counters = byOperation.get(operation);
        return counters == null
                ? new MetricsSnapshot(operation, 0, 0, 0, 0, 0, 0)
                : counters.toSnapshot(operation);
    }

    @Override
    public Map<String, MetricsSnapshot> snapshotAll() {
        Map<String, MetricsSnapshot> all = new LinkedHashMap<>();
        byOperation.forEach((operation, counters) -> all.put(operation, counters.toSnapshot(operation)));
        return all;
    }

    @Override
    public void reset() {
        byOperation.clear();
    }

    /** Multi-line report of every operation — handy for a health endpoint or a shutdown hook. */
    public String report() {
        StringBuilder sb = new StringBuilder("Tools4AI action metrics:");
        snapshotAll().values().forEach(snapshot -> sb.append("\n  ").append(snapshot));
        return sb.toString();
    }
}
