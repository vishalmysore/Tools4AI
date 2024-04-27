package com.t4a.processor;

import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;
import com.t4a.JsonUtils;
import com.t4a.api.*;
import com.t4a.api.AIPlatform;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.predict.PredictionLoader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
@Slf4j
public class GeminiV2ActionProcessor implements AIProcessor{
    private Gson gson ;
    public GeminiV2ActionProcessor(Gson gson) {

        this.gson = gson;
    }


    @Override
    public String query(String promptText) throws AIProcessingException {
        return PredictionLoader.getInstance().getOpenAiChatModel().generate(promptText);
    }
    public GeminiV2ActionProcessor() {
        this.gson = new Gson();
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
    public Object processSingleAction(String prompt, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
        if (action == null) {
            action = PredictionLoader.getInstance().getPredictedAction(prompt, AIPlatform.GEMINI);
            if(action.getActionRisk() == ActionRisk.HIGH) {
                log.warn(" This is a high risk action needs to be explicitly provided by human operator cannot be predicted by AI, {} ",action.getActionName());
                return "This is a high risk action will not proceed "+action.getActionName();
            }
        }
        if(action.getActionType() == ActionType.JAVAMETHOD) {
            log.debug( "found action name {}",action );
            JavaMethodAction javaMethodAction = (JavaMethodAction) action;
            JsonUtils utils = new JsonUtils();
            Method m = null;
            Class<?> clazz = javaMethodAction.getActionClass();
            Method[] methods = clazz.getMethods();
            for (Method m1 : methods
            ) {
                if (m1.getName().equals(action.getActionName())) {
                    m = m1;
                    break;
                }
            }
            if(m== null) {
                throw new AIProcessingException("Method name should matches the actionName  "+action.getActionName()+" class "+action.getClass().getName());
            }
            String jsonStr = utils.convertMethodTOJsonString(m);
            try {
                jsonStr = ResponseHandler.getText(PredictionLoader.getInstance().getChatExplain().sendMessage(" Here is your prompt {" + prompt + "} - here is the json - " + jsonStr + " - populate the fieldValue and return the json"));
            }
            catch(Exception e) {
                throw new AIProcessingException(" Make sure Gemini properties are set in tools4Ai.properties "+e.getMessage());
            }
            log.info(jsonStr);
            if(!jsonStr.trim().startsWith("{")) {
                jsonStr = utils.extractJson(jsonStr.trim());
            }
            JavaMethodInvoker invoke = new JavaMethodInvoker();
            Object[] obj = invoke.parse(jsonStr);
            List<Object> parameterValues = (List<Object>) obj[1];
            List<Class<?>> parameterTypes = (List<Class<?>>) obj[0];
            Method method ;
            try {
                method = clazz.getMethod(m.getName(), parameterTypes.toArray(new Class<?>[0]));
            } catch (NoSuchMethodException e) {
                throw new AIProcessingException(e);
            }

            Object result ;
            try {
                result = method.invoke(javaMethodAction.getActionInstance(), parameterValues.toArray());
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new AIProcessingException(e);
            }
            return result;
        } else {
            log.debug(action+"");
            JavaMethodExecutor methodExecutor = new JavaMethodExecutor();
            methodExecutor.mapMethod(action);
            log.debug(methodExecutor.getProperties()+"");
            String params = PredictionLoader.getInstance().getActionParams(action,prompt,AIPlatform.GEMINI,methodExecutor.getProperties());
            Object obj = null;
            try {
                if(humanVerification.allow(prompt, action.getActionName(), params).isAIResponseValid()) {
                    obj = methodExecutor.action(params, action);
                    log.debug(" the action returned "+obj);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AIProcessingException(e);
            }
            return obj;

        }
    }


    public Object processSingleAction(String prompt) throws AIProcessingException{
        return processSingleAction(prompt, new LoggingHumanDecision(),new LogginggExplainDecision());
    }
}