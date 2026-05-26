package com.t4a.test;

import com.t4a.detect.ActionState;
import com.t4a.detect.SimpleActionCallback;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleActionCallbackTest {

    /**
     * Concrete implementation of the abstract SimpleActionCallback for testing.
     */
    static class TestCallback extends SimpleActionCallback {
        private String lastStatus;
        private ActionState lastState;

        @Override
        public void sendtStatus(String status, ActionState state) {
            lastStatus = status;
            lastState = state;
        }

        public String getLastStatus() {
            return lastStatus;
        }

        public ActionState getLastState() {
            return lastState;
        }
    }

    @Test
    void testGetType() {
        TestCallback callback = new TestCallback();
        assertEquals("simple", callback.getType());
    }

    @Test
    void testSetAndGetContext() {
        TestCallback callback = new TestCallback();
        AtomicReference<Object> ctx = new AtomicReference<>("hello");
        callback.setContext(ctx);
        assertSame(ctx, callback.getContext());
        assertEquals("hello", callback.getContext().get());
    }

    @Test
    void testInitialContextIsNull() {
        TestCallback callback = new TestCallback();
        assertNull(callback.getContext());
    }

    @Test
    void testSendtStatus() {
        TestCallback callback = new TestCallback();
        callback.sendtStatus("running", ActionState.WORKING);
        assertEquals("running", callback.getLastStatus());
        assertEquals(ActionState.WORKING, callback.getLastState());
    }
}
