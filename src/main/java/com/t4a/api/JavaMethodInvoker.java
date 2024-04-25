package com.t4a.api;

import com.t4a.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class JavaMethodInvoker {
    public JavaMethodInvoker() {

    }
    public  Object[] parse(String jsonStr) {
        JsonUtils utils = new JsonUtils();
        jsonStr = utils.extractJson(jsonStr);
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
                Class<?> parameterType = getType(type,paramObj);
                Object value;

                if (paramObj.has("fieldValue")) {
                    value = getValue(paramObj.get("fieldValue"),parameterType,paramObj);
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
    private  Object getValue(Object value, Class<?> type,JSONObject paramObj) {
        log.info("parsing value "+type.getName()+" value "+value+" for field Name "+paramObj.optString("fieldName"));
        if(value==null) return null;
        if(value instanceof  String) {
            if(((String)value).trim().length() == 0) return null;
        }
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
                  return new SimpleDateFormat(paramObj.getString("dateFormat")).parse((String) value);
                else
                    return null;
            } catch (ParseException e) {
                log.warn("not able to parse date "+value+" for field "+paramObj.optString("fieldName") );
                return null;
            }
        }else if (type.isArray()) {

            // Handle array types
            JSONArray jsonArray = paramObj.getJSONArray("fieldValue");
            int length = jsonArray.length();
            Class<?> componentType = type.getComponentType();
            Object array = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                JsonUtils utils = new JsonUtils();
                Object elementValue = null;
                try {

                    Object insideObject = jsonArray.get(i);
                    if(insideObject instanceof JSONObject){
                        elementValue = utils.populateObject((JSONObject) insideObject,paramObj);
                    } else {
                        elementValue = getValue(insideObject,componentType,paramObj);
                    }
                } catch (Exception e) {
                    log.warn("not able to populate "+paramObj);
                    //throw new RuntimeException(e);
                }

                Array.set(array, i, elementValue);
            }
            return array;
        }else if (type.getName().equalsIgnoreCase("java.util.List")) {
            return value;
        }else {
            // Handle custom class types
            JsonUtils jsonUtils = new JsonUtils();
            try {
                Constructor<?> constructor = type.getDeclaredConstructor(); // Specify parameter types
                constructor.setAccessible(true); // Make it accessible if it's private
                value = constructor.newInstance(); // Provide arguments
            } catch (Exception e) {
                e.printStackTrace(); // Handle exceptions appropriately
            }

            return value;
        }
    }
    @NotNull
    private  Class<?> getType(String type,JSONObject jsonObject) throws ClassNotFoundException {
        Class<?> parameterType;
        if (type.endsWith("[]")) {
            // This is an array type
            String componentTypeName = type.substring(0, type.length() - 2); // Remove "[]"
            Class<?> componentType = getType(jsonObject.getString("className"),jsonObject); // Recursively get the component type
            return Array.newInstance(componentType, 0).getClass(); // Create an array class of the component type
        }
        if (type.equalsIgnoreCase("String")) {
            parameterType = String.class;
        } else if (type.equalsIgnoreCase("int")) {
            parameterType = int.class;
        } else if (type.equalsIgnoreCase("double")) {
            parameterType = double.class;
        } else if (type.equalsIgnoreCase("boolean")) {
            parameterType = boolean.class;
        } else if (type.equalsIgnoreCase("Date")) {
            parameterType = java.util.Date.class;
            // Convert string value to Date
        } else if (type.equalsIgnoreCase("list")) {
            parameterType = Class.forName("java.util.List");
        }        else {
            // Handle custom class types
            parameterType = Class.forName(type);
        }
        return parameterType;
    }



    // Create POJO using reflection
    public  Object createPOJO(JSONArray fieldsArray, Class<?> clazz) throws Exception {
        Object instance = null;
        if(clazz.getName().equalsIgnoreCase("java.util.Map")){
            instance = new HashMap<>();
            JsonUtils utls = new JsonUtils();
            utls.buildMapFromJsonArray(fieldsArray,(Map)instance);
            return instance;
        } else {
            Constructor<?> constructor = clazz.getConstructor();
            instance = constructor.newInstance();

            for (int i = 0; i < fieldsArray.length(); i++) {
                JSONObject fieldObj = fieldsArray.getJSONObject(i);
                String fieldName = fieldObj.getString("fieldName");
                String fieldType = fieldObj.getString("fieldType");
                Class<?> parameterType = getType(fieldType, fieldObj);
                Object fieldValue = null;// getValue(fieldObj.get("fieldValue"),parameterType);
                if (fieldObj.has("fieldValue")) {
                    fieldValue = getValue(fieldObj.get("fieldValue"), parameterType, fieldObj);
                } else if (fieldObj.has("fields")) {
                    fieldValue = createPOJO(fieldObj.getJSONArray("fields"), Class.forName(fieldType));
                } else {
                    throw new IllegalArgumentException("No value or fields found for parameter: " + fieldObj.getString("name"));
                }
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                // Resolve nested POJOs recursively
                if (fieldValue instanceof JSONObject || fieldValue instanceof JSONArray) {
                    if (fieldType.equalsIgnoreCase("list")) {

                        JSONArray listArray = fieldObj.getJSONArray("fieldValue");
                        String classNameList = fieldObj.getString("className");
                        Class listClazz = Class.forName(classNameList);

                        List objList = new ArrayList();
                        for (Object obj : listArray
                        ) {
                            if (!listClazz.isPrimitive()
                                    && !listClazz.equals(String.class)
                                    && !listClazz.equals(Date.class)
                                    && !listClazz.isArray()
                                    && !List.class.isAssignableFrom(listClazz)) {
                                JsonUtils util = new JsonUtils();
                                objList.add(listClazz.cast(util.populateObject((JSONObject) obj, fieldObj)));
                            } else {
                                objList.add(listClazz.cast(obj));
                            }
                        }

                        fieldValue = objList;

                    } else {
                        if(fieldValue!= null) {
                            fieldValue = createPOJO(fieldValue, Class.forName(fieldType));
                        }

                    }
                    if(fieldValue!= null) {
                        field.set(instance, fieldValue);
                    }
                } else {
                    if(fieldValue!= null) {
                        field.set(instance, fieldValue);
                    }
                }


            }

            return instance;
        }
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
