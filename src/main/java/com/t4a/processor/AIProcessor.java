package com.t4a.processor;

import com.t4a.JsonUtils;
import com.t4a.api.AIAction;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;

public interface AIProcessor {
    public Object processSingleAction(String promptText, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException;
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException;
    public Object processSingleAction(String promptText)  throws AIProcessingException;
    public String query(String promptText)  throws AIProcessingException;
    public default String query(String question, String answer) throws AIProcessingException {
        return query(" this was my question { "+ question+"} context - "+answer);
    }
    public default String query(String question, Object answerObj) throws AIProcessingException {
        String answer = JsonUtils.convertObjectToJson(answerObj);
        return query(" this was my question { "+ question+"} context - "+answer);
    }
    public default String summarize(String prompt) throws AIProcessingException {
        return query(" Summarize this { "+ prompt+"}" );
    }
    public default Object processSingleAction(String promptText, Object actionInstance, String actionName) throws AIProcessingException {
        GenericJavaMethodAction action = new GenericJavaMethodAction(actionInstance, actionName);
       return processSingleAction(promptText, action, new LoggingHumanDecision(), new LogginggExplainDecision());
    }

    public default Object processSingleAction(String promptText, Object actionInstance) throws AIProcessingException {
        GenericJavaMethodAction action = new GenericJavaMethodAction(actionInstance);
        return processSingleAction(promptText, action, new LoggingHumanDecision(), new LogginggExplainDecision());
    }
}
