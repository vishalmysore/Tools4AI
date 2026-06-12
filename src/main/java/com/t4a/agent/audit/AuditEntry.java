package com.t4a.agent.audit;

/**
 * One record in the agent audit trail — what was asked, what happened, when, and how long it took.
 *
 * <p>Plain public-field DTO (same style as
 * {@code PersistentFileAgentMemory.PersistentTurn}) so Jackson can serialise it without Lombok.
 */
public class AuditEntry {

    public long timestampMs;
    public String prompt;
    public String result;
    public boolean success;
    public String errorMessage;
    public long durationMs;

    // required by Jackson
    public AuditEntry() {}

    public AuditEntry(long timestampMs, String prompt, Object result,
                      boolean success, String errorMessage, long durationMs) {
        this.timestampMs = timestampMs;
        this.prompt = prompt;
        this.result = result == null ? null : result.toString();
        this.success = success;
        this.errorMessage = errorMessage;
        this.durationMs = durationMs;
    }

    @Override
    public String toString() {
        return "AuditEntry{timestampMs=" + timestampMs + ", prompt='" + prompt + "', success=" + success
                + ", durationMs=" + durationMs + (errorMessage != null ? ", error='" + errorMessage + "'" : "") + "}";
    }
}
