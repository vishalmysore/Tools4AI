package com.t4a.processor;

import com.t4a.JsonUtils;
import com.t4a.api.AIAction;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.api.JavaMethodAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;

import java.lang.reflect.*;
import java.util.List;

public interface AIProcessor {
    public Object processSingleAction(String promptText, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException;
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException;
    public Object processSingleAction(String promptText)  throws AIProcessingException;

    public default Object processSingleAction(String promptText, HumanInLoop humanVeification) throws AIProcessingException{
        return processSingleAction(promptText, null,humanVeification,null);
    }

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
        Object result = null;
        try {
            result = method.invoke(javaMethodAction.getActionInstance(), parameterValues.toArray());
        }  finally {
            cleanUpThreadLocal(javaMethodAction);
        }
        return result;
    }

    default void cleanUpThreadLocal(JavaMethodAction javaMethodAction) throws IllegalAccessException {
        if (javaMethodAction == null) return;

        Object obj = javaMethodAction.getActionInstance();
        if (obj == null) return;

        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

                if (field.getType().equals(ThreadLocal.class)) {
                    // Ensure this is ThreadLocal<ActionCallback>
                    if (field.getGenericType() instanceof ParameterizedType) {
                        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        if (genericType.getActualTypeArguments()[0].getTypeName().equals(ActionCallback.class.getName())) {
                            ThreadLocal<ActionCallback> threadLocal = (ThreadLocal<ActionCallback>) field.get(obj);
                            if (threadLocal != null) {
                                threadLocal.remove(); // Clean up to avoid memory leaks
                            }
                        }
                    }
                }

        }
    }


    default void setCallBack(ActionCallback callback, JavaMethodAction javaMethodAction) throws IllegalAccessException {
        if (callback == null || javaMethodAction == null) return;

        Object obj = javaMethodAction.getActionInstance();
        if (obj == null) return;

        // 1. PREFERRED: Check if it implements the interface
        if (obj instanceof ActionCallbackAware) {
            ((ActionCallbackAware) obj).setCallback(callback);
            return;
        }

        // 2. FALLBACK: Keep your existing reflection logic here for backward compatibility
        // ... (paste your existing reflection code here) ...
    }

    default Class<?> resolveActualClass(Object obj) {
        try {
            // Try using Spring's AopUtils.getTargetClass if available
            Class<?> aopUtils = Class.forName("org.springframework.aop.support.AopUtils");
            Method getTargetClass = aopUtils.getMethod("getTargetClass", Object.class);
            return (Class<?>) getTargetClass.invoke(null, obj);
        } catch (ClassNotFoundException e) {
            // Spring not present, fall back to raw class
            return obj.getClass();
        } catch (Exception e) {
            // Something else went wrong, fall back

            return obj.getClass();
        }
    }



    default void setProcessor(JavaMethodAction javaMethodAction) {
        if (javaMethodAction == null) return;

        Object obj = javaMethodAction.getActionInstance();
        if (obj == null) return;


            if (obj instanceof ProcessorAware) {

                ((ProcessorAware) obj).setProcessor(this);
                return;
            }



    }
}
