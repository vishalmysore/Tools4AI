package com.t4a.agent.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bounded in-memory audit trail — keeps the most recent {@code maxEntries} records.
 *
 * <p>Good for tests and for exposing a "recent activity" view in an admin endpoint.
 * For a durable record use {@link JsonFileAuditTrail}.
 */
public class InMemoryAuditTrail implements AuditTrail {

    private final int maxEntries;
    private final List<AuditEntry> entries = new ArrayList<>();

    /** Unbounded for practical purposes (one million entries). */
    public InMemoryAuditTrail() {
        this(1_000_000);
    }

    public InMemoryAuditTrail(int maxEntries) {
        if (maxEntries < 1) throw new IllegalArgumentException("maxEntries must be >= 1");
        this.maxEntries = maxEntries;
    }

    @Override
    public synchronized void record(AuditEntry entry) {
        entries.add(entry);
        while (entries.size() > maxEntries) {
            entries.remove(0);
        }
    }

    @Override
    public synchronized List<AuditEntry> getEntries() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }
}
