package com.t4a.examples;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiActionProcessor;

import java.util.Properties;

public class SerperTester {
    public static void main(String[] args) throws AIProcessingException {

        Properties p = System.getProperties();

        GeminiActionProcessor processor = new GeminiActionProcessor();
        String news = (String)processor.processSingleAction("Can you get me  book with id 189");


    }
}
