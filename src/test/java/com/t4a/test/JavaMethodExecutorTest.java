package com.t4a.test;

import com.t4a.action.BlankAction;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.api.JavaMethodExecutor;
import com.t4a.processor.AIProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JavaMethodExecutorTest {
    @Test
    void mapMethodTest() throws AIProcessingException {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        BlankAction action = new BlankAction();
        GenericJavaMethodAction genericJavaMethodAction = new GenericJavaMethodAction(action);
        executor.mapMethod(genericJavaMethodAction);
        Assertions.assertNotNull(executor.getProperties());
        Assertions.assertNotNull(executor.getProperties().get("additionalQuestion"));
    }

    @Test
    void mapMethodTestAction() throws AIProcessingException {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        MockAction action = new MockAction();

        executor.mapMethod(action);
        Assertions.assertNotNull(executor.getProperties());
        Assertions.assertNotNull(executor.getProperties().get("mockName"));
    }
}
