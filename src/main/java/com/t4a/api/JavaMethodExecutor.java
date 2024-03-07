package com.t4a.api;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Type;
import com.google.gson.Gson;
import com.t4a.action.ExtendedPredictedAction;
import com.t4a.action.http.HttpPredictedAction;
import com.t4a.action.http.InputParameter;
import com.t4a.action.shell.ShellPredictedAction;
import com.t4a.predict.LoaderException;
import lombok.extern.java.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is one of the main classes which is part of processing logic. IT does alll the mapping of actions to
 * predicted options and creates the instance of action class. It is also responsible for invoking the correct action
 * and pass back the response.
 */
@Log
public class JavaMethodExecutor extends JavaActionExecutor {
    private final Map<String, Type> properties = new HashMap<>();
    private FunctionDeclaration generatedFunction;
    private Gson gson = new Gson();

    private Class<?> clazz ;
    private Method method;
    private AIAction action;
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
    private FunctionDeclaration buildFunction(HttpPredictedAction action, String methodName, String funName, String discription) {
        mapMethod(action);
        generatedFunction = getBuildFunction(funName, discription);
        return generatedFunction;
    }
    private FunctionDeclaration buildFunction(ShellPredictedAction action, String methodName, String funName, String discription) {
        mapMethod(action);
        generatedFunction = getBuildFunction(funName, discription);
        return generatedFunction;
    }

    /**
     * Take the AIAction class and based on the type it returns a FunctionDeclaration for Gemini
     * @param action
     * @return
     */
    public FunctionDeclaration buildFunction(AIAction action) {
        this.action = action;
        if(action.getActionType().equals(ActionType.SHELL)) {
            ShellPredictedAction shellAction = (ShellPredictedAction)action;
            return buildFunction(shellAction,shellAction.getDefaultExecutorMethodName(),action.getActionName(),action.getDescription());
        } else if(action.getActionType().equals(ActionType.HTTP)) {
            HttpPredictedAction httpAction = (HttpPredictedAction)action;
            return buildFunction(httpAction,httpAction.getDefaultExecutorMethodName(),httpAction.getActionName(),httpAction.getDescription());
        } else if(action.getActionType().equals(ActionType.EXTEND))  {
            ExtendedPredictedAction extendedPredictedAction = (ExtendedPredictedAction)action;
            return extendedPredictedAction.buildFunction();
        }
        else
        return buildFunction(action.getClass().getName(),action.getActionName(),action.getActionName(),action.getDescription());
    }
    private void mapMethod(HttpPredictedAction action) {
        List<InputParameter> inputParameterList =action.getInputObjects();
        for (InputParameter parameter : inputParameterList) {
            if(!parameter.hasDefaultValue())
              properties.put(parameter.getName(), mapType(parameter.getType()));
        }

    }
    private void mapMethod(ShellPredictedAction action) {
        String nameList =action.getParameterNames();
        String[] names = nameList.split(",");
        for (String name : names) {
                properties.put(name, mapType("String"));
        }

    }

    /**
     * Convert method to map with name and value ( needs --parameter to be set at compiler to work )
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

                    log.info("Method arguments for " + methodName + ": " + properties);
                    return;
                }
            }

            log.severe("Method not found: " + methodName);
        } catch (ClassNotFoundException e) {
            log.severe("Class not found: " + className);
        }
    }

    public AIAction getAction() {
        return action;
    }

    /**
     * This method invokes the action based on the type of the action. It gets the values for the input params
     * from the prompt and populates it for the action
     * @param response
     * @param instance
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    public Object action(GenerateContentResponse response, AIAction instance) throws InvocationTargetException, IllegalAccessException {

        Map<String, Object> propertyValuesMap = getPropertyValuesMap(response);
        if(instance.getActionType().equals(ActionType.HTTP)) {
         HttpPredictedAction action = (HttpPredictedAction) instance;
            try {
                 return action.executeHttpRequest(propertyValuesMap);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else  if(instance.getActionType().equals(ActionType.SHELL)) {
            ShellPredictedAction action = (ShellPredictedAction) instance;
            try {
                String paramNamesStr = action.getParameterNames();
                String[] paramNamesArray = paramNamesStr.split(",");
                String[] paraNamesToPassToShell = new String[paramNamesArray.length];
                for (int i = 0; i < paramNamesArray.length; i++) {
                    String s = paramNamesArray[i];
                    paramNamesArray[i] = (String)propertyValuesMap.get(s);
                    log.info(paramNamesArray[i]);

                }
                action.executeShell(paramNamesArray);
                return "Executed "+action.getActionName();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } if(instance.getActionType().equals(ActionType.EXTEND)) {
            ExtendedPredictedAction action = (ExtendedPredictedAction) instance;
            try {
                return action.extendedExecute(propertyValuesMap);

            } catch (LoaderException e) {
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
