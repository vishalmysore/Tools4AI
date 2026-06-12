package com.t4a.agent.audit;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * Decorator around any {@link AIProcessor} that records every action execution — prompt,
 * result, success/failure, and duration — to an {@link AuditTrail}.
 *
 * <p>This gives agentic applications the persistent "who asked what, what ran, what came back"
 * record that compliance and debugging both need, without touching action code:
 * <pre>{@code
 * AuditTrail trail = new JsonFileAuditTrail("/var/agent/audit.jsonl");
 * AIProcessor audited = new AuditedActionProcessor(new OpenAiActionProcessor(), trail);
 *
 * audited.processSingleAction("Restart the payment server");
 * // audit.jsonl now contains:
 * // {"timestampMs":...,"prompt":"Restart the payment server","result":"...","success":true,...}
 * }</pre>
 *
 * <p>Failures are recorded too (with the exception message) and then rethrown unchanged.
 * Composes with the other decorators — e.g. wrap a
 * {@code RetryActionProcessor} to audit the final outcome after retries.
 */
@Slf4j
public class AuditedActionProcessor implements AIProcessor {

    private final AIProcessor delegate;
    private final AuditTrail trail;

    public AuditedActionProcessor(AIProcessor delegate, AuditTrail trail) {
        if (delegate == null) throw new IllegalArgumentException("delegate must not be null");
        if (trail == null) throw new IllegalArgumentException("trail must not be null");
        this.delegate = delegate;
        this.trail = trail;
    }

    @FunctionalInterface
    private interface Invocation<T> {
        T run() throws AIProcessingException;
    }

    private <T> T audited(String prompt, Invocation<T> invocation) throws AIProcessingException {
        long start = System.currentTimeMillis();
        try {
            T result = invocation.run();
            trail.record(new AuditEntry(start, prompt, result, true, null,
                    System.currentTimeMillis() - start));
            return result;
        } catch (AIProcessingException | RuntimeException e) {
            trail.record(new AuditEntry(start, prompt, null, false, e.getMessage(),
                    System.currentTimeMillis() - start));
            throw e;
        }
    }

    @Override
    public Object processSingleAction(String promptText) throws AIProcessingException {
        return audited(promptText, () -> delegate.processSingleAction(promptText));
    }

    @Override
    public Object processSingleAction(String promptText, ActionCallback callback) throws AIProcessingException {
        return audited(promptText, () -> delegate.processSingleAction(promptText, callback));
    }

    @Override
    public Object processSingleAction(String promptText, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        return audited(promptText, () -> delegate.processSingleAction(promptText, action, humanVerification, explain));
    }

    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        return audited(promptText, () -> delegate.processSingleAction(promptText, humanVerification, explain));
    }

    @Override
    public Object processSingleAction(String prompt, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain,
                                      ActionCallback callback) throws AIProcessingException {
        return audited(prompt, () -> delegate.processSingleAction(prompt, action, humanVerification, explain, callback));
    }

    @Override
    public String query(String promptText) throws AIProcessingException {
        return audited(promptText, () -> delegate.query(promptText));
    }
}
