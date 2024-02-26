package com.mysore.bridge;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Type;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class JavaClassBridge extends AIBridge {
    private Map<String, Type> properties = new HashMap<>();
    private FunctionDeclaration generatedFunction;
    private Gson gson = new Gson();

    public JavaClassBridge() {

    }

    public JavaClassBridge(Gson gson) {
        this.gson = gson;

    }
    private Class<?> pojoClass ;
    public void mapClass(String className) throws ClassNotFoundException {
        this.pojoClass = Class.forName(className);;

        // Create a Map to store field names and types


        // Get all the declared fields of the POJO class
        Field[] fields = pojoClass.getDeclaredFields();

        // Iterate over the fields
        for (Field field : fields) {
            // Get the name of the field
            String fieldName = field.getName();

            // Get the type of the field
            Class<?> fieldType = field.getType();

            // Add the field name and type to the map
            properties.put(fieldName, mapType(fieldType));
        }

        // Print the field names and types
        for (Map.Entry<String, Type> entry : properties.entrySet()) {
            System.out.println("Field Name: " + entry.getKey() + ", Field Type: " + entry.getValue());
        }
    }
    public FunctionDeclaration buildFunction(String className, String funName, String discription) throws ClassNotFoundException {
        mapClass(className);
        generatedFunction = getBuildFunction(funName, discription);
        return generatedFunction;
    }

    @Override
    public Map<String, Type> getProperties() {
        return properties;
    }

    @Override
    public Gson getGson() {
        return gson;
    }

    public Object invoke(GenerateContentResponse response, String jsonString){
        Object obj = gson.fromJson(jsonString, pojoClass);
        return obj;
    }
}

