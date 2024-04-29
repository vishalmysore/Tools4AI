package com.t4a.test;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiV2ActionProcessor;
import com.t4a.processor.scripts.ScriptProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

public class ScriptProcessorTest {

    @Test
     void testScriptProcessor() throws AIProcessingException {
        // Arrange
        try (MockedConstruction<GeminiV2ActionProcessor> mockProcessor = Mockito.mockConstruction(GeminiV2ActionProcessor.class,
                (mock, context) -> {
                    Mockito.when(mock.processSingleAction(anyString())).thenReturn("success");
                    Mockito.when(mock.query(anyString())).thenReturn("yes");
                    Mockito.when(mock.query(anyString(), anyString())).thenReturn("yes");
                })) {


            // Act
            ScriptProcessor scriptProcessor = new ScriptProcessor();

            // Assert
            assertNotNull(scriptProcessor);
            //get path of the file


            assertNotNull(scriptProcessor.process("test.action"));
            assertEquals("success", scriptProcessor.process("test.action").getResults().get(0).getResult());
            assertEquals("yes",scriptProcessor.summarize(scriptProcessor.process("test.action")));
        }
    }
}
