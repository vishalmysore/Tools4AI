package com.t4a.processor;

import com.t4a.api.AIAction;
import com.t4a.api.JavaMethodExecutor;
import com.t4a.predict.AIPlatform;
import com.t4a.predict.PredictionLoader;
import lombok.extern.java.Log;

import java.lang.reflect.InvocationTargetException;

@Log
public class OpenAiActionProcessor implements AIProcessor{

    public Object processSingleAction(String prompt,HumanInLoop humanVerification, ExplainDecision explain) {
        AIAction action = PredictionLoader.getInstance().getPredictedAction(prompt, AIPlatform.OPENAI);
        log.info(action+"");
        JavaMethodExecutor methodExecutor = new JavaMethodExecutor();
        methodExecutor.mapMethod(action);
        log.info(methodExecutor.getProperties()+"");
        String params = PredictionLoader.getInstance().getActionParams(action,prompt,AIPlatform.OPENAI,methodExecutor.getProperties());
        Object obj = null;
        try {
            if(humanVerification.allow(prompt, action.getActionName(), params).isAIResponseValid()) {
                obj = methodExecutor.action(params, action);
                log.info(" the action returned "+obj);
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return PredictionLoader.getInstance().postActionProcessing(action,prompt,AIPlatform.OPENAI,methodExecutor.getProperties(),(String)obj);
    }
    public Object processSingleAction(String prompt) {
        return processSingleAction(prompt, new LoggingHumanDecision(),new LogginggExplainDecision());
    }
}
