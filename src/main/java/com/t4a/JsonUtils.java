package com.t4a;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.t4a.annotations.Prompt;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
@Slf4j
public class JsonUtils {

    public static String convertClassObjectToJsonString(Schema classSchema) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        // Process each property of the class schema recursively
        processProperties(rootNode, classSchema.getProperties());

        // Convert the JSON node to a string
        return rootNode.toString();
    }

    public  Object populateClassFromJson(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        return populateObject(jsonObject);
    }

    private  Object populateObject(JSONObject jsonObject) throws Exception {
        String className = jsonObject.getString("className");
        Class<?> clazz = Class.forName(className);
        Object instance = clazz.getDeclaredConstructor().newInstance();

        JSONArray fields = jsonObject.getJSONArray("fields");
        for (int i = 0; i < fields.length(); i++) {
            JSONObject fieldObj = fields.getJSONObject(i);
            String fieldName = fieldObj.getString("fieldName");
            String fieldType = fieldObj.getString("fieldType");
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            if ("String".equals(fieldType)) {
                field.set(instance, fieldObj.optString("fieldValue"));
            } else if (fieldType.endsWith("[]")) { // Check if the field is an array
                JSONArray jsonArray = fieldObj.optJSONArray("fieldValue");
                if (jsonArray != null) {
                    String componentType = fieldType.substring(0, fieldType.indexOf('['));
                    Object array;

                    if ("int".equals(componentType)) {
                        array = new int[jsonArray.length()];
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Array.setInt(array, j, jsonArray.optInt(j));
                        }
                    } else if ("double".equals(componentType)) {
                        array = new double[jsonArray.length()];
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Array.setDouble(array, j, jsonArray.optDouble(j));
                        }
                    } else if ("long".equals(componentType)) {
                        array = new long[jsonArray.length()];
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Array.setLong(array, j, jsonArray.optLong(j));
                        }
                    } else if ("boolean".equals(componentType)) {
                        array = new boolean[jsonArray.length()];
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Array.setBoolean(array, j, jsonArray.optBoolean(j));
                        }
                    } else if ("String".equals(componentType)) {
                        array = new String[jsonArray.length()];
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Array.set(array, j, jsonArray.optString(j));
                        }
                    } else {
                        // Handle other component types or custom objects
                        array = null; // Placeholder, adjust as necessary
                    }

                    field.set(instance, array);
                }
            } else if ("date".equalsIgnoreCase(fieldType)) {
                try {
                    String dateStr = (String) fieldObj.getString("fieldValue");
                    SimpleDateFormat sdf = new SimpleDateFormat(fieldObj.getString("dateFormat"));
                    Date d = sdf.parse(dateStr);
                    field.set(instance,d);

                } catch (ParseException e) {
                    e.printStackTrace();

                }
            }
            else if ("int".equals(fieldType)) {
                        field.setInt(instance, fieldObj.getInt("fieldValue"));
                    } else if ("boolean".equals(fieldType)) {
                        field.setBoolean(instance, fieldObj.getBoolean("fieldValue"));
                    } else if ("double".equals(fieldType)) {
                        // Checking and parsing double value
                        if (!fieldObj.optString("fieldValue").isEmpty()) {
                            field.setDouble(instance, fieldObj.getDouble("fieldValue"));
                        }
                    } else if ("long".equals(fieldType)) {
                        // Checking and parsing long value
                        if (!fieldObj.optString("fieldValue").isEmpty()) {
                            field.setLong(instance, fieldObj.getLong("fieldValue"));
                        }
                    }

             else if (fieldObj.has("fields")) {
                // Recursively populate nested objects
                Object nestedInstance = populateObject(fieldObj);
                field.set(instance, nestedInstance);
            }
            // Handle other types like Date here as in previous examples
        }

        return instance;
    }

    public String convertClassToJSONString(Class<?> clazz) {
        JSONObject classJson = new JSONObject();
        classJson.put("className", clazz.getName());

        JSONArray fieldsArray = new JSONArray();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // Make private fields accessible

            Prompt promptAnnotation = null;
            if (field.isAnnotationPresent(Prompt.class)) {
                promptAnnotation = field.getAnnotation(Prompt.class);
                if(promptAnnotation.ignore())
                    continue; // Skip if ignore is true
            }

            JSONObject fieldJson = new JSONObject();
            fieldJson.put("fieldName", field.getName());

            // Similar logic to what was previously applied to method parameters
            if (!field.getType().isPrimitive() && !field.getType().equals(String.class)
                    && !field.getType().equals(Date.class) && !field.getType().isArray()) {
                JSONArray innerFieldsDetails = new JSONArray();
                for (Field innerField : field.getType().getDeclaredFields()) {
                    Object innerFieldJson = getJsonObject(innerField); // Assuming getJsonObject handles field details
                    if(innerFieldJson != null) {
                        innerFieldsDetails.put(innerFieldJson);
                    }
                }
                fieldJson.put("fields", innerFieldsDetails);
                fieldJson.put("fieldType", field.getType().getName());
                fieldJson.put("className", field.getType().getName());
            } else if (field.getType().isArray()) {
                Class<?> componentType = field.getType().getComponentType();
                fieldJson.put("isArray", true);
                fieldJson.put("fieldType", componentType.getSimpleName() + "[]");
                fieldJson.put("fieldValue", "");
            } else {
                fieldJson.put("fieldType", field.getType().getSimpleName());
                fieldJson.put("fieldValue", "");
            }

            // Handling Prompt annotation details if present
            if (promptAnnotation != null) {
                if (!promptAnnotation.describe().isEmpty()) {
                    fieldJson.put("description", promptAnnotation.describe());
                }
                if (!promptAnnotation.dateFormat().isEmpty()) {
                    fieldJson.put("dateFormat", promptAnnotation.dateFormat());
                }
            }

            fieldsArray.put(fieldJson);
        }

        classJson.put("fields", fieldsArray);
        return classJson.toString(4); // Pretty print
    }
    public String convertMethodTOJsonString(Method method) {


        JSONObject methodJson = new JSONObject();
        methodJson.put("methodName", method.getName());

        JSONArray parameters = new JSONArray();
        for (Parameter parameter : method.getParameters()) {
            Prompt promptAnnotation = null;
            if (parameter.isAnnotationPresent(Prompt.class)) {
                 promptAnnotation = parameter.getAnnotation(Prompt.class);
                 if(promptAnnotation.ignore())
                     continue;
            }
            JSONObject paramJson = new JSONObject();

            paramJson.put("name", parameter.getName());

            // Handle custom object types by inspecting their structure
            if (!parameter.getType().isPrimitive() && !parameter.getType().equals(String.class)
                    && !parameter.getType().equals(Date.class) && !parameter.getType().isArray()) {
                JSONArray fieldDetails = new JSONArray();
                for (Field field : parameter.getType().getDeclaredFields()) {
                    Object fieldJson = getJsonObject(field);
                    if(fieldJson != null) {
                        fieldDetails.put(fieldJson);
                    }
                }
                paramJson.put("fields", fieldDetails);
                paramJson.put("type", parameter.getType().getName());
            } else if (parameter.getType().isArray()) {
                // This is an array type
                Class<?> componentType = parameter.getType().getComponentType();
                paramJson.put("isArray", true);
                paramJson.put("type", componentType.getSimpleName() + "[]"); // Append "[]" for array types
            } else {
                paramJson.put("type", parameter.getType().getSimpleName());
                Annotation[] annotations = parameter.getDeclaredAnnotations();
                JSONObject fieldJson = new JSONObject();
                if (promptAnnotation!= null) {

                    // Check if describe field is present in @Prompt annotation
                    if (!promptAnnotation.describe().isEmpty()) {
                        paramJson.put("fieldDescription", promptAnnotation.describe());
                    }

                    // Check if format field is present in @Prompt annotation
                    if (!promptAnnotation.dateFormat().isEmpty()) {
                        paramJson.put("dateFormat", promptAnnotation.dateFormat());
                    }
                }
                paramJson.put("fieldValue", "");
            }

            parameters.put(paramJson);
        }

        methodJson.put("parameters", parameters);
        methodJson.put("returnType", method.getReturnType().getSimpleName());

        log.info(methodJson.toString(4)); // Pretty print with indentation
        return methodJson.toString(4);

    }


    private static Object getJsonObject(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        JSONObject fieldJson = new JSONObject();
        Prompt promptAnnotation = null;
        if (field.isAnnotationPresent(Prompt.class)) {
             promptAnnotation = field.getAnnotation(Prompt.class);

        }
        if((promptAnnotation !=null)&&(promptAnnotation.ignore())) {
            return null;
        }
        if(promptAnnotation!= null) {
            // Check if describe field is present in @Prompt annotation
            if (!promptAnnotation.describe().isEmpty()) {
                fieldJson.put("fieldDescription", promptAnnotation.describe());
            }

            // Check if format field is present in @Prompt annotation
            if (!promptAnnotation.dateFormat().isEmpty()) {
                fieldJson.put("dateFormat", promptAnnotation.dateFormat());
            }
        }
        fieldJson.put("fieldName", field.getName());
        Class<?> fieldType = field.getType();
        if (!fieldType.isPrimitive() && !fieldType.equals(String.class)
                && !fieldType.equals(Date.class)&&!fieldType.isArray()) {
            JSONArray fieldDetails = new JSONArray();
            for (Field childfield : field.getType().getDeclaredFields()) {
                Object childfieldJson = getJsonObject(childfield);
                if(childfieldJson!=null) {
                    fieldDetails.put(childfieldJson);
                }
            }
           fieldJson.put("fieldType", fieldType.getName());
           fieldJson.put("fields", fieldDetails);
           return fieldJson;
        } else if (fieldType.isArray()) {
            // This is an array type
            Class<?> componentType = fieldType.getComponentType();
            fieldJson.put("isArray", true);
            fieldJson.put("type", componentType.getSimpleName() + "[]"); // Append "[]" for array types
        }
        fieldJson.put("fieldType", fieldType.getSimpleName());
        fieldJson.put("fieldValue", "");
        return fieldJson;
    }

    private static void processProperties(ObjectNode parentNode, Map<String, Schema> properties) {
        ObjectMapper mapper = new ObjectMapper();

        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String propertyName = entry.getKey();
            Schema propertySchema = entry.getValue();
            String value = "";
            if(propertySchema.getDefault() != null) {
                value = propertySchema.getDefault().toString();
            } else {
                value = propertySchema.getType();
            }
            parentNode.put(propertyName, value);
            if (propertySchema instanceof ObjectSchema) {
                ObjectNode objectNode = mapper.createObjectNode();
                parentNode.set(propertyName, objectNode);
                processProperties(objectNode, ((ObjectSchema) propertySchema).getProperties());
            }
        }
    }
}