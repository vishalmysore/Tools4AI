package com.t4a.examples;

import com.t4a.processor.ActionProcessor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class SerperTester {
    public static void main(String[] args) throws IOException, InvocationTargetException, IllegalAccessException {

        ActionProcessor processor = new ActionProcessor();
        String news = (String)processor.processSingleAction("can you search the web for Indian news");


    }
}
