package com.t4a.agent.memory;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Short-term, in-process agent memory backed by a bounded deque.
 *
 * <p>Once {@code maxTurns} is reached the oldest turn is evicted to make room for the new one
 * (sliding-window behaviour). This prevents the context string from growing unboundedly.
 *
 * <p>Thread-safe — all mutations are synchronised on the internal deque.
 *
 * <p>Example usage:
 * <pre>{@code
 * AgentMemory memory = new InMemoryAgentMemory(10);
 * memory.addTurn("What is the weather?", "Sunny, 25 °C");
 * memory.addTurn("Book a taxi", "Taxi booked for 3 PM");
 *
 * String contextualPrompt = memory.getHistoryAsContext() + "\nNow: remind me what I booked.";
 * }</pre>
 */
@Slf4j
public class InMemoryAgentMemory implements AgentMemory {

    /** Default window size — keep the last 20 turns. */
    public static final int DEFAULT_MAX_TURNS = 20;

    private final int maxTurns;
    private final LinkedList<MemoryTurn> turns = new LinkedList<>();

    public InMemoryAgentMemory() {
        this(DEFAULT_MAX_TURNS);
    }

    public InMemoryAgentMemory(int maxTurns) {
        if (maxTurns <= 0) throw new IllegalArgumentException("maxTurns must be > 0");
        this.maxTurns = maxTurns;
    }

    @Override
    public synchronized void addTurn(String prompt, Object result) {
        if (turns.size() >= maxTurns) {
            MemoryTurn evicted = turns.removeFirst();
            log.debug("Memory window full — evicted oldest turn: '{}'", evicted.getPrompt());
        }
        turns.addLast(new MemoryTurn(prompt, result));
        log.debug("Memory turn added (total={}): '{}'", turns.size(), prompt);
    }

    @Override
    public synchronized List<MemoryTurn> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(turns));
    }

    @Override
    public synchronized String getHistoryAsContext() {
        if (turns.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("[Previous context]\n");
        int i = 1;
        for (MemoryTurn t : turns) {
            sb.append("Turn ").append(i++).append(" - Prompt: \"").append(t.getPrompt())
              .append("\" | Result: \"").append(t.getResult()).append("\"\n");
        }
        sb.append("[End context]\n");
        return sb.toString();
    }

    @Override
    public synchronized void clear() {
        turns.clear();
        log.debug("Memory cleared");
    }

    @Override
    public synchronized int size() {
        return turns.size();
    }

    /** Maximum number of turns this memory window holds. */
    public int getMaxTurns() {
        return maxTurns;
    }
}
