package com.t4a.processor;

import com.t4a.detect.ActionCallback;
import com.t4a.detect.FeedbackLoop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessorExtraCoverageTest {

    @Test
    void testLoggingHumanDecision() {
        LoggingHumanDecision decision = new LoggingHumanDecision();
        Map<String, Object> params = new HashMap<>();
        params.put("key1", "value1");

        FeedbackLoop loop1 = decision.allow("test prompt", "testMethod", params);
        Assertions.assertTrue(loop1.isAIResponseValid());

        FeedbackLoop loop2 = decision.allow("test prompt", "testMethod", "paramString");
        Assertions.assertTrue(loop2.isAIResponseValid());
    }

    @Test
    void testLoggingExplainDecision() {
        LogginggExplainDecision explain = new LogginggExplainDecision();
        String reason = "the reason";
        String result = explain.explain("prompt", "method", reason);
        Assertions.assertEquals(reason, result);
    }

    @Test
    void testActionContextMultiThreading() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicBoolean thread1Ok = new AtomicBoolean(false);
        AtomicBoolean thread2Ok = new AtomicBoolean(false);

        ActionCallback callback1 = Mockito.mock(ActionCallback.class);
        ActionCallback callback2 = Mockito.mock(ActionCallback.class);

        Thread t1 = new Thread(() -> {
            ActionContext.setCallback(callback1);
            try {
                Thread.sleep(100);
                if (ActionContext.getCallback() == callback1) {
                    thread1Ok.set(true);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        Thread t2 = new Thread(() -> {
            ActionContext.setCallback(callback2);
            try {
                Thread.sleep(100);
                if (ActionContext.getCallback() == callback2) {
                    thread2Ok.set(true);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        t1.start();
        t2.start();

        latch.await(1, TimeUnit.SECONDS);
        Assertions.assertTrue(thread1Ok.get());
        Assertions.assertTrue(thread2Ok.get());

        ActionContext.clear();
        Assertions.assertNull(ActionContext.getCallback());
    }

    @Test
    void testActionContextProcessor() {
        AIProcessor processor = Mockito.mock(AIProcessor.class);
        ActionContext.setProcessor(processor);
        Assertions.assertEquals(processor, ActionContext.getProcessor());
        ActionContext.clear();
        Assertions.assertNull(ActionContext.getProcessor());
    }
}
