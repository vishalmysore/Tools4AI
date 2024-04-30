package com.t4a.test;



import com.t4a.processor.AIProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AIProcessingExceptionTest {

    @Test
    public void testAIProcessingException() {
        String message = "Test exception message";
        AIProcessingException exception = new AIProcessingException(message);

        assertEquals(message, exception.getMessage());
    }
}