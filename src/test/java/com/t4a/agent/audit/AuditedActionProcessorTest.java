package com.t4a.agent.audit;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuditedActionProcessorTest {

    static class ScriptedProcessor implements AIProcessor {
        Object result = "tool result";
        boolean throwError = false;

        private Object attempt() throws AIProcessingException {
            if (throwError) throw new AIProcessingException("boom");
            return result;
        }

        @Override public Object processSingleAction(String p) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, ActionCallback cb) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, HumanInLoop h, ExplainDecision e) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e, ActionCallback cb) throws AIProcessingException { return attempt(); }
        @Override public String query(String p) throws AIProcessingException { return (String) attempt(); }
    }

    @Test
    void testSuccessfulCall_recordsEntry() throws AIProcessingException {
        InMemoryAuditTrail trail = new InMemoryAuditTrail();
        AuditedActionProcessor audited = new AuditedActionProcessor(new ScriptedProcessor(), trail);

        Object result = audited.processSingleAction("restart server");
        assertEquals("tool result", result);

        List<AuditEntry> entries = trail.getEntries();
        assertEquals(1, entries.size());
        AuditEntry e = entries.get(0);
        assertTrue(e.success);
        assertEquals("restart server", e.prompt);
        assertEquals("tool result", e.result);
        assertNull(e.errorMessage);
        assertTrue(e.timestampMs > 0);
    }

    @Test
    void testFailedCall_recordsFailureAndRethrows() {
        InMemoryAuditTrail trail = new InMemoryAuditTrail();
        ScriptedProcessor delegate = new ScriptedProcessor();
        delegate.throwError = true;
        AuditedActionProcessor audited = new AuditedActionProcessor(delegate, trail);

        assertThrows(AIProcessingException.class, () -> audited.processSingleAction("bad prompt"));

        List<AuditEntry> entries = trail.getEntries();
        assertEquals(1, entries.size());
        assertFalse(entries.get(0).success);
        assertEquals("boom", entries.get(0).errorMessage);
        assertNull(entries.get(0).result);
    }

    @Test
    void testQuery_isAudited() throws AIProcessingException {
        InMemoryAuditTrail trail = new InMemoryAuditTrail();
        AuditedActionProcessor audited = new AuditedActionProcessor(new ScriptedProcessor(), trail);
        audited.query("plain question");
        assertEquals(1, trail.getEntries().size());
        assertEquals("plain question", trail.getEntries().get(0).prompt);
    }

    @Test
    void testInMemoryTrail_boundedEviction() {
        InMemoryAuditTrail trail = new InMemoryAuditTrail(2);
        trail.record(new AuditEntry(1, "p1", "r1", true, null, 5));
        trail.record(new AuditEntry(2, "p2", "r2", true, null, 5));
        trail.record(new AuditEntry(3, "p3", "r3", true, null, 5));
        List<AuditEntry> entries = trail.getEntries();
        assertEquals(2, entries.size());
        assertEquals("p2", entries.get(0).prompt);
        assertEquals("p3", entries.get(1).prompt);
    }

    @Test
    void testJsonFileTrail_appendsAndReloads(@TempDir Path tempDir) {
        String file = tempDir.resolve("audit.jsonl").toString();
        JsonFileAuditTrail trail = new JsonFileAuditTrail(file);
        trail.record(new AuditEntry(100, "prompt one", "result one", true, null, 12));
        trail.record(new AuditEntry(200, "prompt two", null, false, "rate limited", 34));

        // a fresh instance reads the same file — entries survive restarts
        List<AuditEntry> entries = new JsonFileAuditTrail(file).getEntries();
        assertEquals(2, entries.size());
        assertEquals("prompt one", entries.get(0).prompt);
        assertTrue(entries.get(0).success);
        assertFalse(entries.get(1).success);
        assertEquals("rate limited", entries.get(1).errorMessage);
    }

    @Test
    void testJsonFileTrail_missingFile_returnsEmpty(@TempDir Path tempDir) {
        JsonFileAuditTrail trail = new JsonFileAuditTrail(tempDir.resolve("nope.jsonl").toString());
        assertTrue(trail.getEntries().isEmpty());
    }

    @Test
    void testInvalidConstructorArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new AuditedActionProcessor(null, new InMemoryAuditTrail()));
        assertThrows(IllegalArgumentException.class,
                () -> new AuditedActionProcessor(new ScriptedProcessor(), null));
        assertThrows(IllegalArgumentException.class, () -> new InMemoryAuditTrail(0));
    }
}
