package com.t4a.examples;

import com.t4a.JsonUtils;
import com.t4a.examples.actions.CustomerWithQueryAction;
import com.t4a.examples.actions.SearchAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

public class TestJsonUtilsExample {
    public static void main1(String[] args) {
        JsonUtils utils = new JsonUtils();
        Method[] met = SearchAction.class.getMethods();


        met = CustomerWithQueryAction.class.getMethods();
        for (Method m:met
        ) {
            if(m.getName().equals("computerRepairWithDetails")){
                utils.convertMethodTOJsonString(m);
            }
        }
    }

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
        JSONObject json = new JSONObject(jsonStr);

        try {
            String methodName = json.getString("methodName");
            JSONArray paramsArray = json.getJSONArray("parameters");
            Class<?>[] paramTypes = new Class[paramsArray.length()];
            Object[] paramValues = new Object[paramsArray.length()];

            for (int i = 0; i < paramsArray.length(); i++) {
                JSONObject param = paramsArray.getJSONObject(i);
                String type = param.getString("type");


                if (param.has("fields")) {
                    Class<?> paramClass = Class.forName(type);
                    paramTypes[i] = paramClass;// Complex object
                    Constructor<?> constructor = paramClass.getDeclaredConstructor(); // No-arg constructor
                    Object paramInstance = constructor.newInstance();

                    JSONArray fields = param.getJSONArray("fields");
                    for (int j = 0; j < fields.length(); j++) {
                        JSONObject field = fields.getJSONObject(j);
                        String fieldName = field.getString("fieldName");
                        String fieldType = field.getString("fieldType");
                        Field classField = paramClass.getDeclaredField(fieldName);
                        classField.setAccessible(true);

                        // Assuming all fields are public for simplicity
                        if ("String".equals(fieldType)) {
                            classField.set(paramInstance, "Example"); // Placeholder value, fetch from JSON if available
                        } else if ("Date".equals(fieldType)) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            classField.set(paramInstance, sdf.parse("2024-01-01")); // Placeholder value, fetch from JSON if available
                        }
                        // Add more types as needed
                    }

                    paramValues[i] = paramInstance;
                } else { // Simple object or primitive
                    // Direct assignment or parsing, depending on type
                    // For demonstration, assuming simple cases with direct assignment or straightforward parsing
                    if ("String".equals(type)) {
                        paramValues[i] = "Example query"; // Placeholder, use actual values
                        paramTypes[i] = getClassForType(type);
                    } else if ("Date".equals(type)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        paramValues[i] = sdf.parse("2024-01-01"); // Placeholder, use actual values
                        paramTypes[i] = getClassForType(type);
                    }
                }
            }

            // Dynamically invoke the method
            Method method = CustomerWithQueryAction.class.getMethod(methodName, paramTypes);
            String returnValue = (String) method.invoke(new CustomerWithQueryAction(), paramValues); // Assuming static method for simplicity

            System.out.println("Method returned: " + returnValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Class<?> getClassForType(String typeName) throws ClassNotFoundException {
        switch (typeName) {
            case "int":
                return int.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "long":
                return long.class;
            case "boolean":
                return boolean.class;
            case "char":
                return char.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            // For non-primitive types, attempt to load the class by name
            case "Date":
                return Date.class;
            case "date":
                return Date.class;
            case "String":
                return String.class;
            case "string":
                return String.class;
            default:
                return Class.forName(typeName);
        }
    }
}
