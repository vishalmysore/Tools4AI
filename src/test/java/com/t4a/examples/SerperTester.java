package com.t4a.examples;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.ActionProcessor;

public class SerperTester {
    public static void main(String[] args) throws AIProcessingException {

        ActionProcessor processor = new ActionProcessor();
        String news = (String)processor.processSingleAction("search the web for India sweets");


    }
}
