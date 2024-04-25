package com.t4a.transform;

import com.t4a.processor.AIProcessingException;
/**
 * The PromptTransformer interface provides methods for transforming prompts into Java POJOs and JSON.
 * It is used to convert a given prompt text into a specific format based on the provided class name, function name, and description.
 */
public interface PromptTransformer {
    public  Object transformIntoPojo(String promptText, String className, String funName, String description) throws AIProcessingException;
    public String transformIntoJson(String jsonString, String promptText, String funName, String description) throws AIProcessingException;
    public default  Object transformIntoPojo(String prompt, String className) throws AIProcessingException {
        return transformIntoPojo(prompt,  className,  "funName",  "description");
    }
    public default  Object transformIntoPojo(String prompt, Class<?> clazz) throws AIProcessingException {
        return transformIntoPojo(prompt,  clazz.getName(),  "funName",  "description");
    }
}
