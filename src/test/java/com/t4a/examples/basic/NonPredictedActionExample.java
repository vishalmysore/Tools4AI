package com.t4a.examples.basic;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.ActionProcessor;
import lombok.extern.java.Log;

@Log
public class NonPredictedActionExample {
    public void executeAction() throws AIProcessingException {
        ActionProcessor processor = new ActionProcessor();
        processor.processSingleAction(" I want to eat Halva for breakfast", new NonPredictionAction() );
    }
    public static void main(String[] args) throws AIProcessingException {
        NonPredictedActionExample ex = new NonPredictedActionExample();
        ex.executeAction();;
    }
}
