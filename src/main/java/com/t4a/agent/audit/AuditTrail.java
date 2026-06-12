package com.t4a.agent.audit;

import java.util.List;

/**
 * Destination for agent execution audit records.
 *
 * <p>Implementations: {@link InMemoryAuditTrail} (bounded, for tests/inspection) and
 * {@link JsonFileAuditTrail} (append-only JSON-lines file, for production).
 * Used by {@link AuditedActionProcessor}, which records every action execution automatically.
 */
public interface AuditTrail {

    /** Record one execution. Implementations must be safe to call from multiple threads. */
    void record(AuditEntry entry);

    /** Snapshot of the recorded entries, oldest first. */
    List<AuditEntry> getEntries();
}
