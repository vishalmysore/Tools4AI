package com.t4a.examples.basic;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiActionProcessor;
import lombok.extern.java.Log;

@Log
public class NonPredictedActionExample {
    public void executeAction() throws AIProcessingException {
        GeminiActionProcessor processor = new GeminiActionProcessor();
        processor.processSingleAction(" I want to eat Halva for breakfast", new NonPredictionAction() );
    }
    public static void main(String[] args) throws AIProcessingException {
        NonPredictedActionExample ex = new NonPredictedActionExample();
        ex.executeAction();;
    }
}
