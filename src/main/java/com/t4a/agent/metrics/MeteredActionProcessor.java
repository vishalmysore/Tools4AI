package com.t4a.agent.metrics;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * Decorator around any {@link AIProcessor} that times every call and reports the outcome to an
 * {@link ActionMetrics} sink.
 *
 * <p>Agentic systems fail in ways that only show up in aggregate — a provider that got slower,
 * an action that started throwing after a deploy. This decorator gives you latency and error
 * rates per operation without touching action code:
 *
 * <pre>{@code
 * InMemoryActionMetrics metrics = new InMemoryActionMetrics();
 * AIProcessor metered = new MeteredActionProcessor(new OpenAiActionProcessor(), metrics);
 *
 * metered.processSingleAction("Book a flight to Bangalore");
 * metered.query("Summarise today's bookings");
 *
 * System.out.println(metrics.report());
 * }</pre>
 *
 * <p>Measurements are recorded for both successful and failed calls; failures are recorded and
 * then rethrown unchanged. Pass a {@code namePrefix} when several processors share one metrics
 * sink so each provider's numbers stay separate — {@code "openai."} yields
 * {@code openai.processSingleAction}.
 *
 * <p>Where it sits in a decorator stack changes what it measures: wrapping a
 * {@code RetryActionProcessor} measures the total time including retries, wrapping the raw
 * processor measures each individual attempt.
 */
@Slf4j
public class MeteredActionProcessor implements AIProcessor {

    /** Operation name recorded for every {@code processSingleAction} overload. */
    public static final String OPERATION_PROCESS = "processSingleAction";
    /** Operation name recorded for {@code query}. */
    public static final String OPERATION_QUERY = "query";

    private final AIProcessor delegate;
    private final ActionMetrics metrics;
    private final String namePrefix;

    public MeteredActionProcessor(AIProcessor delegate, ActionMetrics metrics) {
        this(delegate, metrics, "");
    }

    /**
     * @param delegate   the real processor to call
     * @param metrics    sink the measurements are reported to
     * @param namePrefix prepended to every operation name; {@code null} is treated as empty
     */
    public MeteredActionProcessor(AIProcessor delegate, ActionMetrics metrics, String namePrefix) {
        if (delegate == null) throw new IllegalArgumentException("delegate must not be null");
        if (metrics == null) throw new IllegalArgumentException("metrics must not be null");
        this.delegate = delegate;
        this.metrics = metrics;
        this.namePrefix = namePrefix == null ? "" : namePrefix;
    }

    /** The sink this processor reports to, so callers can read snapshots without holding a second reference. */
    public ActionMetrics getMetrics() {
        return metrics;
    }

    @FunctionalInterface
    private interface Invocation<T> {
        T run() throws AIProcessingException;
    }

    private <T> T measured(String operation, Invocation<T> invocation) throws AIProcessingException {
        long start = System.currentTimeMillis();
        boolean success = false;
        try {
            T result = invocation.run();
            success = true;
            return result;
        } finally {
            metrics.record(namePrefix + operation, success, System.currentTimeMillis() - start);
        }
    }

    @Override
    public Object processSingleAction(String promptText) throws AIProcessingException {
        return measured(OPERATION_PROCESS, () -> delegate.processSingleAction(promptText));
    }

    @Override
    public Object processSingleAction(String promptText, ActionCallback callback) throws AIProcessingException {
        return measured(OPERATION_PROCESS, () -> delegate.processSingleAction(promptText, callback));
    }

    @Override
    public Object processSingleAction(String promptText, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        return measured(OPERATION_PROCESS, () -> delegate.processSingleAction(promptText, action, humanVerification, explain));
    }

    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        return measured(OPERATION_PROCESS, () -> delegate.processSingleAction(promptText, humanVerification, explain));
    }

    @Override
    public Object processSingleAction(String prompt, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain,
                                      ActionCallback callback) throws AIProcessingException {
        return measured(OPERATION_PROCESS, () -> delegate.processSingleAction(prompt, action, humanVerification, explain, callback));
    }

    @Override
    public String query(String promptText) throws AIProcessingException {
        return measured(OPERATION_QUERY, () -> delegate.query(promptText));
    }
}
