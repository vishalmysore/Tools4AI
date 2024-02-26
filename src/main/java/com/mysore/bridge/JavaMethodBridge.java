package com.mysore.bridge;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Type;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JavaMethodBridge extends AIBridge {
    private final Map<String, Type> properties = new HashMap<>();
    private FunctionDeclaration generatedFunction;
    private Gson gson = new Gson();

    private Class<?> clazz ;
    private Method method;
    public JavaMethodBridge() {

    }

    public JavaMethodBridge(Gson gson) {
        this.gson = gson;

    }








    private  String getValue(GenerateContentResponse response, String propertyName) {
        return ResponseHandler.getContent(response).getParts(0).getFunctionCall().getArgs().getFieldsMap().get(propertyName).getStringValue();
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

    public FunctionDeclaration buildFunction(String className, String methodName, String funName, String discription) {
        mapMethod(className, methodName);
        generatedFunction = getBuildFunction(funName, discription);
        return generatedFunction;
    }




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
    public void invoke(GenerateContentResponse response, Object instance) throws InvocationTargetException, IllegalAccessException {
        Map<String, Object> propertyValuesMap = getPropertyValuesMap(response);
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
        method.invoke(instance, parameterValues);
    }


}
