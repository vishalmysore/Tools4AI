package com.t4a;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.t4a.predict.Prompt;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

    public String convertMethodTOJsonString(Method method) {


        JSONObject methodJson = new JSONObject();
        methodJson.put("methodName", method.getName());

        JSONArray parameters = new JSONArray();
        for (Parameter parameter : method.getParameters()) {
            JSONObject paramJson = new JSONObject();

            paramJson.put("name", parameter.getName());

            // Handle custom object types by inspecting their structure
            if (!parameter.getType().isPrimitive() && !parameter.getType().equals(String.class)
                    && !parameter.getType().equals(Date.class) && !parameter.getType().isArray()) {
                JSONArray fieldDetails = new JSONArray();
                for (Field field : parameter.getType().getDeclaredFields()) {
                    Object fieldJson = getJsonObject(field);
                    fieldDetails.put(fieldJson);
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
                if (parameter.isAnnotationPresent(Prompt.class)) {
                    Prompt promptAnnotation = parameter.getAnnotation(Prompt.class);

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

    @NotNull
    private static Object getJsonObject(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        JSONObject fieldJson = new JSONObject();
        if (field.isAnnotationPresent(Prompt.class)) {
            Prompt promptAnnotation = field.getAnnotation(Prompt.class);

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
                && !fieldType.equals(Date.class)) {
            JSONArray fieldDetails = new JSONArray();
            for (Field childfield : field.getType().getDeclaredFields()) {
                Object childfieldJson = getJsonObject(childfield);
                fieldDetails.put(childfieldJson);
            }
           fieldJson.put("fieldType", fieldType.getName());
           fieldJson.put("fields", fieldDetails);
           return fieldJson;
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