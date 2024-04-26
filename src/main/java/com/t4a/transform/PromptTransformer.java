package com.t4a.transform;

import com.t4a.JsonUtils;
import com.t4a.processor.AIProcessingException;

/**
 * The PromptTransformer interface provides methods for transforming prompts into Java POJOs and JSON.
 * It is used to convert a given prompt text into a specific format based on the provided class name, function name, and description.
 */

public interface PromptTransformer {
    public default String transformIntoJson(String jsonString, String promptText) throws AIProcessingException{
        return transformIntoJson(jsonString,promptText,"get me values", "Get me the values in json");
    }
    public String transformIntoJson(String jsonString, String promptText, String funName, String description) throws AIProcessingException;
    public default  Object transformIntoPojo(String prompt, String className) throws AIProcessingException {
        return transformIntoPojo(prompt,  className,  "funName",  "description");
    }
    public default  Object transformIntoPojo(String prompt, Class<?> clazz) throws AIProcessingException {
        return transformIntoPojo(prompt,  clazz.getName(),  "funName",  "description");
    }

    public default Object transformIntoPojo(String prompt, String className, String funName, String description) throws AIProcessingException {
        try {
            JsonUtils util = new JsonUtils();
            Class<?> clazz = Class.forName(className);
            String jsonStr;
            if (clazz.getName().equalsIgnoreCase("java.util.Map")) {
                jsonStr = util.buildBlankMapJsonObject(null).toString(4);


            } else if (clazz.getName().equalsIgnoreCase("java.util.List")) {
                jsonStr = util.buildBlankListJsonObject(null).toString(4);


            } else {
                jsonStr = util.convertClassToJSONString(clazz);
            }

            jsonStr = getJSONResponseFromAI(prompt, jsonStr);

            jsonStr = jsonStr.trim();

            return util.populateClassFromJson(jsonStr);

        } catch (Exception e) {
            throw new AIProcessingException(e);
        }

    }

    public String getJSONResponseFromAI(String prompt, String jsonStr) throws AIProcessingException;
}
