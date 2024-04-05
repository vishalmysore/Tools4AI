package com.t4a.examples;

import com.t4a.examples.actions.CustomerWithQueryAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JavaMethodInvoker {
    public static void main(String[] args) {
        String resourceName = "customerJson.json"; // Adjust the path as needed
        String jsonStr = null;
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourceName);
            }

            jsonStr = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining(System.lineSeparator()));

            System.out.println(jsonStr);
        } catch (Exception e) {
            System.err.println("Error reading resource: " + e.getMessage());
        }

        JSONObject jsonObject = new JSONObject(jsonStr);
        String methodName = jsonObject.getString("methodName");
        String returnType = jsonObject.getString("returnType");

        JSONArray parametersArray = jsonObject.getJSONArray("parameters");
        List<Object> parameterValues = new ArrayList<>();
        List<Class<?>> parameterTypes = new ArrayList<>();

        try {
            for (int i = 0; i < parametersArray.length(); i++) {
                JSONObject paramObj = parametersArray.getJSONObject(i);
                String type = paramObj.getString("type");
                Object value;

                if (paramObj.has("value")) {
                    value = paramObj.get("value");
                } else if (paramObj.has("fields")) {
                    value = createPOJO(paramObj.getJSONArray("fields"), Class.forName(type));
                } else {
                    throw new IllegalArgumentException("No value or fields found for parameter: " + paramObj.getString("name"));
                }

                // Resolve primitive types
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
                    value = new SimpleDateFormat("yyyy-MM-dd").parse((String) value);
                } else {
                    // Handle custom class types
                    parameterType = Class.forName(type);
                }

                // Add parameter type to the list
                parameterTypes.add(parameterType);

                // Add parameter value to the list
                parameterValues.add(value);
            }
            // Invoke the method
            Class<?> clazz = Class.forName(CustomerWithQueryAction.class.getName()); // Replace "YourClassName" with the actual class name containing the method
            Method method = clazz.getMethod(methodName, parameterTypes.toArray(new Class[0]));
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Object result = method.invoke(instance, parameterValues.toArray());

            System.out.println("Method Invocation Result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create POJO using reflection
    private static Object createPOJO(JSONArray fieldsArray, Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getConstructor();
        Object instance = constructor.newInstance();

        for (int i = 0; i < fieldsArray.length(); i++) {
            JSONObject fieldObj = fieldsArray.getJSONObject(i);
            String fieldName = fieldObj.getString("fieldName");
            String fieldType = fieldObj.getString("fieldType");
            Object fieldValue = fieldObj.get("value");

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
    private static Object createPOJO(Object fieldValue, Class<?> clazz) throws Exception {
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
