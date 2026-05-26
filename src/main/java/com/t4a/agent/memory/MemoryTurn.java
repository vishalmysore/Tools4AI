package com.t4a.agent.memory;

import lombok.Getter;

/**
 * Represents a single prompt/result exchange stored in agent memory.
 */
@Getter
public class MemoryTurn {

    private final String prompt;
    private final Object result;
    private final long timestampMs;

    public MemoryTurn(String prompt, Object result) {
        this.prompt = prompt;
        this.result = result;
        this.timestampMs = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "MemoryTurn{prompt='" + prompt + "', result=" + result + '}';
    }
}
