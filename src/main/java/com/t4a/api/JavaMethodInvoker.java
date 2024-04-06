package com.t4a.api;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JavaMethodInvoker {
    public JavaMethodInvoker() {

    }
    public  Object[] parse(String jsonStr) {

        JSONObject jsonObject = new JSONObject(jsonStr);
        String methodName = jsonObject.getString("methodName");
        String returnType = jsonObject.getString("returnType");

        JSONArray parametersArray = jsonObject.getJSONArray("parameters");
        List<Object> parameterValues = new ArrayList<>();
        List<Class<?>> parameterTypes = new ArrayList<>();
        Object[] returnObject = new Object[2];
        returnObject[0] = parameterTypes;
        returnObject[1] = parameterValues;

        try {
            for (int i = 0; i < parametersArray.length(); i++) {
                JSONObject paramObj = parametersArray.getJSONObject(i);
                String type = paramObj.getString("type");
                Class<?> parameterType = getType(type);
                Object value;

                if (paramObj.has("fieldValue")) {
                    value = getValue(paramObj.get("fieldValue"),parameterType);
                } else if (paramObj.has("fields")) {
                    value = createPOJO(paramObj.getJSONArray("fields"), Class.forName(type));
                } else {
                    throw new IllegalArgumentException("No value or fields found for parameter: " + paramObj.getString("name"));
                }

                // Resolve primitive types


                // Add parameter type to the list
                parameterTypes.add(parameterType);

                // Add parameter value to the list
                parameterValues.add(value);
            }
            // Invoke the method


            return returnObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object[0];
    }
    private  Object getValue(Object value, Class<?> type) {
        if (type == String.class) {
            return value.toString();
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value.toString());
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value.toString());
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value.toString());
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value.toString());
        } else if (type == Date.class) {
            try {
                String dateStr = (String) value;
                if((dateStr != null) && (dateStr.trim().length() > 1))
                  return new SimpleDateFormat("yyyy-MM-dd").parse((String) value);
                else
                    return null;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            // Handle custom class types
            return value;
        }
    }
    @NotNull
    private  Class<?> getType(String type) throws ClassNotFoundException {
        Class<?> parameterType;
        if (type.equals("String")) {
            parameterType = String.class;
        } else if (type.equals("int")) {
            parameterType = int.class;
        } else if (type.equals("double")) {
            parameterType = double.class;
        } else if (type.equals("boolean")) {
            parameterType = boolean.class;
        } else if (type.equals("Date")) {
            parameterType = java.util.Date.class;
            // Convert string value to Date
        } else {
            // Handle custom class types
            parameterType = Class.forName(type);
        }
        return parameterType;
    }

    // Create POJO using reflection
    private  Object createPOJO(JSONArray fieldsArray, Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getConstructor();
        Object instance = constructor.newInstance();

        for (int i = 0; i < fieldsArray.length(); i++) {
            JSONObject fieldObj = fieldsArray.getJSONObject(i);
            String fieldName = fieldObj.getString("fieldName");
            String fieldType = fieldObj.getString("fieldType");
            Class<?> parameterType = getType(fieldType);
            Object fieldValue =null;// getValue(fieldObj.get("fieldValue"),parameterType);
            if (fieldObj.has("fieldValue")) {
                fieldValue = getValue(fieldObj.get("fieldValue"),parameterType);
            } else if (fieldObj.has("fields")) {
                fieldValue = createPOJO(fieldObj.getJSONArray("fields"), Class.forName(fieldType));
            } else {
                throw new IllegalArgumentException("No value or fields found for parameter: " + fieldObj.getString("name"));
            }
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            // Resolve nested POJOs recursively
            if (fieldValue instanceof JSONObject || fieldValue instanceof JSONArray) {
                fieldValue = createPOJO(fieldValue, Class.forName(fieldType));
            }

            field.set(instance, fieldValue);
        }

        return instance;
    }

    // Overloaded method for handling nested arrays
    private  Object createPOJO(Object fieldValue, Class<?> clazz) throws Exception {
        if (fieldValue instanceof JSONObject) {
            return createPOJO((JSONObject) fieldValue, clazz);
        } else if (fieldValue instanceof JSONArray) {
            List<Object> nestedList = new ArrayList<>();
            JSONArray jsonArray = (JSONArray) fieldValue;
            for (int i = 0; i < jsonArray.length(); i++) {
                nestedList.add(createPOJO(jsonArray.getJSONObject(i), clazz));
            }
            return nestedList;
        }
        return fieldValue;
    }
}
