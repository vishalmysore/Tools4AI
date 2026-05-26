package com.t4a.agent.memory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistentFileAgentMemoryTest {

    @TempDir
    Path tempDir;

    private File storageFile;
    private PersistentFileAgentMemory memory;

    @BeforeEach
    void setUp() {
        storageFile = tempDir.resolve("memory.json").toFile();
        memory = new PersistentFileAgentMemory(storageFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        storageFile.delete();
    }

    @Test
    void testInitiallyEmpty() {
        assertEquals(0, memory.size());
        assertTrue(memory.getHistory().isEmpty());
    }

    @Test
    void testAddTurnPersistsToFile() {
        memory.addTurn("Hello", "World");
        assertTrue(storageFile.exists());
        assertTrue(storageFile.length() > 0);
    }

    @Test
    void testSurvivesReload() {
        memory.addTurn("What time is it?", "3 PM");
        memory.addTurn("Book a taxi", "Taxi booked");

        // Create a new instance from same file — should reload
        PersistentFileAgentMemory reloaded =
                new PersistentFileAgentMemory(storageFile.getAbsolutePath());
        assertEquals(2, reloaded.size());

        List<MemoryTurn> history = reloaded.getHistory();
        assertEquals("What time is it?", history.get(0).getPrompt());
        assertEquals("Book a taxi", history.get(1).getPrompt());
    }

    @Test
    void testClearEmptiesFileAndMemory() {
        memory.addTurn("p1", "r1");
        memory.clear();
        assertEquals(0, memory.size());

        // Reload: should be empty
        PersistentFileAgentMemory reloaded =
                new PersistentFileAgentMemory(storageFile.getAbsolutePath());
        assertEquals(0, reloaded.size());
    }

    @Test
    void testGetHistoryAsContext() {
        memory.addTurn("first prompt", "first result");
        String ctx = memory.getHistoryAsContext();
        assertTrue(ctx.contains("first prompt"));
        assertTrue(ctx.contains("first result"));
    }

    @Test
    void testMissingFileStartsFresh() {
        File nonExistent = tempDir.resolve("does_not_exist.json").toFile();
        PersistentFileAgentMemory fresh =
                new PersistentFileAgentMemory(nonExistent.getAbsolutePath());
        assertEquals(0, fresh.size());
    }
}
