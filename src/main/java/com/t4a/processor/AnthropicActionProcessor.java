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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class AnthropicActionProcessor implements AIProcessor {
    private Gson gson;

    public AnthropicActionProcessor(Gson gson) {

        this.gson = gson;
    }


    @Override
    public String query(String promptText) throws AIProcessingException {
        return PredictionLoader.getInstance().getAnthropicChatModel().generate(promptText);
    }

    public AnthropicActionProcessor() {
        this.gson = new Gson();
    }

    @Override
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
        return processSingleAction(promptText, null, humanVerification, explain);
    }

    public Object processSingleAction(String promptText, AIAction action) throws AIProcessingException {
        return processSingleAction(promptText, action, null, null);
    }

    public Object processSingleAction(String promptText, String actionName) throws AIProcessingException {
        AIAction action = PredictionLoader.getInstance().getAiAction(actionName);
        if (action == null) {
            throw new AIProcessingException(" action not found " + actionName);
        }
        return processSingleAction(promptText, action, null, null);
    }

    public Object processSingleAction(String prompt, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
        return processSingleAction(prompt, action, humanVerification, explain, null);
    }

    @Override
    public Object processSingleAction(String prompt, AIAction action, HumanInLoop humanVerification, ExplainDecision explain, ActionCallback callback) throws AIProcessingException {
        if (action == null) {
            action = PredictionLoader.getInstance().getPredictedAction(prompt, AIPlatform.ANTHROPIC);
            if (action == null) {
                return "no action found for the prompt " + prompt;
            }
            if (action.getActionRisk() == ActionRisk.HIGH) {
                log.warn(" This is a high risk action needs to be explicitly provided by human operator cannot be predicted by AI " + action.getActionName());
                return "This is a high risk action will not proceed " + action.getActionName();
            }
        }
        if (action.getActionType() == ActionType.JAVAMETHOD) {
            log.debug(action + "");
            JsonUtils utils = new JsonUtils();
            Method m = null;
            JavaMethodAction javaMethodAction = (JavaMethodAction) action;
            try {
                setCallBack(callback, javaMethodAction);
            } catch (IllegalAccessException e) {
                throw new AIProcessingException(e);
            }
            setProcessor(javaMethodAction);
            Class<?> clazz = javaMethodAction.getActionClass();
            m = findActionMethod(javaMethodAction);
            String jsonStr = utils.convertMethodTOJsonString(m);
            try {
                jsonStr = PredictionLoader.getInstance().getAnthropicChatModel().generate(" Here is your prompt {" + prompt + "} - here is the json - " + jsonStr + " - populate the fieldValue and return the json");
            } catch (Exception e) {
                throw new AIProcessingException(" Make sure Anthropic properties are set in tools4Ai.properties ");
            }
            log.info(jsonStr);
            if (!jsonStr.trim().startsWith("{")) {
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
                if (!isApprovedByHuman(humanVerification, prompt, action.getActionName(), jsonStr)) {
                    return "Human verification failed";
                }
                result = invokeReflection(method, javaMethodAction, parameterValues);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new AIProcessingException(e);
            }
            return result;
        } else {
            log.debug(action + "");
            JavaMethodExecutor methodExecutor = new JavaMethodExecutor();
            methodExecutor.mapMethod(action);
            log.debug(methodExecutor.getProperties() + "");
            String params = PredictionLoader.getInstance().getActionParams(action, prompt, AIPlatform.ANTHROPIC, methodExecutor.getProperties());
            Object obj = null;
            try {
                if (isApprovedByHuman(humanVerification, prompt, action.getActionName(), params)) {
                    obj = methodExecutor.action(params, action);
                    log.debug(" the action returned " + obj);
                } else {
                    return "Human verification failed";
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new AIProcessingException(e);
            }
            return obj;

        }
    }


    public Object processSingleAction(String prompt) throws AIProcessingException {
        return processSingleAction(prompt, new LoggingHumanDecision(), new LogginggExplainDecision());
    }

    @Override
    public Object processSingleAction(String promptText, ActionCallback callback) throws AIProcessingException {
        return processSingleAction(promptText, null, new LoggingHumanDecision(), null, callback);
    }
}