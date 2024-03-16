package com.t4a.examples;

import com.t4a.processor.OpenAiActionProcessor;

import java.lang.reflect.InvocationTargetException;

public class OpenAIActionTester  {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        String prompt = "My friends name is Vishal ,I dont know what to cook for him today.";
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String result = (String)processor.processSingleAction(prompt);
        System.out.println(result);
    }

}
