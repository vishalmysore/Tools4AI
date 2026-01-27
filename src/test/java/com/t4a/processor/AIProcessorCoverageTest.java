package com.t4a.processor;

import com.t4a.api.AIAction;
import com.t4a.api.JavaMethodAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import org.junit.jupiter.api.Test;

import com.t4a.annotations.Action;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AIProcessorCoverageTest {

    public static class ActionTestClass {
        @Action(description = "test")
        public void actionName() {
        }

        @Action(description = "test")
        public void doSomething() {
        }

        @Action(description = "test")
        public String sayHello(String name) {
            return "Hello " + name;
        }
    }

    private static class TestAIProcessor implements AIProcessor {
        @Override
        public Object processSingleAction(String promptText, AIAction action, HumanInLoop humanVerification,
                ExplainDecision explain) throws AIProcessingException {
            return "processed";
        }

        @Override
        public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain)
                throws AIProcessingException {
            return "processed";
        }

        @Override
        public Object processSingleAction(String promptText) throws AIProcessingException {
            return "processed";
        }

        @Override
        public Object processSingleAction(String promptText, ActionCallback callback) throws AIProcessingException {
            return "processed";
        }

        @Override
        public String query(String promptText) throws AIProcessingException {
            return "response for " + promptText;
        }

        @Override
        public Object processSingleAction(String prompt, AIAction action, HumanInLoop humanVerification,
                ExplainDecision explain, ActionCallback callback) throws AIProcessingException {
            return "processed";
        }
    }

    @Test
    public void testQueryMethods() throws AIProcessingException {
        TestAIProcessor processor = new TestAIProcessor();
        assertEquals("response for  this was my question { q} context - a", processor.query("q", "a"));
        assertEquals("response for  this was my question { q} context - {\"key\":\"val\"}",
                processor.query("q", new Object() {
                    public String key = "val";
                }));
        assertEquals("response for  Summarize this { text}", processor.summarize("text"));
    }

    @Test
    public void testProcessSingleActionDefaults() throws AIProcessingException {
        TestAIProcessor processor = new TestAIProcessor();
        ActionTestClass actionInstance = new ActionTestClass();
        assertEquals("processed", processor.processSingleAction("prompt", mock(HumanInLoop.class)));
        assertEquals("processed", processor.processSingleAction("prompt", actionInstance, "actionName"));
        assertEquals("processed", processor.processSingleAction("prompt", actionInstance));
        assertEquals("processed", processor.processSingleAction("prompt", actionInstance, mock(ActionCallback.class)));
    }

    @Test
    public void testInvokeReflection() throws Exception {
        TestAIProcessor processor = new TestAIProcessor();
        JavaMethodAction mockAction = mock(JavaMethodAction.class);
        ActionTestClass instance = new ActionTestClass();
        when(mockAction.getActionInstance()).thenReturn(instance);
        Method method = instance.getClass().getMethod("sayHello", String.class);
        List<Object> params = new ArrayList<>();
        params.add("World");

        Object result = processor.invokeReflection(method, mockAction, params);
        assertEquals("Hello World", result);
    }

    @Test
    public void testCleanUpThreadLocal() throws IllegalAccessException {
        TestAIProcessor processor = new TestAIProcessor();
        JavaMethodAction mockAction = mock(JavaMethodAction.class);

        class ActionWithThreadLocal {
            public ThreadLocal<ActionCallback> callback = new ThreadLocal<>();
        }

        ActionWithThreadLocal instance = new ActionWithThreadLocal();
        instance.callback.set(mock(ActionCallback.class));
        assertNotNull(instance.callback.get());

        when(mockAction.getActionInstance()).thenReturn(instance);
        processor.cleanUpThreadLocal(mockAction);
        assertNull(instance.callback.get());
    }

    @Test
    public void testSetCallBack() throws IllegalAccessException {
        TestAIProcessor processor = new TestAIProcessor();
        ActionCallback callback = mock(ActionCallback.class);
        JavaMethodAction javaMethodAction = mock(JavaMethodAction.class);

        ActionCallbackAware awareInstance = mock(ActionCallbackAware.class);
        when(javaMethodAction.getActionInstance()).thenReturn(awareInstance);

        processor.setCallBack(callback, javaMethodAction);
        verify(awareInstance).setCallback(callback);
    }

    @Test
    public void testResolveActualClass() {
        TestAIProcessor processor = new TestAIProcessor();
        Object obj = new String("test");
        assertEquals(String.class, processor.resolveActualClass(obj));
    }

    @Test
    public void testSetProcessor() {
        TestAIProcessor processor = new TestAIProcessor();
        JavaMethodAction javaMethodAction = mock(JavaMethodAction.class);
        ProcessorAware awareInstance = mock(ProcessorAware.class);
        when(javaMethodAction.getActionInstance()).thenReturn(awareInstance);

        processor.setProcessor(javaMethodAction);
        verify(awareInstance).setProcessor(processor);
    }
}
