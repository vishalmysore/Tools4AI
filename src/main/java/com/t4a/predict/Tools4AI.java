package com.t4a.predict;

import com.t4a.api.AIAction;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.api.JavaMethodAction;
import com.t4a.api.JavaMethodInvoker;
import com.t4a.processor.AIProcessingException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;

public class Tools4AI {

    public static String getActionListAsJSONRPC() {
        JSONArray actionsArray = new JSONArray();

        PredictionLoader.getInstance()
                .getPredictions()
                .values()
                .forEach(action -> {
                    // Parse the escaped string into a proper JSON object
                    JSONObject actionJson = new JSONObject(action.getJsonRPC());
                    actionsArray.put(actionJson);
                });

        return actionsArray.toString(2);  // Pretty print with 2-space indentation
    }

    public static Object executeAction(String actionName, String jsonStr) throws AIProcessingException {
        AIAction action = PredictionLoader.getInstance().getPredictions().get(actionName);

        if (action == null) {
            return "Action not found";
        }
        JavaMethodInvoker invoke = new JavaMethodInvoker();
        Object[] obj = invoke.parse(jsonStr);
        List<Object> parameterValues = (List<Object>) obj[1];
        List<Class<?>> parameterTypes = (List<Class<?>>) obj[0];
        Method method;
        Class<?> clazz = ((GenericJavaMethodAction)action).getActionClass();
        Object result;
        try {
            method = clazz.getMethod(actionName, parameterTypes.toArray(new Class<?>[0]));
            result = method.invoke(((JavaMethodAction)action).getActionInstance(), parameterValues.toArray());
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
        return result;
    }

}
