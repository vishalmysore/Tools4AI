package com.t4a.processor;

import com.t4a.api.AIAction;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;

/**
 * Take actions based on images
 *
 */
public class ImageActionProcessor implements AIProcessor{
    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) {
        return null;
    }
    public Object processSingleAction(String promptText, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) {
        return null;
    }
    public Object processSingleAction(String promptText)  throws AIProcessingException {
        return processSingleAction(promptText, null, new LoggingHumanDecision(), new LogginggExplainDecision());
    }

    @Override
    public String query(String promptText) throws AIProcessingException {
        return null;
    }
}
