package com.t4a.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AIProcessingExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String message = "test error";
        AIProcessingException exception = new AIProcessingException(message);
        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    void testExceptionWithCause() {
        Exception cause = new RuntimeException("cause");
        AIProcessingException exception = new AIProcessingException(cause);
        Assertions.assertEquals(cause, exception.getCause());
    }
}
