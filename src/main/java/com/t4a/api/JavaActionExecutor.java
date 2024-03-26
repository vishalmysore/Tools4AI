package com.t4a.api;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Schema;
import com.google.cloud.vertexai.api.Type;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * This is the base class for all the java based executors with common functionality
 */
@Slf4j
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

    public Type mapTypeForPojo(Class<?> type) {
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
        } else if(type.equals(Date.class)){
            return Type.STRING;
        } else {
            return Type.OBJECT;
        }

    }


    /**
     * Map String to Gemini type
     * @param type
     * @return
     */
    public Type mapType(String type) {
        if (type.equalsIgnoreCase("String")) {
            return Type.STRING;
        } else if (type.equalsIgnoreCase("int")) {
            return Type.INTEGER;
        } else if (type.equalsIgnoreCase("integer")) {
            return Type.INTEGER;
        } else if (type.equalsIgnoreCase("num")) {
            return Type.NUMBER;
        } else if (type.equalsIgnoreCase("boolean")) {
            return Type.BOOLEAN;
        } else if(type.equalsIgnoreCase("array")){
            return Type.ARRAY;
        } else {
            return Type.OBJECT;
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
              //  if(getProperties() != null) {
                //    getProperties().put(fieldName, fieldType);
               // }
            }
        }

        Schema sc =  schemaBuilder.build();
        return sc;

    }


    /**
     * Create Gemini Schema object this will be used to create funciton
     *
     * @return
     */
    protected Schema getBuildForJson(Map<String,Object> mapOfMapsForJason) {
        Schema.Builder schemaBuilder = Schema.newBuilder().setType(Type.OBJECT);

        for (Map.Entry<String, Object> entry : mapOfMapsForJason.entrySet()) {
            String property = entry.getKey();
            Object value = entry.getValue();
            Type type = mapType(value.getClass());

            Schema.Builder propertySchemaBuilder = Schema.newBuilder()
                    .setType(type)
                    .setDescription(property);

            if(type == Type.OBJECT) {
                log.debug(property);
                if(value instanceof Map) {
                    Map<String, Object> mapOfMapsForJasonRecursive = (Map<String, Object>) value;
                    propertySchemaBuilder.putProperties(property, getBuildForJson(mapOfMapsForJasonRecursive));
                } else if (value instanceof ArrayList) {
                    ArrayList list = (ArrayList) value;

                    for (Object listVal:list
                         ) {

                        log.debug(getBuildForJson((Map<String, Object>) listVal).toString());
                    }
                }
            }
            Schema propertySchema= propertySchemaBuilder.build();
            schemaBuilder.putProperties(property, propertySchema)
                    .addRequired(property);
        }

        return schemaBuilder.build();
    }
    protected Schema getBuild(Map<String, Object> properties) {
        Schema.Builder schemaBuilder = Schema.newBuilder().setType(Type.OBJECT);

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String property = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Type) {
                Type type = (Type) entry.getValue();

                Schema propertySchema = Schema.newBuilder()
                        .setType(type)
                        .setDescription(property)
                        .build();
                if (type == Type.OBJECT) {
                    log.debug(property);
                }

                schemaBuilder.putProperties(property, propertySchema)
                        .addRequired(property);
            }else  {
                try {
                    Class valueClass = (Class)value;
                    schemaBuilder.putProperties(property,mapClassToFun(valueClass.getName(),valueClass.getSimpleName()," adding sub component"));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
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
    protected FunctionDeclaration getBuildFunction(Map<String,Object> mapOfMapsForJason,String funName, String discription) {
        return FunctionDeclaration.newBuilder()
                .setName(funName)
                .setDescription(discription)
                .setParameters(
                        getBuildForJson(mapOfMapsForJason)
                )
                .build();
    }

    public  Map<String, Object> protobufToMap(Map<String, Value> protobufMap) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, Value> entry : protobufMap.entrySet()) {
            String key = entry.getKey();
            Value value = entry.getValue();
            Object convertedValue = convertValue(value);
            resultMap.put(key, convertedValue);
        }
        return resultMap;
    }

    private  Object convertValue(Value value) {
        switch (value.getKindCase()) {
            case NULL_VALUE:
                return null;
            case NUMBER_VALUE:
                return value.getNumberValue();
            case STRING_VALUE:
                return value.getStringValue();
            case BOOL_VALUE:
                return value.getBoolValue();
            case STRUCT_VALUE:
                Struct struct = value.getStructValue();
                Map<String, Object> structMap = new HashMap<>();
                for (Map.Entry<String, Value> entry : struct.getFieldsMap().entrySet()) {
                    structMap.put(entry.getKey(), convertValue(entry.getValue()));
                }
                return structMap;
            case LIST_VALUE:
                return value.getListValue().getValuesList().stream()
                        .map(this::convertValue)
                        .toArray();
            case KIND_NOT_SET:
                return null;
        }
        return null;
    }


    public Map<String, Object> getPropertyValuesMapMap(GenerateContentResponse response) {
        Map<String, Value> map = ResponseHandler.getContent(response).getParts(0).getFunctionCall().getArgs().getFieldsMap();
        Map<String, Object> resultMap =  protobufToMap(map);
        return resultMap;

    }


    public Map<String, Object> getPropertyValuesMap(String responseFromAI) {
        List<String> dataList = Arrays.asList(responseFromAI.split(","));
        // Create a new Map to store key-value pairs
        Map<String, Object> map = new HashMap<>();

        // Iterate through the list and parse each entry to populate the map
        for (String data : dataList) {
            // Split each entry by '=' to separate key and value
            String[] parts = data.split("=");
            if (parts.length == 2) {
                String key = parts[0];
                Object value = parts[1]; // Object type allows for flexibility
                map.put(key, value);
            } else {
                log.warn("Invalid entry: " + data);
            }
        }
        return map;
    }
    /**
     * Fetches the values populated by gemini into the function , this will get mapped to a MAP
     * which can then converted to json or invoke method or make http call
     * @param response
     * @return
     */
    public Map<String, Object> getPropertyValuesMap(GenerateContentResponse response) {
        List<String> propertyNames = new ArrayList<>(getProperties().keySet());
        log.debug(" Trying to parse "+ResponseHandler.getContent(response));
        Map<String, Object> propertyValues = new HashMap<>();
        for (String propertyName : propertyNames) {
            if(ResponseHandler.getContent(response).getPartsList().size() > 0) {
                Value value = ResponseHandler.getContent(response).getParts(0).getFunctionCall().getArgs().getFieldsMap().get(propertyName);
                if (value != null) {
                    if (value.hasBoolValue()) {
                        propertyValues.put(propertyName, Boolean.valueOf(value.getBoolValue()));
                    } else if (value.hasStringValue()) {
                        propertyValues.put(propertyName, value.getStringValue());
                    } else if (value.hasListValue()) {
                        extractedList(propertyName, value, propertyValues);
                    } else if (value.hasNumberValue()) {
                        if (getProperties().get(propertyName).equals(Type.INTEGER)) {
                            propertyValues.put(propertyName, Integer.valueOf((int) value.getNumberValue()));
                        } else {
                            propertyValues.put(propertyName, Double.valueOf(value.getNumberValue()));
                        }
                    }
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
    protected abstract Map<String,Object> getProperties();
    protected abstract Gson getGson();


}
