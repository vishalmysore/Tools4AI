package com.t4a.agent.orchestration;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds the per-agent results produced by an {@link AgentOrchestrator} run.
 *
 * <p>Results are stored in insertion order (the order agents were invoked) so callers
 * can iterate them predictably.
 */
@Getter
public class OrchestrationResult {

    /** Agent name → result returned by that agent's {@code processSingleAction} call. */
    private final Map<String, Object> results = new LinkedHashMap<>();

    /** The original goal prompt that triggered this orchestration run. */
    private final String goal;

    /** Whether every agent completed without throwing an exception. */
    private boolean successful = true;

    /** Optional synthesised summary built by the orchestrator after all agents finish. */
    private String summary;

    public OrchestrationResult(String goal) {
        this.goal = goal;
    }

    void addResult(String agentName, Object result) {
        results.put(agentName, result);
    }

    void markFailed(String agentName, Exception e) {
        results.put(agentName, "ERROR: " + e.getMessage());
        successful = false;
    }

    void setSummary(String summary) {
        this.summary = summary;
    }

    /** Unmodifiable view of the result map. */
    public Map<String, Object> getResults() {
        return Collections.unmodifiableMap(results);
    }

    @Override
    public String toString() {
        return "OrchestrationResult{goal='" + goal + "', successful=" + successful
                + ", results=" + results + '}';
    }
}
