package com.t4a.agent.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryAgentMemoryTest {

    private InMemoryAgentMemory memory;

    @BeforeEach
    void setUp() {
        memory = new InMemoryAgentMemory(3); // small window for eviction tests
    }

    @Test
    void testInitiallyEmpty() {
        assertEquals(0, memory.size());
        assertTrue(memory.getHistory().isEmpty());
        assertEquals("", memory.getHistoryAsContext());
    }

    @Test
    void testAddTurnAndSize() {
        memory.addTurn("What is 2+2?", "4");
        assertEquals(1, memory.size());
    }

    @Test
    void testGetHistoryOrder() {
        memory.addTurn("prompt1", "result1");
        memory.addTurn("prompt2", "result2");

        List<MemoryTurn> history = memory.getHistory();
        assertEquals(2, history.size());
        assertEquals("prompt1", history.get(0).getPrompt());
        assertEquals("prompt2", history.get(1).getPrompt());
    }

    @Test
    void testSlidingWindowEviction() {
        memory.addTurn("p1", "r1");
        memory.addTurn("p2", "r2");
        memory.addTurn("p3", "r3");
        // window full (maxTurns=3), add one more
        memory.addTurn("p4", "r4");

        assertEquals(3, memory.size());
        List<MemoryTurn> history = memory.getHistory();
        // p1 should have been evicted
        assertEquals("p2", history.get(0).getPrompt());
        assertEquals("p4", history.get(2).getPrompt());
    }

    @Test
    void testGetHistoryAsContext_containsKeywords() {
        memory.addTurn("Book a flight", "Flight confirmed");
        String ctx = memory.getHistoryAsContext();
        assertTrue(ctx.contains("[Previous context]"));
        assertTrue(ctx.contains("Book a flight"));
        assertTrue(ctx.contains("Flight confirmed"));
        assertTrue(ctx.contains("[End context]"));
    }

    @Test
    void testClear() {
        memory.addTurn("p1", "r1");
        memory.addTurn("p2", "r2");
        memory.clear();
        assertEquals(0, memory.size());
        assertEquals("", memory.getHistoryAsContext());
    }

    @Test
    void testHistoryIsUnmodifiable() {
        memory.addTurn("p1", "r1");
        List<MemoryTurn> history = memory.getHistory();
        assertThrows(UnsupportedOperationException.class, () -> history.add(new MemoryTurn("x", "y")));
    }

    @Test
    void testMemoryTurnTimestamp() {
        long before = System.currentTimeMillis();
        memory.addTurn("q", "a");
        long after = System.currentTimeMillis();
        MemoryTurn turn = memory.getHistory().get(0);
        assertTrue(turn.getTimestampMs() >= before);
        assertTrue(turn.getTimestampMs() <= after);
    }

    @Test
    void testDefaultMaxTurns() {
        InMemoryAgentMemory defaultMemory = new InMemoryAgentMemory();
        assertEquals(InMemoryAgentMemory.DEFAULT_MAX_TURNS, defaultMemory.getMaxTurns());
    }

    @Test
    void testInvalidMaxTurnsThrows() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryAgentMemory(0));
        assertThrows(IllegalArgumentException.class, () -> new InMemoryAgentMemory(-1));
    }

    @Test
    void testNullResultStoredGracefully() {
        memory.addTurn("prompt", null);
        assertEquals(1, memory.size());
        assertNull(memory.getHistory().get(0).getResult());
    }
}
