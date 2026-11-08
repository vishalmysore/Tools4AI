package com.t4a.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LocalAIActionProcessorTest {

    @Test
    void testDelegatesToOpenAiCompatibleProcessor() {
        LocalAIActionProcessor processor = new LocalAIActionProcessor();
        Assertions.assertTrue(processor instanceof OpenAiActionProcessor);
    }
}
