package com.t4a.processor;

import com.google.gson.Gson;
import com.t4a.api.AIAction;
import com.t4a.api.ActionType;
import com.t4a.api.JavaMethodAction;
import com.t4a.api.JavaMethodExecutor;
import com.t4a.predict.AIPlatform;
import com.t4a.predict.PredictionLoader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * Uses Json conversion to convert method and java pojo to jsons and then  call the openai , your objects have complex
 * parameters like custom dates etc then its better to pass a custom GSON , otherwise it will use the default gson
 */
@Slf4j
public class OpenAiActionProcessor implements AIProcessor{
    private Gson gson ;
    public OpenAiActionProcessor(Gson gson) {

        this.gson = gson;
    }

    public OpenAiActionProcessor() {
        this.gson = new Gson();
    }

    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
        return processSingleAction(promptText,null,humanVerification,explain);
    }

    public Object processSingleAction(String prompt, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
        if(action == null) {
            action = PredictionLoader.getInstance().getPredictedAction(prompt, AIPlatform.OPENAI);
        }
        log.debug(action+"");
        JavaMethodExecutor methodExecutor = new JavaMethodExecutor();


        methodExecutor.mapMethod(action);
        Object objReturnFromAI = null;
        if(action.getActionType() == ActionType.JAVAMETHOD)  {
            if(((JavaMethodAction)action).isComplexMethod()) {
                         Object[] params =   PredictionLoader.getInstance().getComplexActionParams(action,prompt,AIPlatform.OPENAI,methodExecutor.getProperties(),gson );
                objReturnFromAI = methodExecutor.action(params, action);
                log.debug(" the action returned "+objReturnFromAI);
            } else {
                log.debug(methodExecutor.getProperties()+"");
                String params = PredictionLoader.getInstance().getActionParams(action,prompt,AIPlatform.OPENAI,methodExecutor.getProperties());

                try {
                    if(humanVerification.allow(prompt, action.getActionName(), params).isAIResponseValid()) {
                        objReturnFromAI = methodExecutor.action(params, action);
                        log.debug(" the action returned "+objReturnFromAI);
                    }
                } catch (InvocationTargetException e) {
                    throw new AIProcessingException(e);
                } catch (IllegalAccessException e) {
                    throw new AIProcessingException(e);
                }
            }
        }

        return PredictionLoader.getInstance().postActionProcessing(action,prompt,AIPlatform.OPENAI,methodExecutor.getProperties(),(String)objReturnFromAI);
    }
    public Object processSingleAction(String prompt) throws AIProcessingException{
        return processSingleAction(prompt, new LoggingHumanDecision(),new LogginggExplainDecision());
    }
}
