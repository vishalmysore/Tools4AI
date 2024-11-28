package com.t4a;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.t4a.annotations.ListType;
import com.t4a.annotations.MapKeyType;
import com.t4a.annotations.MapValueType;
import com.t4a.annotations.Prompt;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Utility class for handling JSON related operations.
 * This class provides methods for fetching group and action names from a JSON string,
 * extracting JSON from a string, converting class objects to JSON strings, populating class objects from JSON,
 * building maps and lists from JSON arrays, and more.
 * It also provides methods for handling Prompt annotations and converting methods to JSON strings.
 * This class uses the Jackson and Gson libraries for JSON processing and the Lombok library for logging.
 */
@Slf4j
public class JsonUtils {
    private static final String  FIELDTYPE=  "fieldType";
    private static final String  DATEFORMAT = "dateFormat";
    private static final String  FIELDS= "fields";
    private static final String  FIELDNAME= "fieldName";
    private static final String  FIELDVALUE_JSON = "fieldValue";
    private static final String CLASSNAME_JSON = "className";
    public static final String IF_YOU_FIND_MORE_THAN_1 = "If you find more than 1 ";
    public static final String ADD_IT_AS_ANOTHER_OBJECT_INSIDE_FIELD_VALUE = " add it as another object inside fieldValue";

    /**
     * Fetches the group name from a JSON string.
     * @param groupJson The JSON string to fetch the group name from.
     * @return The group name.
     */

    public String fetchGroupName(String groupJson) {
        groupJson = extractJson(groupJson);
        JSONObject obj = new JSONObject(groupJson);
        return obj.optString("groupName",null);

    }

    /**
     * Fetches the action name from a JSON string.
     * @param groupJson The JSON string to fetch the action name from.
     * @return The action name.
     */
    public String fetchActionName(@NotNull String groupJson) {
        if(groupJson.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be empty");
        }
        groupJson = extractJson(groupJson);
        JSONObject obj = new JSONObject(groupJson);
        String groupName = obj.optString("actionName",null);
        if(groupName == null) {
            groupName = groupJson;
        }
        return groupName;
    }

    /**
     * Extracts JSON from a string.
     * @param jsonString The string to extract JSON from.
     * @return The extracted JSON string.
     */
    public  String extractJson(String jsonString) {
        // Find the index of the first opening bracket
        int startIndex = jsonString.indexOf('{');
        // Find the index of the last closing bracket
        int endIndex = jsonString.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            // Extract the substring between the first '{' and the last '}'
            return jsonString.substring(startIndex, endIndex + 1);
        }
        return jsonString; // Return null if the structure is not as expected
    }

    /**
     * Converts a class object to a JSON string.
     * @param classSchema The class schema to convert to a JSON string.
     * @return The JSON string representation of the class object.
     */
    public  String convertClassObjectToJsonString(Schema<?> classSchema) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        // Process each property of the class schema recursively
        processProperties(rootNode, classSchema.getProperties());

        // Convert the JSON node to a string
        return rootNode.toString();
    }

    /**
     * Populates a class object from a JSON string.
     * @param json The JSON string to populate the class object from.
     * @return The populated class object.
     * @throws Exception If an error occurs during population.
     */

    public Object populateClassFromJson(String json) throws Exception {
        json = extractJson(json);
        JSONObject jsonObject = new JSONObject(json);
        return populateObject(jsonObject,null);
    }

    /**
     * Builds a map from a JSON array.
     * @param fieldsArray The JSON array to build the map from.
     * @param map The map to populate.
     */
    public void buildMapFromJsonArray(JSONArray fieldsArray, Map<String,String> map){
        for (int i = 0; i < fieldsArray.length(); i++) {
            JSONObject jsonObject = fieldsArray.getJSONObject(i);
            String name = jsonObject.getString("key");
            String value = jsonObject.getString("value");
            map.put(name, value);
        }

    }

    /**
     * Builds a list from a JSON array.
     * @param fieldsArray The JSON array to build the list from.
     * @param list The list to populate.
     */
    public void buildListFromJsonArray(JSONArray fieldsArray, List<Object> list){
        for (int i = 0; i < fieldsArray.length(); i++) {
            list.add(fieldsArray.getJSONObject(i).get(FIELDVALUE_JSON));
        }

    }

    /**
     * Populates an object from a JSON object.
     * @param jsonObject The JSON object to populate the object from.
     * @param parentObject The parent JSON object.
     * @return The populated object.
     * @throws Exception If an error occurs during population.
     */
    public Object populateObject(JSONObject jsonObject, JSONObject parentObject) throws Exception {
        String className = jsonObject.optString(CLASSNAME_JSON,null);
        if((className == null)&&(parentObject!=null)){
            className = parentObject.optString(CLASSNAME_JSON,null);
        }
        if(className == null) {
            throw new IllegalArgumentException("Class name is missing in the JSON object");
        }
        Class<?> clazz = Class.forName(className);
        return populateObject(clazz,jsonObject);
    }

    /**
     *
     */
    public Object populateObject(Class<?> clazz,JSONObject jsonObject) {
        Object instance = null;
        try {
            if (clazz.getName().equalsIgnoreCase("java.util.Map")) {
                instance = new HashMap<>();
                JSONArray fieldsArray = jsonObject.getJSONArray(FIELDS);
                buildMapFromJsonArray(fieldsArray, (Map) instance);
                return instance;

            }
            if (clazz.getName().equalsIgnoreCase("java.util.List")) {
                instance = new ArrayList<>();
                JSONArray fieldsArray = jsonObject.getJSONArray(FIELDS);
                buildListFromJsonArray(fieldsArray, (List) instance);
                return instance;

            } else {
                instance = clazz.getDeclaredConstructor().newInstance();


                JSONArray fields = jsonObject.getJSONArray(FIELDS);
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject fieldObj = fields.getJSONObject(i);
                    String fieldName = fieldObj.getString(FIELDNAME);
                    String fieldType = fieldObj.getString(FIELDTYPE);
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    if ("String".equalsIgnoreCase(fieldType)) {
                        field.set(instance, fieldObj.optString(FIELDVALUE_JSON));
                    } else if (fieldType.endsWith("[]")) { // Check if the field is an array
                        JSONArray jsonArray = fieldObj.optJSONArray(FIELDVALUE_JSON);
                        if (jsonArray != null) {
                            String componentType = fieldType.substring(0, fieldType.indexOf('['));
                            Object array;

                            if ("int".equalsIgnoreCase(componentType)) {
                                array = new int[jsonArray.length()];
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    Array.setInt(array, j, jsonArray.optInt(j));
                                }
                            } else if ("double".equalsIgnoreCase(componentType)) {
                                array = new double[jsonArray.length()];
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    Array.setDouble(array, j, jsonArray.optDouble(j));
                                }
                            } else if ("long".equalsIgnoreCase(componentType)) {
                                array = new long[jsonArray.length()];
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    Array.setLong(array, j, jsonArray.optLong(j));
                                }
                            } else if ("boolean".equalsIgnoreCase(componentType)) {
                                array = new boolean[jsonArray.length()];
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    Array.setBoolean(array, j, jsonArray.optBoolean(j));
                                }
                            } else if ("String".equalsIgnoreCase(componentType)) {
                                array = new String[jsonArray.length()];
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    Array.set(array, j, jsonArray.optString(j));
                                }
                            } else if ("Date".equalsIgnoreCase(componentType)) {
                                array = new Date[jsonArray.length()];
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    String dateFormat = fieldObj.optString(DATEFORMAT);
                                    if (dateFormat.trim().isEmpty())
                                        dateFormat = "yyyy-MM-dd";
                                    SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                                    Array.set(array, j, format.parse(jsonArray.optString(j)));
                                }
                            } else {
                                // Handle other component types or custom objects
                                array = null; // Placeholder, adjust as necessary
                            }

                            field.set(instance, array);
                        }
                    } else if ("date".equalsIgnoreCase(fieldType)) {
                        String dateStr = null;
                        try {
                            dateStr =  fieldObj.getString(FIELDVALUE_JSON);
                            SimpleDateFormat sdf = new SimpleDateFormat(fieldObj.getString(DATEFORMAT));
                            Date d = sdf.parse(dateStr);
                            field.set(instance, d);

                        } catch (ParseException e) {
                            log.error("error parsing " + dateStr + " in format " + fieldObj.getString(DATEFORMAT) + " for " + fieldName + " for " + instance.getClass().getName());


                        }
                    } else if ( ("Integer".equalsIgnoreCase(fieldType)))
                    {
                        field.set(instance, fieldObj.getInt(FIELDVALUE_JSON));
                    } else if (("Float".equalsIgnoreCase(fieldType))) {
                        field.set(instance, fieldObj.getInt(FIELDVALUE_JSON));
                    }
                    else if (("Long".equalsIgnoreCase(fieldType))) {
                        field.set(instance, fieldObj.getLong(FIELDVALUE_JSON));
                    }
                    else if ("Double".equalsIgnoreCase(fieldType)) {
                        field.set(instance, fieldObj.getDouble(FIELDVALUE_JSON));
                    }
                    else if ("int".equalsIgnoreCase(fieldType)) {
                        field.setInt(instance, fieldObj.getInt(FIELDVALUE_JSON));
                    } else if ("boolean".equalsIgnoreCase(fieldType)) {
                        field.setBoolean(instance, fieldObj.getBoolean(FIELDVALUE_JSON));
                    } else if ("double".equalsIgnoreCase(fieldType)) {
                        // Checking and parsing double value
                        if (!fieldObj.optString(FIELDVALUE_JSON).isEmpty()) {
                            field.setDouble(instance, fieldObj.getDouble(FIELDVALUE_JSON));
                        }
                    } else if ("long".equalsIgnoreCase(fieldType)) {
                        // Checking and parsing long value
                        if (!fieldObj.optString(FIELDVALUE_JSON).isEmpty()) {
                            field.setLong(instance, fieldObj.getLong(FIELDVALUE_JSON));
                        }
                    } else if ("map".equalsIgnoreCase(fieldType)) {
                        Map map = new HashMap();
                        buildMapFromJsonArray(fieldObj.getJSONArray(FIELDS), map);
                        field.set(instance, map);

                    } else if ("list".equalsIgnoreCase(fieldType)) {
                        JSONArray listArray = fieldObj.getJSONArray(FIELDVALUE_JSON);
                        String classNameList = fieldObj.getString(CLASSNAME_JSON);
                        Class listClazz = Class.forName(classNameList);

                        List objList = new ArrayList();
                        for (Object obj : listArray
                        ) {
                            if (!listClazz.isPrimitive()
                                    && !listClazz.equals(String.class)
                                    && !listClazz.equals(Date.class)
                                    && !listClazz.isArray()
                                    && !List.class.isAssignableFrom(listClazz)) {
                                objList.add(listClazz.cast(populateObject((JSONObject) obj,fieldObj)));
                            } else {
                                objList.add(listClazz.cast(obj));
                            }
                        }

                        field.set(instance, objList);
                    } else if (fieldObj.has(FIELDS)) {
                        // Recursively populate nested objects
                        Object nestedInstance = populateObject(fieldObj,null);
                        field.set(instance, nestedInstance);
                    }
                    // Handle other types like Date here as in previous examples
                }


                return instance;
            }

        } catch (Exception e) {

            log.warn(" could not populate "+jsonObject);
        }
        return instance;
    }

    public String convertClassToJSONString(Class<?> clazz) {
        JSONObject classJson = getJsonObject(clazz);
        return classJson.toString(4); // Pretty print
    }

    @NotNull
    public JSONObject getJsonObject(Class<?> clazz) {
        JSONObject classJson = new JSONObject();
        classJson.put(CLASSNAME_JSON, clazz.getName());
        JSONArray fieldsArray = new JSONArray();
        for (Field field : clazz.getDeclaredFields()) {
            JSONObject fieldJson = getObject(field);
            if (fieldJson == null) continue; // Skip if ignore is true

            fieldsArray.put(fieldJson);
        }

        classJson.put(FIELDS, fieldsArray);
        return classJson;
    }

    public JSONObject buildBlankListJsonObject(Field field) {
        JSONObject fieldJson = new JSONObject();
        fieldJson.put(CLASSNAME_JSON, "java.util.List");
        fieldJson.put("prompt", "put each value inside fieldValue");
        JSONArray fieldsArray = new JSONArray();
        JSONObject object = new JSONObject();
        object.put(FIELDVALUE_JSON, "");
        fieldsArray.put(object);
        fieldJson.put(FIELDS, fieldsArray);
        if (field != null) {
            fieldJson.put("type", field.getType().getName());
            if (field.isAnnotationPresent(MapKeyType.class)) {
                Class<?> keyType = field.getAnnotation(MapKeyType.class).value();
                fieldJson.put("keyType", keyType.getName());
            } else {
                log.warn("Not able to derive the map Key type for " + field);
            }
            if (field.isAnnotationPresent(MapValueType.class)) {
                Class<?> valueType = field.getAnnotation(MapValueType.class).value();
                fieldJson.put("valueType", valueType.getName());
            } else {
                log.warn("Not able to derive the map Value type for " + field);
            }

        }
        return fieldJson;
    }
    public JSONObject buildBlankMapJsonObject(Field field) {
        JSONObject fieldJson = new JSONObject();
        fieldJson.put(CLASSNAME_JSON, "java.util.Map");
        JSONArray fieldsArray = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("key", "");
        object.put("value", "");
        fieldsArray.put(object);
        fieldJson.put(FIELDS, fieldsArray);
        if (field != null) {
            fieldJson.put("type", field.getType().getName());
            if (field.isAnnotationPresent(MapKeyType.class)) {
                Class<?> keyType = field.getAnnotation(MapKeyType.class).value();
                fieldJson.put("keyType", keyType.getName());
            } else {
                log.warn("Not able to derive the map Key type for " + field);
            }
            if (field.isAnnotationPresent(MapValueType.class)) {
                Class<?> valueType = field.getAnnotation(MapValueType.class).value();
                fieldJson.put("valueType", valueType.getName());
            } else {
                log.warn("Not able to derive the map Value type for " + field);
            }

        }
        return fieldJson;
    }

    public JSONObject getJsonObjectForList(Class<?> clazz, String fieldName) {
        JSONObject classJson = new JSONObject();
        classJson.put(CLASSNAME_JSON, clazz.getName());
        JSONArray fieldsArray = new JSONArray();
        for (Field field : clazz.getDeclaredFields()) {
            JSONObject fieldJson = getObject(field);
            if (fieldJson == null) continue; // Skip if ignore is true

            fieldsArray.put(fieldJson);
        }
        classJson.put(FIELDNAME, fieldName);
        classJson.put(FIELDTYPE, clazz.getName());
        classJson.put(FIELDS, fieldsArray);
        return classJson;
    }

    @Nullable
    private JSONObject getObject(Field field) {
        field.setAccessible(true); // Make private fields accessible

        Prompt promptAnnotation = null;
        if (field.isAnnotationPresent(Prompt.class)) {
            promptAnnotation = field.getAnnotation(Prompt.class);
            if (promptAnnotation.ignore())
                return null;
        }

        JSONObject fieldJson = new JSONObject();
        fieldJson.put(FIELDNAME, field.getName());

        if (field.getType().equals(Integer.class)) {
            fieldJson.put(FIELDTYPE, "Integer");
            fieldJson.put(FIELDVALUE_JSON, "");
        } else if (field.getType().equals(Double.class)) {
            fieldJson.put(FIELDTYPE, "Double");
            fieldJson.put(FIELDVALUE_JSON, "");
        } else if (field.getType().equals(Long.class)) {
            fieldJson.put(FIELDTYPE, "Long");
            fieldJson.put(FIELDVALUE_JSON, "");
        } else if (field.getType().equals(Float.class)) {
            fieldJson.put(FIELDTYPE, "Float");
            fieldJson.put(FIELDVALUE_JSON, "");
        } else if (!field.getType().isPrimitive()
                && !field.getType().equals(String.class)
                && !field.getType().equals(Date.class)
                && !field.getType().isArray()
                && !List.class.isAssignableFrom(field.getType())&&!Map.class.isAssignableFrom(field.getType())) {
            JSONArray innerFieldsDetails = new JSONArray();
            for (Field innerField : field.getType().getDeclaredFields()) {
                Object innerFieldJson = getJsonObject(innerField); // Assuming getJsonObject handles field details
                if (innerFieldJson != null) {
                    innerFieldsDetails.put(innerFieldJson);
                }
            }
            fieldJson.put(FIELDS, innerFieldsDetails);
            fieldJson.put(FIELDTYPE, field.getType().getName());
            fieldJson.put(CLASSNAME_JSON, field.getType().getName());
        } else if (List.class.isAssignableFrom(field.getType())) {
            addList(field, fieldJson);
        } else if (field.getType().isArray()) {
            Class<?> componentType = field.getType().getComponentType();
            fieldJson.put("isArray", true);
            fieldJson.put(FIELDTYPE, componentType.getSimpleName() + "[]");
            JSONArray array = new JSONArray();

            if (!componentType.isPrimitive() && !componentType.equals(String.class)
                    && !componentType.equals(Date.class) && !componentType.isArray() && !List.class.isAssignableFrom(componentType)) {
                array.put(getJsonObject(componentType));
            } else {
                array.put(componentType);
            }
            fieldJson.put(FIELDVALUE_JSON, array);
        } else if (Map.class.isAssignableFrom(field.getType())) {
            addMap(field, fieldJson);
        }else {
            fieldJson.put(FIELDTYPE, field.getType().getSimpleName());
            fieldJson.put(FIELDVALUE_JSON, "");
        }

        // Handling Prompt annotation details if present
        if (promptAnnotation != null) {
            if (!promptAnnotation.describe().isEmpty()) {
                fieldJson.put("description", promptAnnotation.describe());
            }
            if (!promptAnnotation.dateFormat().isEmpty()) {
                fieldJson.put(DATEFORMAT, promptAnnotation.dateFormat());
            }
        }
        return fieldJson;
    }

    private void addList(Field field, JSONObject fieldJson) {
        fieldJson.put(FIELDTYPE, "list");

        if (field.isAnnotationPresent(ListType.class)) {
            Class<?> listType = field.getAnnotation(ListType.class).value();

            JSONArray innerFieldsDetails = new JSONArray();
            if (!listType.isPrimitive()
                    && !listType.equals(String.class)
                    && !listType.equals(Date.class)
                    && !listType.isArray()
                    && !List.class.isAssignableFrom(listType)) {

                fieldJson.put(FIELDTYPE, "list");
                fieldJson.put(CLASSNAME_JSON, listType.getName());
                fieldJson.put(FIELDNAME, field.getName());
                fieldJson.put("prompt", IF_YOU_FIND_MORE_THAN_1 + listType.getSimpleName() + ADD_IT_AS_ANOTHER_OBJECT_INSIDE_FIELD_VALUE);
                innerFieldsDetails.put(getJsonObjectForList(listType, field.getName()));
                // innerFieldsDetails.put(getJsonObjectForList(listType, field.getName()));

            } else {
                fieldJson.put(CLASSNAME_JSON, listType.getName());
                fieldJson.put(FIELDTYPE, "list");
                fieldJson.put(FIELDNAME, field.getName());
                fieldJson.put("description", "there could be multiple " + listType.getSimpleName());
                innerFieldsDetails.put(listType.getName());
            }
            fieldJson.put(FIELDVALUE_JSON, innerFieldsDetails);

        } else {
            log.warn("Not able to derive the list type for " + field+" use the ListType annotation ");
        }
    }

    private void addMap(Field field, JSONObject fieldJson) {
        fieldJson.put(FIELDTYPE, "map");
        fieldJson.put("prompt", "create the key value pair and put in fields");
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("key", "");
        object.put("value", "");
        array.put(object);
        fieldJson.put(FIELDS, array);
        fieldJson.put("type", field.getType().getName());
        if (field.isAnnotationPresent(MapKeyType.class)) {
            Class<?> keyType = field.getAnnotation(MapKeyType.class).value();
            fieldJson.put("keyType", keyType.getName());
        } else {
            log.warn("Not able to derive the map Key type for " + field);
        }
        if (field.isAnnotationPresent(MapValueType.class)) {
            Class<?> valueType = field.getAnnotation(MapValueType.class).value();
            fieldJson.put("valueType", valueType.getName());
        } else {
            log.warn("Not able to derive the map Value type for " + field);
        }
    }

    public void addMap(Parameter field, JSONObject fieldJson) {
        fieldJson.put(FIELDTYPE, "map");
        fieldJson.put("prompt", "create the key value pair and put in fields");
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("key", "");
        object.put("value", "");
        array.put(object);
        fieldJson.put(FIELDS, array);
        fieldJson.put("type", field.getType().getName());
        if (field.isAnnotationPresent(MapKeyType.class)) {
            Class<?> keyType = field.getAnnotation(MapKeyType.class).value();
            fieldJson.put("keyType", keyType.getName());
        } else {
            log.warn("Not able to derive the map Key type for " + field);
        }
        if (field.isAnnotationPresent(MapValueType.class)) {
            Class<?> valueType = field.getAnnotation(MapValueType.class).value();
            fieldJson.put("valueType", valueType.getName());
        } else {
            log.warn("Not able to derive the map Value type for " + field);
        }
    }

    private void addList(Parameter field, JSONObject fieldJson) {
        fieldJson.put(FIELDTYPE, "list");

        if (field.isAnnotationPresent(ListType.class)) {
            Class<?> listType = field.getAnnotation(ListType.class).value();

            JSONArray innerFieldsDetails = new JSONArray();
            if (!listType.isPrimitive()
                    && !listType.equals(String.class)
                    && !listType.equals(Date.class)
                    && !listType.isArray()
                    && !List.class.isAssignableFrom(listType)) {

                fieldJson.put(FIELDTYPE, "list");
                fieldJson.put(CLASSNAME_JSON, listType.getName());
                fieldJson.put(FIELDNAME, field.getName());
                fieldJson.put("prompt", IF_YOU_FIND_MORE_THAN_1 + listType.getSimpleName() + ADD_IT_AS_ANOTHER_OBJECT_INSIDE_FIELD_VALUE);
                innerFieldsDetails.put(getJsonObjectForList(listType, field.getName()));

            } else {
                fieldJson.put(CLASSNAME_JSON, listType.getName());
                fieldJson.put(FIELDTYPE, "list");
                fieldJson.put(FIELDNAME, field.getName());
                fieldJson.put("description", "there could be multiple " + listType.getSimpleName());
                innerFieldsDetails.put(listType.getName());
            }
            fieldJson.put(FIELDVALUE_JSON, innerFieldsDetails);

        } else {
            log.warn("Not able to derive the list type for " + field);
        }
    }

    public String convertMethodTOJsonString(Method method) {


        JSONObject methodJson = new JSONObject();

        methodJson.put("methodName", method.getName());

        JSONArray parameters = new JSONArray();
        for (Parameter parameter : method.getParameters()) {
            Prompt promptAnnotation = null;
            if (parameter.isAnnotationPresent(Prompt.class)) {
                promptAnnotation = parameter.getAnnotation(Prompt.class);
                if (promptAnnotation.ignore())
                    continue;
            }
            JSONObject paramJson = new JSONObject();

            paramJson.put("name", parameter.getName());

            // Handle custom object types by inspecting their structure
            if (!parameter.getType().isPrimitive() && !parameter.getType().equals(String.class)
                    && !parameter.getType().equals(Date.class) && !parameter.getType().isArray() && !List.class.isAssignableFrom(parameter.getType()) &&
                    !Map.class.isAssignableFrom(parameter.getType())) {
                JSONArray fieldDetails = new JSONArray();
                for (Field field : parameter.getType().getDeclaredFields()) {
                    Object fieldJson = getJsonObject(field);
                    if (fieldJson != null) {
                        fieldDetails.put(fieldJson);
                    }
                }
                paramJson.put(FIELDS, fieldDetails);
                paramJson.put("type", parameter.getType().getName());
            } else if (parameter.getType().isArray()) {
                // This is an array type
                Class<?> componentType = parameter.getType().getComponentType();
                paramJson.put("isArray", true);
                paramJson.put("type", componentType.getSimpleName() + "[]"); // Append "[]" for array types
                paramJson.put(CLASSNAME_JSON, componentType.getName());
                JSONArray array = new JSONArray();

                if (!componentType.isPrimitive() && !componentType.equals(String.class)
                        && !componentType.equals(Date.class) && !componentType.isArray() && !List.class.isAssignableFrom(componentType)) {
                    array.put(getJsonObject(componentType));
                } else {
                    array.put(componentType);
                }
                paramJson.put(FIELDVALUE_JSON, array);
                paramJson.put("prompt", IF_YOU_FIND_MORE_THAN_1 + componentType.getSimpleName() + ADD_IT_AS_ANOTHER_OBJECT_INSIDE_FIELD_VALUE);
            } else if (List.class.isAssignableFrom(parameter.getType())) {
                addList(parameter, paramJson);
            } else if (Map.class.isAssignableFrom(parameter.getType())) {
                addMap(parameter, paramJson);
            } else {
                paramJson.put("type", parameter.getType().getSimpleName());


                paramJson.put(FIELDVALUE_JSON, "");
            }
            if (promptAnnotation != null) {

                // Check if describe field is present in @Prompt annotation
                if (!promptAnnotation.describe().isEmpty()) {
                    paramJson.put("fieldDescription", promptAnnotation.describe());
                }

                // Check if format field is present in @Prompt annotation
                if (!promptAnnotation.dateFormat().isEmpty()) {
                    paramJson.put(DATEFORMAT, promptAnnotation.dateFormat());
                }
            }
            parameters.put(paramJson);
        }

        methodJson.put("parameters", parameters);
        methodJson.put("returnType", method.getReturnType().getSimpleName());

        log.info(methodJson.toString(4)); // Pretty print with indentation
        return methodJson.toString(4);

    }


    private Object getJsonObject(Field field) {

        JSONObject fieldJson = new JSONObject();
        Prompt promptAnnotation = null;
        if (field.isAnnotationPresent(Prompt.class)) {
            promptAnnotation = field.getAnnotation(Prompt.class);

        }
        if ((promptAnnotation != null) && (promptAnnotation.ignore())) {
            return null;
        }
        if (promptAnnotation != null) {
            // Check if describe field is present in @Prompt annotation
            if (!promptAnnotation.describe().isEmpty()) {
                fieldJson.put("fieldDescription", promptAnnotation.describe());
            }

            // Check if format field is present in @Prompt annotation
            if (!promptAnnotation.dateFormat().isEmpty()) {
                fieldJson.put(DATEFORMAT, promptAnnotation.dateFormat());
            }
        }
        fieldJson.put(FIELDNAME, field.getName());
        Class<?> fieldType = field.getType();
        if (!fieldType.isPrimitive() && !fieldType.equals(String.class)
                && !fieldType.equals(Date.class) && !fieldType.isArray() && !List.class.isAssignableFrom(fieldType)) {
            JSONArray fieldDetails = new JSONArray();
            for (Field childfield : field.getType().getDeclaredFields()) {
                Object childfieldJson = getJsonObject(childfield);
                if (childfieldJson != null) {
                    fieldDetails.put(childfieldJson);
                }
            }
            fieldJson.put(FIELDTYPE, fieldType.getName());
            fieldJson.put(FIELDS, fieldDetails);
            return fieldJson;
        } else if (List.class.isAssignableFrom(fieldType)) {
            addList(field, fieldJson);
            return fieldJson;
        } else if (fieldType.isArray()) {
            // This is an array type
            Class<?> componentType = fieldType.getComponentType();
            fieldJson.put("isArray", true);
            fieldJson.put("type", componentType.getSimpleName() + "[]"); // Append "[]" for array types
            fieldJson.put(CLASSNAME_JSON, componentType.getName());
            JSONArray array = new JSONArray();

            if (!componentType.isPrimitive() && !componentType.equals(String.class)
                    && !componentType.equals(Date.class) && !componentType.isArray() && !List.class.isAssignableFrom(componentType)) {
                array.put(getJsonObject(componentType));
            } else {
                array.put(componentType);
            }
            fieldJson.put(FIELDVALUE_JSON, array);
            fieldJson.put("prompt", IF_YOU_FIND_MORE_THAN_1 + componentType.getSimpleName() + ADD_IT_AS_ANOTHER_OBJECT_INSIDE_FIELD_VALUE);
        }
        fieldJson.put(FIELDTYPE, fieldType.getSimpleName());
        if (!fieldJson.has(FIELDVALUE_JSON)) {
            fieldJson.put(FIELDVALUE_JSON, "");
        }
        return fieldJson;
    }

    public  void processProperties(ObjectNode parentNode, Map<String, Schema> properties) {
        ObjectMapper mapper = new ObjectMapper();

        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String propertyName = entry.getKey();
            Schema propertySchema = entry.getValue();
            String value;
            if (propertySchema.getDefault() != null) {
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

    /**
     * This will extract the values for the names from the prompt
     * for example if your prompt is like this
     * <p>
     * "My name is vishal and I live in Canada"
     * You can pass the keys like
     * name,location
     * {
     * fields : [
     * { 'fieldName':'name',
     *   'fieldValue':''
     * }
     * ,
     * { 'fieldName':'name',
     *   'fieldValue':''
     * }
     * ]
     * }
     */
    public  String createJson(String... keys) {
        if (keys == null || keys.length == 0) {
            throw new IllegalArgumentException("At least one key is required");
        }
        JsonObject rootObject = new JsonObject();
        JsonArray array = new JsonArray();
        rootObject.add(FIELDS,array);
        for (String key:keys
        ) {
            array.add(createJson(key));
        }


        return rootObject.toString();
    }

    /**
     * { 'fieldName':'name',
     *   'fieldValue':''
     * }
     */
    @NotNull
    public  JsonObject createJson(String key) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(FIELDNAME, key);
        jsonObj.addProperty(FIELDTYPE, "String");
        jsonObj.addProperty(FIELDVALUE_JSON, "");
        return jsonObj;
    }

    /**
     * Returns back field value
     */
    public String getFieldValue(String jsonStr, String fieldName) {
        jsonStr = extractJson(jsonStr);
        JSONObject obj = new JSONObject(jsonStr);
        String fieldValue = obj.optString(FIELDVALUE_JSON,null);
        if(fieldValue == null) {
            fieldValue = jsonStr;
        }
        log.debug(fieldName+" is the fieldName");
        return fieldValue;
    }
}