package com.t4a.regression;

import com.t4a.examples.basic.NonPredictionAction;
import com.t4a.processor.*;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Log
 class NonPredictedActionValidation {
    @Test
     void testGemini() throws AIProcessingException {
        AIProcessor processor = new GeminiActionProcessor();
        String str = (String)processor.processSingleAction(" I want to eat Halva for breakfast", new NonPredictionAction() );
        Assertions.assertEquals("Halva has too much sugar", str);
    }

    @Test
    void testGeminiV2() throws AIProcessingException {
        AIProcessor processor = new GeminiV2ActionProcessor();
        String str = (String)processor.processSingleAction(" I want to eat Halva for breakfast", new NonPredictionAction() );
        Assertions.assertEquals("Halva has too much sugar", str);
    }

    @Test
    void testAnthropic() throws AIProcessingException {
        AIProcessor processor = new AnthropicActionProcessor();
        String str = (String)processor.processSingleAction(" I want to eat Halva for breakfast", new NonPredictionAction() );
        Assertions.assertEquals("Halva has too much sugar", str);
    }

    @Test
    void testOpenAI() throws AIProcessingException {
        AIProcessor processor = new OpenAiActionProcessor();
        String str = (String)processor.processSingleAction(" I want to eat Halva for breakfast", new NonPredictionAction() );
        Assertions.assertEquals("Halva has too much sugar", str);
    }
}
