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

 /*
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
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            System.out.println("Field Name: " + entry.getKey() + ", Field Type: " + entry.getValue());
        }
    }


    public  Schema mapClassToFun(String className, String funName, String discription) throws ClassNotFoundException {

        Schema.Builder schemaBuilder = Schema.newBuilder().setType(Type.OBJECT);


        Class childpojoClass = Class.forName(className);;

        // Create a Map to store field names and types


        // Get all the declared fields of the POJO class
        Field[] fields = childpojoClass.getDeclaredFields();

        // Iterate over the fields
        for (Field field : fields) {
            // Get the name of the field
            String fieldName = field.getName();

            // Get the type of the field
            Type fieldType = mapTypeForPojo(field.getType());

            if(fieldType == Type.OBJECT) {
                schemaBuilder.putProperties(fieldName, mapClassToFun(field.getType().getName(),funName,discription))
                        .addRequired(fieldName);
            } else{

                Schema.Builder propertySchemaBuilder = Schema.newBuilder()
                        .setType(fieldType)
                        .setDescription(fieldName);
                schemaBuilder.putProperties(fieldName, propertySchemaBuilder.build())
                        .addRequired(fieldName);
                properties.put(fieldName,fieldType);
            }
        }

        Schema sc =  schemaBuilder.build();
        return sc;

    }



    public FunctionDeclaration buildFunction(String className, String funName, String discription) throws ClassNotFoundException {
        mapClass(className);
        generatedFunction = getBuildFunction(funName, discription);
        return generatedFunction;
    }
*/
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

