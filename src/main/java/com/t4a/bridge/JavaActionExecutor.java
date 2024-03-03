package com.t4a.bridge;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Schema;
import com.google.cloud.vertexai.api.Type;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the base class for all the bridges will implement common functionality
 */
public abstract class JavaActionExecutor implements AIActionExecutor {


    /**
     * Map Java type to Gemini type
     * @param type
     * @return
     */
    public Type mapType(Class<?> type) {
        if (type == String.class) {
            return Type.STRING;
        } else if (type == int.class || type == Integer.class) {
            return Type.INTEGER;
        } else if (type == double.class || type == Double.class) {
            return Type.NUMBER;
        } else if (type == boolean.class || type == Boolean.class) {
            return Type.BOOLEAN;
        } else if(type.isArray()){
            return Type.ARRAY;
        } else {
            return Type.OBJECT;
        }

    }

    /**
     * Create Gemini Schema object this will be used to create funciton
     * @param properties
     * @return
     */
    protected Schema getBuild(Map<String, Type> properties) {
        Schema.Builder schemaBuilder = Schema.newBuilder().setType(Type.OBJECT);

        for (Map.Entry<String, Type> entry : properties.entrySet()) {
            String property = entry.getKey();
            Type type = entry.getValue();

            Schema propertySchema = Schema.newBuilder()
                    .setType(type)
                    .setDescription(property)
                    .build();

            schemaBuilder.putProperties(property, propertySchema)
                    .addRequired(property);
        }

        return schemaBuilder.build();
    }

    /**
     * Create schema from single property
     * @param type
     * @param property
     * @return
     */
    protected  Schema getBuild(Type type, String property) {
        return Schema.newBuilder()
                .setType(Type.OBJECT)
                .putProperties(property, Schema.newBuilder()
                        .setType(type)
                        .setDescription(property)
                        .build()
                )
                .addRequired(property)
                .build();
    }

    /**
     * Create function from the funciton name and discription , this is the main method behind all the magic
     * it builds based on the properties which are initially created using properties, this properties are
     * created by the subclasses by mapping the method or java class. Any subclass has to populate the properties
     * map object
     * @param funName
     * @param discription
     * @return
     */
    protected FunctionDeclaration getBuildFunction(String funName, String discription) {
        return FunctionDeclaration.newBuilder()
                .setName(funName)
                .setDescription(discription)
                .setParameters(
                        getBuild(getProperties())
                )
                .build();
    }

    /**
     * Fetches the values populated by gemini into the function , this will get mapped to a MAP
     * which can then converted to json or invoke method or make http call
     * @param response
     * @return
     */
    public Map<String, Object> getPropertyValuesMap(GenerateContentResponse response) {
        List<String> propertyNames = new ArrayList<>(getProperties().keySet());
        Map<String, Object> propertyValues = new HashMap<>();
        for (String propertyName : propertyNames) {
            Value value = ResponseHandler.getContent(response).getParts(0).getFunctionCall().getArgs().getFieldsMap().get(propertyName);
            if (value.hasBoolValue()) {
                propertyValues.put(propertyName, Boolean.valueOf(value.getBoolValue()));
            } else if (value.hasStringValue()) {
                propertyValues.put(propertyName, value.getStringValue());
            }else if(value.hasListValue()){
                extractedList(propertyName, value, propertyValues);
            }
            else if (value.hasNumberValue()) {
                if(getProperties().get(propertyName).equals(Type.INTEGER)) {
                    propertyValues.put(propertyName, Integer.valueOf((int)value.getNumberValue()));
                } else {
                    propertyValues.put(propertyName, Double.valueOf(value.getNumberValue()));
                }
            }


        }
        return propertyValues;
    }

    private static void extractedList(String propertyName, Value value, Map<String, Object> propertyValues) {
        ListValue lv = value.getListValue();
        List<Value> valueFromList = lv.getValuesList();
        if(valueFromList.size() >0) {
            if(valueFromList.get(0).hasStringValue()) {
                extractedString(propertyName, valueFromList, propertyValues);
            } else if(valueFromList.get(0).hasBoolValue()) {
                extractedBoolean(propertyName, valueFromList, propertyValues);
            } else if(valueFromList.get(0).hasListValue()) {
                extractedList(propertyName, valueFromList.get(0), propertyValues);
            }
        }
    }

    private static void extractedString(String propertyName, List<Value> valueFromList, Map<String, Object> propertyValues) {
        String[] args = new String[valueFromList.size()];
        int count = 0;
        for (Value v: valueFromList
        ) {
            args[count] = v.getStringValue();
            count++;
        }
        propertyValues.put(propertyName,args);
    }

    private static void extractedBoolean(String propertyName, List<Value> valueFromList, Map<String, Object> propertyValues) {
        Boolean[] args = new Boolean[valueFromList.size()];
        int count = 0;
        for (Value v: valueFromList
        ) {
            args[count] = v.getBoolValue();
            count++;
        }
        propertyValues.put(propertyName,args);
    }

    public String getPropertyValuesJsonString(GenerateContentResponse response) {

        String jsonString = getGson().toJson(getPropertyValuesMap(response));
        return jsonString;
    }

    public String getPropertyValuesJsonString(GenerateContentResponse response, boolean asIs) {

        String jsonString = getGson().toJson(ResponseHandler.getContent(response).getParts(0).getFunctionCall().getArgs().getFieldsMap());
        return jsonString;
    }
    protected abstract Map<String,Type> getProperties();
    protected abstract Gson getGson();


}
