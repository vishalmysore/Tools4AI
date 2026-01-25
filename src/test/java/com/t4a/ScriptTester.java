package com.t4a;

import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.scripts.ScriptProcessor;

import java.io.IOException;

public class ScriptTester {
    public static void main(String[] args) throws AIProcessingException, IOException {
        ScriptProcessor scriptProcessor = new ScriptProcessor(PredictionLoader.getInstance().createOrGetAIProcessor());
        scriptProcessor.processCommands(null,"hello",null);

    }
}
