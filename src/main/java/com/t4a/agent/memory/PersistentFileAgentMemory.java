package com.t4a.agent.memory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Long-term agent memory that persists turns to a JSON file on disk.
 *
 * <p>Turns are loaded from the file at construction time and appended on every {@link #addTurn}
 * call.  The file is re-written atomically (write to a temp file then rename) so a crash during
 * the write does not corrupt the existing history.
 *
 * <p>Thread-safe — all public methods synchronise on {@code this}.
 *
 * <p>Example usage:
 * <pre>{@code
 * AgentMemory memory = new PersistentFileAgentMemory("/var/agent/session-abc.json");
 * memory.addTurn("Book flight to Bangalore", "Flight BLR-001 confirmed");
 * // history survives JVM restart — next session loads it back
 * }</pre>
 */
@Slf4j
public class PersistentFileAgentMemory implements AgentMemory {

    private final File storageFile;
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<PersistentTurn> turns;

    /** Simple DTO that Jackson can serialise/deserialise without Lombok. */
    public static class PersistentTurn {
        public String prompt;
        public String result;
        public long timestampMs;

        // required by Jackson
        public PersistentTurn() {}

        public PersistentTurn(String prompt, Object result, long timestampMs) {
            this.prompt = prompt;
            this.result = result == null ? null : result.toString();
            this.timestampMs = timestampMs;
        }
    }

    public PersistentFileAgentMemory(String filePath) {
        this.storageFile = new File(filePath);
        this.turns = loadFromFile();
        log.info("PersistentFileAgentMemory initialised from '{}' ({} existing turns)",
                storageFile.getAbsolutePath(), turns.size());
    }

    @Override
    public synchronized void addTurn(String prompt, Object result) {
        turns.add(new PersistentTurn(prompt, result, System.currentTimeMillis()));
        persist();
        log.debug("Persisted turn (total={}): '{}'", turns.size(), prompt);
    }

    @Override
    public synchronized List<MemoryTurn> getHistory() {
        List<MemoryTurn> out = new ArrayList<>();
        for (PersistentTurn pt : turns) {
            out.add(new MemoryTurn(pt.prompt, pt.result));
        }
        return Collections.unmodifiableList(out);
    }

    @Override
    public synchronized String getHistoryAsContext() {
        if (turns.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("[Previous context]\n");
        int i = 1;
        for (PersistentTurn t : turns) {
            sb.append("Turn ").append(i++).append(" - Prompt: \"").append(t.prompt)
              .append("\" | Result: \"").append(t.result).append("\"\n");
        }
        sb.append("[End context]\n");
        return sb.toString();
    }

    @Override
    public synchronized void clear() {
        turns.clear();
        persist();
        log.debug("Persistent memory cleared and file updated");
    }

    @Override
    public synchronized int size() {
        return turns.size();
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private List<PersistentTurn> loadFromFile() {
        if (!storageFile.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(storageFile, new TypeReference<List<PersistentTurn>>() {});
        } catch (IOException e) {
            log.warn("Could not read memory file '{}', starting fresh: {}", storageFile, e.getMessage());
            return new ArrayList<>();
        }
    }

    private void persist() {
        try {
            File tmp = new File(storageFile.getParentFile() == null
                    ? storageFile.getName() + ".tmp"
                    : storageFile.getParent() + File.separator + storageFile.getName() + ".tmp");
            mapper.writeValue(tmp, turns);
            if (!tmp.renameTo(storageFile)) {
                // rename failed (e.g. cross-device) — fall back to direct write
                mapper.writeValue(storageFile, turns);
                if (!tmp.delete()) {
                    log.warn("Could not delete temporary file '{}'", tmp.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            log.error("Failed to persist memory to '{}': {}", storageFile, e.getMessage());
        }
    }
}
