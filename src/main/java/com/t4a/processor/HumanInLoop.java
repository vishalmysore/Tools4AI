package com.t4a.processor;

import java.util.Map;

/**
 * The {@code HumanInLoop} interface represents a mechanism for allowing human involvement
 * in a feedback loop process.
 * <p>
 * This interface defines a method {@link #allow(String, String, Map)} that can be used
 * to request human input for a given prompt text and method name with optional parameters.
 * </p>
 */
public interface HumanInLoop {
    public FeedbackLoop allow(String promptText, String methodName, Map<String, Object> params) ;
    public FeedbackLoop allow(String promptText, String methodName, String params) ;
}
