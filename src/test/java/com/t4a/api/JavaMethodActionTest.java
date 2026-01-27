package com.t4a.api;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.processor.AIProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JavaMethodActionTest {

    @Agent(groupName = "testGroup", groupDescription = "testGroupDesc")
    static class MockAction implements JavaMethodAction {
        @Action(description = "testActionDesc")
        public void myMethod(String param) {
        }

        @Override
        public String getPrompt() {
            return "prompt";
        }

        @Override
        public String getSubprompt() {
            return "subprompt";
        }
    }

    static class MockActionNoAnnot implements JavaMethodAction {
        @Override
        public String getPrompt() {
            return "prompt";
        }

        @Override
        public String getSubprompt() {
            return "subprompt";
        }
    }

    public static class ComplexParam {
    }

    static class MockActionComplex implements JavaMethodAction {
        @Action
        public void complexMethod(ComplexParam param) {
        }

        @Override
        public String getPrompt() {
            return "prompt";
        }

        @Override
        public String getSubprompt() {
            return "subprompt";
        }
    }

    @Test
    void testJavaMethodActionDefaults() throws AIProcessingException {
        MockAction action = new MockAction();
        Assertions.assertEquals("myMethod", action.getActionName());
        Assertions.assertEquals("testActionDesc", action.getDescription());
        Assertions.assertEquals("testGroup", action.getActionGroup());
        Assertions.assertEquals("testGroupDesc", action.getGroupDescription());
        Assertions.assertFalse(action.isComplexMethod());
        Assertions.assertEquals(ActionRisk.LOW, action.getActionRisk());
        Assertions.assertEquals(ActionType.JAVAMETHOD, action.getActionType());
        Assertions.assertEquals(action, action.getActionInstance());
    }

    @Test
    void testNoAnnotation() {
        MockActionNoAnnot action = new MockActionNoAnnot();
        Assertions.assertEquals("No action name available", action.getActionName());
        Assertions.assertEquals("No description available", action.getDescription());
        Assertions.assertEquals("No group name available", action.getActionGroup());
    }

    @Test
    void testComplexMethod() throws AIProcessingException {
        MockActionComplex action = new MockActionComplex();
        // Since getActionName will return "complexMethod"
        Assertions.assertTrue(action.isComplexMethod());
    }

    @Test
    void testGetJsonRPC() {
        MockAction action = new MockAction();
        String json = action.getJsonRPC();
        Assertions.assertTrue(json.contains("\"actionName\":\"myMethod\""));
        Assertions.assertTrue(json.contains("\"actionClass\":\"com.t4a.api.JavaMethodActionTest$MockAction\""));
    }
}
