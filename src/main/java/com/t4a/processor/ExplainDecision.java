package com.t4a.processor;
/**
 * The {@code ExplainDecision} interface represents a mechanism for AI to explain decisions
 * regarding a particular prompt text, method name, and reason. AI will call this back
 * <p>
 * This interface defines a method {@link #explain(String, String, String)} that can be used
 * to provide an explanation by AI to a human regarding a decision made based on a prompt text,
 * method name, and reason.
 * </p>
 */
public interface ExplainDecision {
    public void explain(String promptText, String methodName, String reason) ;
}
