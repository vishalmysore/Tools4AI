package com.t4a.examples;

import com.t4a.processor.OpenAiActionProcessor;

import java.lang.reflect.InvocationTargetException;

public class OpenAIActionTester  {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        String prompt = "post a book to http post 'harry poster and problem rat' with id 978 by author vishal";
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String result = (String)processor.processSingleAction(prompt);
        System.out.println(result);
    }

}
