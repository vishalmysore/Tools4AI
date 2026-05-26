package com.t4a.agent.memory;

import java.util.List;

/**
 * Abstraction for agent memory. Implementations can be short-term (in-memory, bounded)
 * or long-term (file-backed, database-backed).
 *
 * <p>Memory is used to give the agent context across multiple prompt/action turns so it
 * does not start each request from a blank slate.
 */
public interface AgentMemory {

    /**
     * Record a completed turn — the prompt that was issued and the result that came back.
     */
    void addTurn(String prompt, Object result);

    /**
     * Return all turns currently held in this memory, oldest first.
     */
    List<MemoryTurn> getHistory();

    /**
     * Build a single context string suitable for prepending to a new prompt so the LLM
     * can see prior conversation turns.
     *
     * <p>Format example:
     * <pre>
     * [Previous context]
     * Turn 1 - Prompt: "..." | Result: "..."
     * Turn 2 - Prompt: "..." | Result: "..."
     * [End context]
     * </pre>
     */
    String getHistoryAsContext();

    /** Remove all stored turns. */
    void clear();

    /** How many turns are currently stored. */
    int size();
}
