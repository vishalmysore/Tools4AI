package com.t4a.agent.safety;

import com.t4a.api.AIAction;
import com.t4a.api.ActionRisk;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.FeedbackLoop;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * Decorator around any {@link AIProcessor} that actually <em>enforces</em> the
 * {@link ActionRisk} contract before the action is allowed to run.
 *
 * <p>{@code AIAction.getActionRisk()} has always declared the intent — LOW runs freely,
 * MEDIUM needs one human verification, HIGH needs two — but nothing in the framework held
 * the action back; the caller had to remember to check. This decorator makes the gate
 * mandatory for everything routed through it:
 *
 * <pre>
 *   LOW    → executes immediately
 *   MEDIUM → one approval from the primary approver, else {@link ActionBlockedException}
 *   HIGH   → two approvals (primary, then secondary), else {@link ActionBlockedException}
 * </pre>
 *
 * <p>Example — dual control on high-risk actions:
 * <pre>{@code
 * AIProcessor gated = new RiskGatedActionProcessor(
 *         new OpenAiActionProcessor(),
 *         onCallEngineerApprover,     // first approval
 *         securityOfficerApprover);   // second approval, HIGH risk only
 *
 * gated.processSingleAction("Delete the customer database", dropDbAction, approver, explain);
 * // → ActionBlockedException if either approver declines
 * }</pre>
 *
 * <p>When only a prompt is supplied and no {@link AIAction} is available, the risk of the
 * action that will eventually be selected is not yet known. That case is governed by
 * {@code riskWhenUnknown}, which defaults to {@link ActionRisk#LOW} so wrapping a processor
 * never silently changes existing behaviour. Set it to {@link ActionRisk#MEDIUM} or
 * {@link ActionRisk#HIGH} for a fail-closed posture where every free-text prompt is reviewed.
 *
 * <p>Composes with the other decorators — a typical production stack is
 * {@code audited(risk-gated(retrying(processor)))} so the audit trail records both the
 * approval outcome and the final result.
 */
@Slf4j
public class RiskGatedActionProcessor implements AIProcessor {

    private final AIProcessor delegate;
    private final HumanInLoop primaryApprover;
    private final HumanInLoop secondaryApprover;
    private final ActionRisk riskWhenUnknown;

    /** Gate with a single approver — HIGH risk actions ask that same approver twice. */
    public RiskGatedActionProcessor(AIProcessor delegate, HumanInLoop approver) {
        this(delegate, approver, null, ActionRisk.LOW);
    }

    /** Gate with dual control — the secondary approver provides the second HIGH-risk approval. */
    public RiskGatedActionProcessor(AIProcessor delegate, HumanInLoop primaryApprover, HumanInLoop secondaryApprover) {
        this(delegate, primaryApprover, secondaryApprover, ActionRisk.LOW);
    }

    /**
     * @param delegate          the real processor to call once approvals pass
     * @param primaryApprover   asked for the first approval; must not be {@code null}
     * @param secondaryApprover asked for the second approval on HIGH risk actions; when
     *                          {@code null} the primary approver is asked a second time
     * @param riskWhenUnknown   risk assumed when the call supplies no {@link AIAction} to read
     *                          {@code getActionRisk()} from; {@code null} is treated as LOW
     */
    public RiskGatedActionProcessor(AIProcessor delegate, HumanInLoop primaryApprover,
                                    HumanInLoop secondaryApprover, ActionRisk riskWhenUnknown) {
        if (delegate == null) throw new IllegalArgumentException("delegate must not be null");
        if (primaryApprover == null) throw new IllegalArgumentException("primaryApprover must not be null");
        this.delegate = delegate;
        this.primaryApprover = primaryApprover;
        this.secondaryApprover = secondaryApprover;
        this.riskWhenUnknown = riskWhenUnknown == null ? ActionRisk.LOW : riskWhenUnknown;
    }

    /** Number of human approvals the contract requires for a given risk level. */
    public static int approvalsRequired(ActionRisk risk) {
        if (risk == null) return 0;
        switch (risk) {
            case HIGH:   return 2;
            case MEDIUM: return 1;
            default:     return 0;
        }
    }

    private void gate(String prompt, AIAction action) throws AIProcessingException {
        ActionRisk risk = action != null ? action.getActionRisk() : riskWhenUnknown;
        if (risk == null) risk = ActionRisk.LOW;

        int required = approvalsRequired(risk);
        if (required == 0) return;

        String actionName = action != null ? action.getActionName() : "unknown";
        String params = action != null ? action.getActionParameters() : "";

        for (int approval = 1; approval <= required; approval++) {
            HumanInLoop approver = (approval == 2 && secondaryApprover != null) ? secondaryApprover : primaryApprover;
            FeedbackLoop decision = approver.allow(prompt, actionName, params);
            if (decision == null || !decision.isAIResponseValid()) {
                log.warn("{} risk action '{}' blocked at approval {}/{}", risk, actionName, approval, required);
                throw new ActionBlockedException(
                        risk + " risk action '" + actionName + "' was declined at approval "
                                + approval + " of " + required, risk, actionName);
            }
            log.info("{} risk action '{}' cleared approval {}/{}", risk, actionName, approval, required);
        }
    }

    @Override
    public Object processSingleAction(String promptText) throws AIProcessingException {
        gate(promptText, null);
        return delegate.processSingleAction(promptText);
    }

    @Override
    public Object processSingleAction(String promptText, ActionCallback callback) throws AIProcessingException {
        gate(promptText, null);
        return delegate.processSingleAction(promptText, callback);
    }

    @Override
    public Object processSingleAction(String promptText, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        gate(promptText, action);
        return delegate.processSingleAction(promptText, action, humanVerification, explain);
    }

    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification,
                                      ExplainDecision explain) throws AIProcessingException {
        gate(promptText, null);
        return delegate.processSingleAction(promptText, humanVerification, explain);
    }

    @Override
    public Object processSingleAction(String prompt, AIAction action,
                                      HumanInLoop humanVerification,
                                      ExplainDecision explain,
                                      ActionCallback callback) throws AIProcessingException {
        gate(prompt, action);
        return delegate.processSingleAction(prompt, action, humanVerification, explain, callback);
    }

    /** Queries never execute an action, so they are passed straight through without a gate. */
    @Override
    public String query(String promptText) throws AIProcessingException {
        return delegate.query(promptText);
    }
}
