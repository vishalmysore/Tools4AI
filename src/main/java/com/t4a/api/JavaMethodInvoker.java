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

interface ValueParser {
    Object parse(Object value, JSONObject paramObj);
    boolean canParse(Class<?> type);
}

@Slf4j
class DateParser implements ValueParser {
    public boolean canParse(Class<?> type) {
        return type == Date.class;
    }

    public Object parse(Object value, JSONObject paramObj) {
        if (value == null || !(value instanceof String) ||
                ((String)value).trim().length() <= 1) return null;
        try {
            return new SimpleDateFormat(paramObj.getString("dateFormat"))
                    .parse((String)value);
        } catch (ParseException e) {
            log.warn("Unable to parse date {} for {}",
                    value, paramObj.optString("fieldName"));
            return null;
        }
    }
}

class ValueParserFactory {
    private final List<ValueParser> parsers;

    public ValueParserFactory() {
        parsers = Arrays.asList(
                new DateParser()
        );
    }

    public ValueParser getParser(Class<?> type) {
        return parsers.stream()
                .filter(parser -> parser.canParse(type))
                .findFirst()
                .orElse(null);
    }
}

@Slf4j
public class JavaMethodInvoker {
    private final ValueParserFactory parserFactory;

    public JavaMethodInvoker() {
        this.parserFactory = new ValueParserFactory();
    }

    public Object[] parse(String jsonStr) {
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
                Class<?> parameterType = getType(type, paramObj);
                Object value;

                if (paramObj.has("fieldValue")) {
                    value = getValue(paramObj.get("fieldValue"), parameterType, paramObj);
                } else if (paramObj.has("fields")) {
                    value = createPOJO(paramObj.getJSONArray("fields"), Class.forName(type));
                } else {
                    throw new IllegalArgumentException("No value or fields found for parameter: " + paramObj.getString("name"));
                }

                parameterTypes.add(parameterType);
                parameterValues.add(value);
            }
            return returnObject;
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return new Object[0];
    }

    private Object getValue(Object value, Class<?> type, JSONObject paramObj) {
        log.info("parsing value type {}, value {}, param {}", type.getName(), value, paramObj.optString("fieldName"));
        if (value == null) return null;
        if (value instanceof String && ((String) value).trim().length() == 0) return null;

        ValueParser parser = parserFactory.getParser(type);
        if (parser != null) {
            return parser.parse(value, paramObj);
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
        } else if (type.isArray()) {
            return handleArrayType(value, type, paramObj);
        } else if (type.getName().equalsIgnoreCase("java.util.List")) {
            return value;
        } else {
            return handleCustomType(type);
        }
    }

    private Object handleArrayType(Object value, Class<?> type, JSONObject paramObj) {
        JSONArray jsonArray = paramObj.getJSONArray("fieldValue");
        int length = jsonArray.length();
        Class<?> componentType = type.getComponentType();
        Object array = Array.newInstance(componentType, length);

        for (int i = 0; i < length; i++) {
            JsonUtils utils = new JsonUtils();
            Object elementValue = null;
            try {
                Object insideObject = jsonArray.get(i);
                if (insideObject instanceof JSONObject) {
                    elementValue = utils.populateObject((JSONObject) insideObject, paramObj);
                } else {
                    elementValue = getValue(insideObject, componentType, paramObj);
                }
            } catch (Exception e) {
                log.warn("not able to populate {}", paramObj);
            }
            Array.set(array, i, elementValue);
        }
        return array;
    }

    private Object handleCustomType(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }


    @NotNull
    private Class<?> getType(String type, JSONObject jsonObject) throws ClassNotFoundException {

        Class<?> parameterType;
        if (type.endsWith("[]")) {
            String componentTypeName = type.substring(0, type.length() - 2);
            Class<?> componentType = getType(jsonObject.getString("className"), jsonObject);
            return Array.newInstance(componentType, 0).getClass();
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
        } else if (type.equalsIgnoreCase("list")) {
            parameterType = Class.forName("java.util.List");
        }  else if (type.equalsIgnoreCase("java.lang.Double")) {
            parameterType = Class.forName("java.lang.Double");
        }
        else if (type.equalsIgnoreCase("java.lang.Long")) {
            parameterType = Class.forName("java.lang.Long");
        } else if (type.equalsIgnoreCase("java.lang.Boolean")) {
            parameterType = Class.forName("java.lang.Boolean");
        } else if (type.equalsIgnoreCase("java.lang.Integer")) {
            parameterType = Class.forName("java.lang.Integer");
        }else {
            parameterType = Class.forName(type);
        }
        return parameterType;
    }

    public Object createPOJO(JSONArray fieldsArray, Class<?> clazz) throws Exception {

        Object instance;
        if (clazz.getName().equalsIgnoreCase("java.util.Map")) {
            instance = new HashMap<>();
            JsonUtils utls = new JsonUtils();
            utls.buildMapFromJsonArray(fieldsArray, (Map) instance);
        } else {
            Constructor<?> constructor = clazz.getConstructor();
            instance = constructor.newInstance();

            for (int i = 0; i < fieldsArray.length(); i++) {
                JSONObject fieldObj = fieldsArray.getJSONObject(i);
                String fieldName = fieldObj.getString("fieldName");
                String fieldType = fieldObj.getString("fieldType");
                Class<?> parameterType = getType(fieldType, fieldObj);
                Object fieldValue;

                if (fieldObj.has("fieldValue")) {
                    fieldValue = getValue(fieldObj.get("fieldValue"), parameterType, fieldObj);
                } else if (fieldObj.has("fields")) {
                    fieldValue = createPOJO(fieldObj.getJSONArray("fields"), Class.forName(fieldType));
                } else {
                    throw new IllegalArgumentException("No value or fields found for parameter: " + fieldObj.getString("name"));
                }

                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (fieldValue instanceof JSONObject || fieldValue instanceof JSONArray) {
                    if (fieldType.equalsIgnoreCase("list")) {
                        JSONArray listArray = fieldObj.getJSONArray("fieldValue");
                        String classNameList = fieldObj.getString("className");
                        Class listClazz = Class.forName(classNameList);

                        List objList = new ArrayList();
                        for (Object obj : listArray) {
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
                        fieldValue = createPOJO(fieldValue, Class.forName(fieldType));
                    }
                }
                if (fieldValue != null) {
                    field.set(instance, fieldValue);
                }
            }
        }
        return instance;
    }

    public Object createPOJO(Object fieldValue, Class<?> clazz) throws Exception {

        if (fieldValue instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) fieldValue;
            JSONArray jsonArray = (JSONArray) jsonObject.optJSONArray("fields");
            if (jsonArray != null) {
                return createPOJO(jsonArray, clazz);
            } else {
                jsonObject = (JSONObject) ((JSONObject) fieldValue).optJSONObject("fieldValue");
                return createPOJO(jsonObject, clazz);
            }
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