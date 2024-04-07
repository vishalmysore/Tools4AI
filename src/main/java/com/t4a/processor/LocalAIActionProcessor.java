package com.t4a.processor;

import com.t4a.api.AIAction;

public class LocalAIActionProcessor implements AIProcessor {
    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) {
        return null;
    }

    @Override
    public String query(String promptText) throws AIProcessingException {
        return null;
    }

    @Override
    public Object processSingleAction(String promptText, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
        return null;
    }
    public Object processSingleAction(String promptText)  throws AIProcessingException {
        return processSingleAction(promptText, null, new LoggingHumanDecision(), new LogginggExplainDecision());
    }
}
