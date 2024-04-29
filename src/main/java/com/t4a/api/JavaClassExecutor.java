package com.t4a.api;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Schema;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Create a POJO Object from the response object
 */
public class JavaClassExecutor extends JavaActionExecutor {
    private Map<String, Object> properties = new HashMap<>();
    private FunctionDeclaration generatedFunction;
    private Gson gson = new Gson();

    public JavaClassExecutor() {

    }

    public JavaClassExecutor(Gson gson) {
        this.gson = gson;

    }
    private Class<?> pojoClass ;


    public FunctionDeclaration buildFunctionFromClass(String className, String funName, String discription) throws ClassNotFoundException {
        Schema schema  =mapClassToFun(className,funName, discription);
        pojoClass = Class.forName(className);
        generatedFunction = FunctionDeclaration.newBuilder()
                .setName(funName)
                .setDescription(discription)
                .setParameters(schema).build();
        return generatedFunction;
    }
    public FunctionDeclaration buildFunction(Map<String,Object> mapOfMapsForJason, String funName, String discription) throws ClassNotFoundException {

        generatedFunction = getBuildFunction(mapOfMapsForJason,funName, discription);
        return generatedFunction;
    }
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public Gson getGson() {
        return gson;
    }

    public Object action(GenerateContentResponse response, String jsonString){
        Object obj = gson.fromJson(jsonString, pojoClass);
        return obj;
    }
}

