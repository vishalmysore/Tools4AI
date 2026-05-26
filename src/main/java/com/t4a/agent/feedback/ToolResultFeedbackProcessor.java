package com.t4a.agent.feedback;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Decorator around any {@link AIProcessor} that feeds the tool result back to the LLM
 * for a follow-up reasoning step.
 *
 * <p>Without this decorator the flow is:
 * <pre>
 *   prompt → [LLM picks action] → [Java method executes] → raw result returned to caller
 * </pre>
 *
 * <p>With this decorator:
 * <pre>
 *   prompt → [LLM picks action] → [Java method executes] → raw result
 *          → [LLM synthesises answer from prompt + raw result] → enriched answer returned
 * </pre>
 *
 * <p>This is the "tool result feedback" step that lets the model say <em>"I called
 * {@code getWeather("Toronto")} and got 22 °C, so the answer to your question is: it's a
 * pleasant 22 °C in Toronto today."</em> instead of returning the bare value {@code "22"}.
 *
 * <p>Example:
 * <pre>{@code
 * AIProcessor raw      = new OpenAiActionProcessor();
 * AIProcessor enriched = new ToolResultFeedbackProcessor(raw);
 *
 * // Returns a natural-language sentence, not the bare object
 * String answer = (String) enriched.processSingleAction("What is the weather in Toronto?");
 * }</pre>
 *
 * <p>All other {@link AIProcessor} methods are transparently delegated to the wrapped instance.
 */
@Slf4j
public class ToolResultFeedbackProcessor implements AIProcessor {

    private final AIProcessor delegate;

    /**
     * @param delegate the real {@link AIProcessor} that performs action selection and execution
     */
    public ToolResultFeedbackProcessor(AIProcessor delegate) {
        if (delegate == null) throw new IllegalArgumentException("delegate must not be null");
        this.delegate = delegate;
    }

    /**
     * Execute the action and feed the raw result back to the LLM for synthesis.
     *
     * @return a natural-language answer rather than the bare tool return value
     */
    @Override
    public Object processSingleAction(String prompt) throws AIProcessingException {
        Object rawResult = delegate.processSingleAction(prompt);
        return synthesise(prompt, rawResult);
    }

    @Override
    public Object processSingleAction(String promptText, ActionCallback callback) throws AIProcessingException {
        Object rawResult = delegate.processSingleAction(promptText, callback);
        return synthesise(promptText, rawResult);
    }

    @Override
    public Object processSingleAction(String promptText, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        Object rawResult = delegate.processSingleAction(promptText, action, humanVerification, explain);
        return synthesise(promptText, rawResult);
    }

    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        Object rawResult = delegate.processSingleAction(promptText, humanVerification, explain);
        return synthesise(promptText, rawResult);
    }

    @Override
    public Object processSingleAction(String prompt, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain,
                                      ActionCallback callback) throws AIProcessingException {
        Object rawResult = delegate.processSingleAction(prompt, action, humanVerification, explain, callback);
        return synthesise(prompt, rawResult);
    }

    /** Delegates directly — no feedback wrapping needed for plain queries. */
    @Override
    public String query(String promptText) throws AIProcessingException {
        return delegate.query(promptText);
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    /**
     * Send prompt + raw tool result back to the LLM and return the enriched answer.
     * Falls back to {@code rawResult} unchanged if synthesis itself fails.
     */
    private Object synthesise(String prompt, Object rawResult) {
        if (rawResult == null) return null;
        log.debug("Feeding tool result back to LLM for synthesis. rawResult={}", rawResult);
        try {
            return delegate.query(prompt, rawResult);
        } catch (AIProcessingException e) {
            log.warn("Tool-result synthesis failed ({}), returning raw result", e.getMessage());
            return rawResult;
        }
    }
}
