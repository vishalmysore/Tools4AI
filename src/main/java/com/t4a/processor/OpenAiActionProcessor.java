package com.t4a.processor;

import com.google.gson.Gson;
import com.t4a.JsonUtils;
import com.t4a.api.*;
import com.t4a.api.AIPlatform;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.predict.PredictionLoader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

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


    @Override
    public String query(String promptText) throws AIProcessingException {
        return PredictionLoader.getInstance().getOpenAiChatModel().generate(promptText);
    }
    public OpenAiActionProcessor() {
        this.gson = new Gson();
    }

    @Override
    public Object processSingleAction(String promptText, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
        return processSingleAction(promptText, action, humanVerification, explain, null);
    }

    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
        return processSingleAction(promptText,null,humanVerification,explain);
    }

    public Object processSingleAction(String promptText, AIAction action)  throws AIProcessingException {
        return processSingleAction(promptText, action, null,null);
    }
    public Object processSingleAction(String promptText, String actionName)  throws AIProcessingException {
        AIAction action = PredictionLoader.getInstance().getAiAction(actionName);
        if(action == null) {
            throw new AIProcessingException(" action not found "+actionName);
        }
        return processSingleAction(promptText, action, null,null);
    }



    public Object processSingleAction(String prompt, AIAction action, HumanInLoop humanVerification, ExplainDecision explain, ActionCallback callback) throws AIProcessingException {
        if (action == null) {
            action = PredictionLoader.getInstance().getPredictedAction(prompt, AIPlatform.OPENAI);
            if(action == null) {
              return  "no action found for the prompt "+prompt;
            }
            if(action.getActionRisk() == ActionRisk.HIGH) {
                log.warn(" This is a high risk action needs to be explicitly provided by human operator cannot be predicted by AI "+action.getActionName());
                return "This is a high risk action will not proceed "+action.getActionName();
            }
        }
        if(action.getActionType() == ActionType.JAVAMETHOD) {
            log.debug(action + "");
            JsonUtils utils = new JsonUtils();
            Method m = null;
            JavaMethodAction javaMethodAction = (JavaMethodAction) action;
            try {
                setCallBack(callback,javaMethodAction);
            } catch (IllegalAccessException e) {
                throw new AIProcessingException(e);
            }
            setProcessor(javaMethodAction);
            Class<?> clazz = javaMethodAction.getActionClass();
            Method[] methods = clazz.getMethods();
            for (Method m1 : methods
            ) {
                if (m1.getName().equals(javaMethodAction.getActionName())) {
                    m = m1;
                    break;
                }
            }
            if(m== null) {
                throw new AIProcessingException("Method name should matches the actionName  "+action.getActionName()+" class "+action.getClass().getName());
            }
            String jsonStr = utils.convertMethodTOJsonString(m);
            try {
                jsonStr = PredictionLoader.getInstance().getOpenAiChatModel().generate(" Here is your prompt {" + prompt + "} - here is the json - " + jsonStr + " "+PredictionLoader.getInstance().METHODTOJSONOI);
            }
            catch(Exception e) {
             throw new AIProcessingException(" Make sure openAiKey is set either in tools4Ai.properties or as runtime parameter -DopenAiKey=  "+e.getMessage());
            }
            log.info(jsonStr);
            JavaMethodInvoker invoke = new JavaMethodInvoker();
            Object[] obj = invoke.parse(jsonStr);
            List<Object> parameterValues = (List<Object>) obj[1];
            List<Class<?>> parameterTypes = (List<Class<?>>) obj[0];
            Method method;
            try {
                method = clazz.getMethod(m.getName(), parameterTypes.toArray(new Class<?>[0]));
            } catch (NoSuchMethodException e) {
                throw new AIProcessingException(e);
            }

            Object result;
            try {
                if(humanVerification!=null && humanVerification.allow(prompt, action.getActionName(), jsonStr).isAIResponseValid()) {
                    result = invokeReflection(method, javaMethodAction, parameterValues);
                } else {
                    return "Human verification failed";
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new AIProcessingException(e);
            }
            return result;
        } else {
            log.debug(action+"");
            JavaMethodExecutor methodExecutor = new JavaMethodExecutor();
            methodExecutor.mapMethod(action);
            log.debug(methodExecutor.getProperties()+"");
            String params = PredictionLoader.getInstance().getActionParams(action,prompt,AIPlatform.OPENAI,methodExecutor.getProperties());
            Object obj = null;
            try {
                if(humanVerification.allow(prompt, action.getActionName(), params).isAIResponseValid()) {
                    obj = methodExecutor.action(params, action);
                    log.debug(" the action is returning "+obj);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AIProcessingException(e);
            }
            return obj;

        }
    }



    public Object processSingleNonJava(String prompt, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
        if(action == null) {
            action = PredictionLoader.getInstance().getPredictedAction(prompt, AIPlatform.OPENAI);
        }
        log.debug(action+"");
        JavaMethodExecutor methodExecutor = new JavaMethodExecutor();


        methodExecutor.mapMethod(action);
        Object objReturnFromAI = null;
        if(action.getActionType() == ActionType.JAVAMETHOD)  {
            if(((JavaMethodAction)action).isComplexMethod()) {
                         Object[] params =   PredictionLoader.getInstance().getComplexActionParams(prompt, methodExecutor.getProperties(),gson );
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
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new AIProcessingException(e);
                }
            }
        }

        return PredictionLoader.getInstance().postActionProcessing(prompt, (String)objReturnFromAI);
    }
    public Object processSingleAction(String prompt) throws AIProcessingException{
        return processSingleAction(prompt, new LoggingHumanDecision(),new LogginggExplainDecision());
    }

    @Override
    public Object processSingleAction(String promptText, ActionCallback callback) throws AIProcessingException {
        return processSingleAction(promptText,null, null,null,callback);
    }
}
