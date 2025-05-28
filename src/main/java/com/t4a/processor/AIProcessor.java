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

    default void cleanUpThreadLocal(JavaMethodAction javaMethodAction) {
        if (javaMethodAction == null) return;

        Object obj = javaMethodAction.getActionInstance();
        if (obj == null) return;

        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
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
            } catch (Exception e) {
                e.printStackTrace(); // Optional: log the error
            }
        }
    }


    default void setCallBack(ActionCallback callback, JavaMethodAction javaMethodAction) {
        if (callback == null || javaMethodAction == null) return;

        Object obj = javaMethodAction.getActionInstance();
        if (obj == null) return;

        Class<?> clazz = resolveActualClass(obj);
        if (clazz == null) return;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.getType().equals(ActionCallback.class)) {
                    // Direct ActionCallback field
                    field.set(obj, callback);
                    break;
                } else if (ThreadLocal.class.isAssignableFrom(field.getType())) {
                    // Check if it's a ThreadLocal<ActionCallback>
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType paramType = (ParameterizedType) genericType;
                        Type actualType = paramType.getActualTypeArguments()[0];
                        if (actualType.getTypeName().equals(ActionCallback.class.getName())) {
                            ThreadLocal<ActionCallback> threadLocal = (ThreadLocal<ActionCallback>) field.get(obj);
                            if (threadLocal == null) {
                                threadLocal = new ThreadLocal<>();
                                field.set(obj, threadLocal);
                            }
                            threadLocal.set(callback);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // You might want to log this instead
            }
        }
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
            e.printStackTrace();
            return obj.getClass();
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
