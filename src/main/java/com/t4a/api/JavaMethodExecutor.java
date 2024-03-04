package com.t4a.api;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Type;
import com.google.gson.Gson;
import com.t4a.action.http.GenericHttpAction;
import com.t4a.action.http.InputParameter;
import com.t4a.action.shell.ShellAction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaMethodExecutor extends JavaActionExecutor {
    private final Map<String, Type> properties = new HashMap<>();
    private FunctionDeclaration generatedFunction;
    private Gson gson = new Gson();

    private Class<?> clazz ;
    private Method method;
    public JavaMethodExecutor() {

    }

    public JavaMethodExecutor(Gson gson) {
        this.gson = gson;

    }










    public Map<String, Type> getProperties() {
        return properties;
    }

    public FunctionDeclaration getGeneratedFunction() {
        return generatedFunction;
    }

    public Gson getGson() {
        return gson;
    }

    private FunctionDeclaration buildFunction(String className, String methodName, String funName, String discription) {
        mapMethod(className, methodName);
        generatedFunction = getBuildFunction(funName, discription);
        return generatedFunction;
    }
    private FunctionDeclaration buildFunction(GenericHttpAction action, String methodName, String funName, String discription) {
        mapMethod(action);
        generatedFunction = getBuildFunction(funName, discription);
        return generatedFunction;
    }
    public FunctionDeclaration buildFunciton(AIAction action) {
        if(action.getActionType().equals(ActionType.SHELL)) {
            ShellAction shellAction = (ShellAction)action;
            return buildFunction(action.getClass().getName(),shellAction.getDefaultExecutorMethodName(),action.getActionName(),action.getDescription());
        } if(action.getActionType().equals(ActionType.HTTP)) {
            GenericHttpAction httpAction = (GenericHttpAction)action;
            return buildFunction(httpAction,httpAction.getDefaultExecutorMethodName(),httpAction.getActionName(),httpAction.getDescription());
        }
        else
        return buildFunction(action.getClass().getName(),action.getActionName(),action.getActionName(),action.getDescription());
    }
    private void mapMethod(GenericHttpAction action) {
        List<InputParameter> inputParameterList =action.getInputObjects();
        for (InputParameter parameter : inputParameterList) {
            if(!parameter.hasDefaultValue())
              properties.put(parameter.getName(), mapType(parameter.getType()));
        }

    }

    /**
     * Convert metod to map with name and value ( needs --parameter to be set at compiler to work )
     * @param className
     * @param methodName
     */

    private void mapMethod(String className, String methodName) {


        try {
            clazz = Class.forName(className);
            Method[] methods = clazz.getMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    this.method = method;
                    Parameter[] parameters = method.getParameters();

                    for (int i = 0; i < parameters.length; i++) {
                        properties.put(parameters[i].getName(), mapType(parameters[i].getType()));
                    }

                    System.out.println("Method arguments for " + methodName + ": " + properties);
                    return;
                }
            }

            System.out.println("Method not found: " + methodName);
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + className);
        }
    }
    public Object action(GenerateContentResponse response, AIAction instance) throws InvocationTargetException, IllegalAccessException {

        Map<String, Object> propertyValuesMap = getPropertyValuesMap(response);
        if(instance.getActionType().equals(ActionType.HTTP)) {
         GenericHttpAction action = (GenericHttpAction) instance;
            try {
                 return action.executeHttpRequest(propertyValuesMap);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            String[] parameterNames = Arrays.stream(method.getParameters())
                    .map(p -> p.getName())
                    .toArray(String[]::new);

            // Create an array to hold the parameter values
            Object[] parameterValues = new Object[parameterNames.length];

            // Populate the parameter values from the map based on parameter names
            for (int i = 0; i < parameterNames.length; i++) {
                parameterValues[i] = propertyValuesMap.get(parameterNames[i]);
            }


            // Invoke the method with arguments
            Object obj = method.invoke(instance, parameterValues);
            return obj;
        }
    }


}
