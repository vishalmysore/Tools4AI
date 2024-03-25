package com.t4a.processor;

import com.t4a.api.AIAction;

public class LocalAIActionProcessor implements AIProcessor {
    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) {
        return null;
    }

    @Override
    public Object processSingleAction(String promptText, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
        return null;
    }
}
