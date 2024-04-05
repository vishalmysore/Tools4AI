package com.t4a.predict;

import com.t4a.processor.AIProcessingException;

public interface PromptTransformer {
    public  Object transformIntoPojo(String promptText, String className, String funName, String description) throws AIProcessingException;
    public String transformIntoJson(String jsonString, String promptText, String funName, String description) throws AIProcessingException;
}
