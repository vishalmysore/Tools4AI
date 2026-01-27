package com.t4a.processor;

import com.t4a.detect.ActionCallback;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ActionContextTest {

    @AfterEach
    void tearDown() {
        ActionContext.clear();
    }

    @Test
    void testCallback() {
        ActionCallback callback = Mockito.mock(ActionCallback.class);
        ActionContext.setCallback(callback);
        Assertions.assertEquals(callback, ActionContext.getCallback());
    }

    @Test
    void testProcessor() {
        AIProcessor processor = Mockito.mock(AIProcessor.class);
        ActionContext.setProcessor(processor);
        Assertions.assertEquals(processor, ActionContext.getProcessor());
    }

    @Test
    void testClear() {
        ActionCallback callback = Mockito.mock(ActionCallback.class);
        AIProcessor processor = Mockito.mock(AIProcessor.class);
        ActionContext.setCallback(callback);
        ActionContext.setProcessor(processor);

        ActionContext.clear();

        Assertions.assertNull(ActionContext.getCallback());
        Assertions.assertNull(ActionContext.getProcessor());
    }

    @Test
    void testThreadIsolation() throws InterruptedException {
        ActionCallback callback1 = Mockito.mock(ActionCallback.class);
        ActionContext.setCallback(callback1);

        Thread thread = new Thread(() -> {
            Assertions.assertNull(ActionContext.getCallback());
            ActionCallback callback2 = Mockito.mock(ActionCallback.class);
            ActionContext.setCallback(callback2);
            Assertions.assertEquals(callback2, ActionContext.getCallback());
        });
        thread.start();
        thread.join();

        Assertions.assertEquals(callback1, ActionContext.getCallback());
    }
}
