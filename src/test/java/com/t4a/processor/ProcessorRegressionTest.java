package com.t4a.processor;

import com.t4a.annotations.Action;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ActionState;
import com.t4a.detect.FeedbackLoop;
import com.t4a.detect.HumanInLoop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Regression tests for the shared {@link AIProcessor} helpers: callback injection, method resolution and the
 * null-safe human verification check.
 */
class ProcessorRegressionTest {

    public static class FieldCallbackAction {
        public ActionCallback callback;

        @Action(description = "uses a plain callback field")
        public String run(String value) {
            return value;
        }
    }

    public static class ThreadLocalCallbackAction {
        public ThreadLocal<ActionCallback> callback;

        @Action(description = "uses a thread-local callback field")
        public String run(String value) {
            return value;
        }
    }

    public static class OverloadedAction {
        public String search(int id) {
            return "int";
        }

        @Action(description = "the annotated overload")
        public String search(String query) {
            return "string";
        }
    }

    private static ActionCallback callback() {
        return new ActionCallback() {
            private AtomicReference<Object> context;

            @Override
            public void setContext(AtomicReference<Object> context) {
                this.context = context;
            }

            @Override
            public AtomicReference<Object> getContext() {
                return context;
            }

            @Override
            public String getType() {
                return "test";
            }

            @Override
            public void sendtStatus(String status, ActionState state) {
            }
        };
    }

    @Test
    void setCallBackInjectsPlainField() throws Exception {
        FieldCallbackAction target = new FieldCallbackAction();
        ActionCallback cb = callback();
        new OpenAiActionProcessor().setCallBack(cb, new GenericJavaMethodAction(target));
        Assertions.assertSame(cb, target.callback);
    }

    @Test
    void setCallBackInjectsThreadLocalField() throws Exception {
        ThreadLocalCallbackAction target = new ThreadLocalCallbackAction();
        ActionCallback cb = callback();
        new OpenAiActionProcessor().setCallBack(cb, new GenericJavaMethodAction(target));
        Assertions.assertNotNull(target.callback);
        Assertions.assertSame(cb, target.callback.get());
    }

    @Test
    void findActionMethodPrefersAnnotatedOverload() throws Exception {
        GenericJavaMethodAction action = new GenericJavaMethodAction(new OverloadedAction());
        Method method = new OpenAiActionProcessor().findActionMethod(action);
        Assertions.assertArrayEquals(new Class<?>[]{String.class}, method.getParameterTypes());
    }

    @Test
    void nullHumanInLoopMeansApproved() {
        Assertions.assertTrue(new OpenAiActionProcessor().isApprovedByHuman(null, "p", "a", "{}"));
    }

    @Test
    void rejectingHumanInLoopIsHonoured() {
        HumanInLoop reject = new HumanInLoop() {
            @Override
            public FeedbackLoop allow(String promptText, String methodName, Map<String, Object> params) {
                return () -> false;
            }

            @Override
            public FeedbackLoop allow(String promptText, String methodName, String params) {
                return () -> false;
            }
        };
        Assertions.assertFalse(new OpenAiActionProcessor().isApprovedByHuman(reject, "p", "a", "{}"));
    }
}
