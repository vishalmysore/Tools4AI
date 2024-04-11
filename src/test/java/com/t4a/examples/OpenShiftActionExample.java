package com.t4a.examples;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiActionProcessor;
import lombok.extern.java.Log;

@Log
public class OpenShiftActionExample {
    public static void main(String[] args) throws AIProcessingException {
        GeminiActionProcessor processor = new GeminiActionProcessor();
        log.info(processor.processSingleAction("can you provide me list of core V1 component status for the kubernetes cluster").toString());
    }
}
