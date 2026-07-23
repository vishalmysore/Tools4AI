package com.t4a.agent.metrics;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MeteredActionProcessorTest {

    static class ScriptedProcessor implements AIProcessor {
        boolean throwError = false;
        long sleepMillis = 0;

        private Object attempt() throws AIProcessingException {
            if (sleepMillis > 0) {
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (throwError) throw new AIProcessingException("boom");
            return "tool result";
        }

        @Override public Object processSingleAction(String p) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, ActionCallback cb) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, HumanInLoop h, ExplainDecision e) throws AIProcessingException { return attempt(); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e, ActionCallback cb) throws AIProcessingException { return attempt(); }
        @Override public String query(String p) throws AIProcessingException { return (String) attempt(); }
    }

    @Test
    void testSuccessfulCallsAreCounted() throws AIProcessingException {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        MeteredActionProcessor metered = new MeteredActionProcessor(new ScriptedProcessor(), metrics);

        metered.processSingleAction("one");
        metered.processSingleAction("two");

        MetricsSnapshot snapshot = metrics.snapshot(MeteredActionProcessor.OPERATION_PROCESS);
        assertEquals(2, snapshot.getTotalCount());
        assertEquals(2, snapshot.getSuccessCount());
        assertEquals(0, snapshot.getFailureCount());
        assertEquals(1.0, snapshot.getSuccessRate(), 0.0001);
        assertEquals(0.0, snapshot.getErrorRate(), 0.0001);
    }

    @Test
    void testFailuresAreCountedAndRethrown() {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        ScriptedProcessor delegate = new ScriptedProcessor();
        delegate.throwError = true;
        MeteredActionProcessor metered = new MeteredActionProcessor(delegate, metrics);

        assertThrows(AIProcessingException.class, () -> metered.processSingleAction("boom"));

        MetricsSnapshot snapshot = metrics.snapshot(MeteredActionProcessor.OPERATION_PROCESS);
        assertEquals(1, snapshot.getTotalCount());
        assertEquals(0, snapshot.getSuccessCount());
        assertEquals(1, snapshot.getFailureCount());
        assertEquals(1.0, snapshot.getErrorRate(), 0.0001);
    }

    @Test
    void testQueryIsMeasuredSeparatelyFromActions() throws AIProcessingException {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        MeteredActionProcessor metered = new MeteredActionProcessor(new ScriptedProcessor(), metrics);

        metered.processSingleAction("act");
        metered.query("ask");

        assertEquals(1, metrics.snapshot(MeteredActionProcessor.OPERATION_PROCESS).getTotalCount());
        assertEquals(1, metrics.snapshot(MeteredActionProcessor.OPERATION_QUERY).getTotalCount());
        assertEquals(2, metrics.snapshotAll().size());
    }

    @Test
    void testAllProcessOverloadsShareOneOperationName() throws AIProcessingException {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        MeteredActionProcessor metered = new MeteredActionProcessor(new ScriptedProcessor(), metrics);

        metered.processSingleAction("a");
        metered.processSingleAction("b", (ActionCallback) null);
        metered.processSingleAction("c", null, null, null);
        metered.processSingleAction("d", (HumanInLoop) null, (ExplainDecision) null);
        metered.processSingleAction("e", null, null, null, null);

        assertEquals(5, metrics.snapshot(MeteredActionProcessor.OPERATION_PROCESS).getTotalCount());
    }

    @Test
    void testNamePrefixSeparatesProviders() throws AIProcessingException {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        new MeteredActionProcessor(new ScriptedProcessor(), metrics, "openai.").processSingleAction("a");
        new MeteredActionProcessor(new ScriptedProcessor(), metrics, "gemini.").processSingleAction("b");

        assertEquals(1, metrics.snapshot("openai.processSingleAction").getTotalCount());
        assertEquals(1, metrics.snapshot("gemini.processSingleAction").getTotalCount());
    }

    @Test
    void testDurationsAreRecorded() throws AIProcessingException {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        ScriptedProcessor delegate = new ScriptedProcessor();
        delegate.sleepMillis = 30;
        MeteredActionProcessor metered = new MeteredActionProcessor(delegate, metrics);

        metered.processSingleAction("slow");

        MetricsSnapshot snapshot = metrics.snapshot(MeteredActionProcessor.OPERATION_PROCESS);
        assertTrue(snapshot.getMaxDurationMs() >= 25,
                "expected the 30ms delay to be reflected, got " + snapshot.getMaxDurationMs());
        assertTrue(snapshot.getAverageDurationMs() >= 25);
    }

    @Test
    void testUnrecordedOperationReturnsEmptySnapshot() {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        MetricsSnapshot snapshot = metrics.snapshot("nothing-ran");

        assertEquals(0, snapshot.getTotalCount());
        assertEquals(0, snapshot.getMinDurationMs());
        assertEquals(0, snapshot.getMaxDurationMs());
        assertEquals(0.0, snapshot.getAverageDurationMs(), 0.0001);
        assertEquals(0.0, snapshot.getSuccessRate(), 0.0001);
    }

    @Test
    void testResetClearsEverything() throws AIProcessingException {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        MeteredActionProcessor metered = new MeteredActionProcessor(new ScriptedProcessor(), metrics);

        metered.processSingleAction("a");
        assertEquals(1, metrics.snapshotAll().size());

        metrics.reset();
        assertTrue(metrics.snapshotAll().isEmpty());
        assertEquals(0, metrics.snapshot(MeteredActionProcessor.OPERATION_PROCESS).getTotalCount());
    }

    @Test
    void testReportListsEveryOperation() throws AIProcessingException {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        MeteredActionProcessor metered = new MeteredActionProcessor(new ScriptedProcessor(), metrics);

        metered.processSingleAction("a");
        metered.query("b");

        String report = metrics.report();
        assertTrue(report.contains(MeteredActionProcessor.OPERATION_PROCESS));
        assertTrue(report.contains(MeteredActionProcessor.OPERATION_QUERY));
    }

    @Test
    void testConcurrentRecordingIsThreadSafe() throws InterruptedException {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        int threads = 8;
        int perThread = 250;

        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    metrics.record("concurrent", j % 2 == 0, 5);
                }
            });
            workers[i].start();
        }
        for (Thread worker : workers) worker.join();

        MetricsSnapshot snapshot = metrics.snapshot("concurrent");
        assertEquals((long) threads * perThread, snapshot.getTotalCount());
        assertEquals(snapshot.getTotalCount(), snapshot.getSuccessCount() + snapshot.getFailureCount());
    }

    @Test
    void testNullOperationNameIsBucketedAsUnknown() {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        metrics.record(null, true, 1);

        assertEquals(1, metrics.snapshot("unknown").getTotalCount());
    }

    @Test
    void testMetricsSinkIsExposed() {
        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        MeteredActionProcessor metered = new MeteredActionProcessor(new ScriptedProcessor(), metrics);

        assertSame(metrics, metered.getMetrics());
    }

    @Test
    void testCustomSinkReceivesMeasurements() throws AIProcessingException {
        Map<String, Long> counts = new java.util.concurrent.ConcurrentHashMap<>();
        ActionMetrics customSink = new ActionMetrics() {
            @Override public void record(String operation, boolean success, long durationMs) {
                counts.merge(operation, 1L, Long::sum);
            }
            @Override public MetricsSnapshot snapshot(String operation) { return null; }
            @Override public Map<String, MetricsSnapshot> snapshotAll() { return java.util.Collections.emptyMap(); }
            @Override public void reset() { counts.clear(); }
        };

        new MeteredActionProcessor(new ScriptedProcessor(), customSink).processSingleAction("a");

        assertEquals(1L, counts.get(MeteredActionProcessor.OPERATION_PROCESS));
    }

    @Test
    void testConstructorRejectsNulls() {
        assertThrows(IllegalArgumentException.class,
                () -> new MeteredActionProcessor(null, new InMemoryActionMetrics()));
        assertThrows(IllegalArgumentException.class,
                () -> new MeteredActionProcessor(new ScriptedProcessor(), null));
    }
}
