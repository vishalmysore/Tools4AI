package com.t4a.agent.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Append-only audit trail stored as JSON-lines (one JSON object per line).
 *
 * <p>Appending a line is atomic enough for an audit log and never rewrites earlier records,
 * so a crash cannot corrupt history that has already been written. The file can be tailed,
 * grepped, or bulk-loaded into any log pipeline.
 *
 * <p>Example:
 * <pre>{@code
 * AuditTrail trail = new JsonFileAuditTrail("/var/agent/audit.jsonl");
 * AIProcessor audited = new AuditedActionProcessor(new OpenAiActionProcessor(), trail);
 * }</pre>
 */
@Slf4j
public class JsonFileAuditTrail implements AuditTrail {

    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonFileAuditTrail(String filePath) {
        this.file = new File(filePath);
    }

    @Override
    public synchronized void record(AuditEntry entry) {
        try {
            String line = mapper.writeValueAsString(entry) + System.lineSeparator();
            Files.write(file.toPath(), line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Failed to append audit entry to '{}': {}", file, e.getMessage());
        }
    }

    @Override
    public synchronized List<AuditEntry> getEntries() {
        List<AuditEntry> out = new ArrayList<>();
        if (!file.exists()) return out;
        try {
            for (String line : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)) {
                if (line.trim().isEmpty()) continue;
                out.add(mapper.readValue(line, AuditEntry.class));
            }
        } catch (IOException e) {
            log.warn("Could not read audit file '{}': {}", file, e.getMessage());
        }
        return out;
    }
}
