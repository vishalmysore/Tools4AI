package com.t4a.processor;

import com.t4a.JsonUtils;
import com.t4a.api.AIAction;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.api.JavaMethodAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public interface AIProcessor {
    public Object processSingleAction(String promptText, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException;
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException;
    public Object processSingleAction(String promptText)  throws AIProcessingException;

    public Object processSingleAction(String promptText, ActionCallback callback)  throws AIProcessingException;
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

    public default Object processSingleAction(String promptText, Object actionInstance, ActionCallback callback) throws AIProcessingException {
        GenericJavaMethodAction action = new GenericJavaMethodAction(actionInstance);
        return processSingleAction(promptText, action, new LoggingHumanDecision(), new LogginggExplainDecision(), callback);
    }
    public Object processSingleAction(String prompt, AIAction action, HumanInLoop humanVerification, ExplainDecision explain, ActionCallback callback) throws AIProcessingException ;
    public default Object invokeReflection(Method method, JavaMethodAction javaMethodAction, List<Object> parameterValues) throws IllegalAccessException, InvocationTargetException {
        Object result;
        result = method.invoke(javaMethodAction.getActionInstance(), parameterValues.toArray());
        return result;
    }
    default void setCallBack(ActionCallback callback, JavaMethodAction javaMethodAction) {
        if (callback != null) {
            Object obj = javaMethodAction.getActionInstance();
            if (obj != null) {
                Class<?> clazz = obj.getClass();
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.getType().equals(ActionCallback.class)) {
                        field.setAccessible(true); // Make the field accessible
                        try {
                            field.set(obj, callback); // Set the field to the callback instance

                        } catch (IllegalAccessException e) {

                        }
                        break; // Exit the loop after setting the field
                    }
                }
            }
        }
    }

    default void setProcessor(JavaMethodAction javaMethodAction) {
        if (javaMethodAction != null) {
            Object obj = javaMethodAction.getActionInstance();
            if (obj != null) {
                Class<?> clazz = obj.getClass();
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.getType().equals(AIProcessor.class)) {
                        field.setAccessible(true); // Make the field accessible
                        try {
                            field.set(obj, this); // Set the field to the callback instance

                        } catch (IllegalAccessException e) {

                        }
                        break; // Exit the loop after setting the field
                    }
                }
            }
        }
    }
}
