package com.t4a.processor;

import com.t4a.detect.ActionCallback;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AwareInterfacesTest {

    @AfterEach
    void tearDown() {
        ActionContext.clear();
    }

    @Test
    void testActionCallbackAware() {
        ActionCallbackAware aware = new ActionCallbackAware() {
        };
        ActionCallback callback = Mockito.mock(ActionCallback.class);

        aware.setCallback(callback);
        Assertions.assertEquals(callback, aware.getCallback());
        Assertions.assertEquals(callback, ActionContext.getCallback());
    }

    @Test
    void testProcessorAware() {
        ProcessorAware aware = new ProcessorAware() {
        };
        AIProcessor processor = Mockito.mock(AIProcessor.class);

        aware.setProcessor(processor);
        Assertions.assertEquals(processor, aware.getProcessor());
        Assertions.assertEquals(processor, ActionContext.getProcessor());
    }
}
