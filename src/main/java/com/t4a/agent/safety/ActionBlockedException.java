package com.t4a.agent.safety;

import com.t4a.api.ActionRisk;
import com.t4a.processor.AIProcessingException;

/**
 * Thrown by {@link RiskGatedActionProcessor} when a human approver declines a MEDIUM or HIGH
 * risk action, or when an action is blocked outright by policy.
 *
 * <p>It is an {@link AIProcessingException} so existing {@code catch} blocks keep working, but
 * callers that care about the difference between "the LLM failed" and "a human said no" can
 * catch this subtype and carry the risk level and action name into their own audit records.
 */
public class ActionBlockedException extends AIProcessingException {

    private final ActionRisk risk;
    private final String actionName;

    public ActionBlockedException(String message, ActionRisk risk, String actionName) {
        super(message);
        this.risk = risk;
        this.actionName = actionName;
    }

    /** Risk level of the action that was blocked. */
    public ActionRisk getRisk() {
        return risk;
    }

    /** Name of the action that was blocked, or {@code "unknown"} when no action was supplied. */
    public String getActionName() {
        return actionName;
    }
}
