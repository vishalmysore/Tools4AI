package com.t4a.predict;

import com.t4a.processor.AIProcessingException;

public interface PromptTransformer {
    public  Object transformIntoPojo(String promptText, String className, String funName, String description) throws AIProcessingException;
    public String transformIntoJson(String jsonString, String promptText, String funName, String description) throws AIProcessingException;
    public default  Object transformIntoPojo(String prompt, String className) throws AIProcessingException {
        return transformIntoPojo(prompt,  className,  "funName",  "description");
    }
    public default  Object transformIntoPojo(String prompt, Class clazz) throws AIProcessingException {
        return transformIntoPojo(prompt,  clazz.getName(),  "funName",  "description");
    }
}
