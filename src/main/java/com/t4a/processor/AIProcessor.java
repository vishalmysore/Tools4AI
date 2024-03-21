package com.t4a.processor;

public interface AIProcessor {
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException;
}
