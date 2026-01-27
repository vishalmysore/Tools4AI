package com.t4a.api;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.gson.Gson;
import com.t4a.action.ExtendedPredictedAction;
import com.t4a.action.http.HttpPredictedAction;
import com.t4a.action.shell.ShellPredictedAction;
import com.t4a.processor.AIProcessingException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JavaMethodExecutorMoreCoverageTest {

    public static class TestAction implements JavaMethodAction {
        public String doSomething(String name, String age) {
            return "Hello " + name + " " + age;
        }

        @Override
        public String getActionName() {
            return "doSomething";
        }

        @Override
        public String getDescription() {
            return "test description";
        }

        @Override
        public String getPrompt() {
            return "prompt";
        }

        @Override
        public String getSubprompt() {
            return "subprompt";
        }

        @Override
        public Object getActionInstance() {
            return this;
        }
    }

    @Test
    public void testBuildFunctionJavaMethod() throws AIProcessingException {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        TestAction action = new TestAction();
        FunctionDeclaration func = executor.buildFunction(action);
        assertNotNull(func);
        assertEquals("doSomething", func.getName());
        assertFalse(executor.getProperties().isEmpty());
    }

    @Test
    public void testBuildFunctionShell() throws AIProcessingException {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        ShellPredictedAction shellAction = mock(ShellPredictedAction.class);
        when(shellAction.getActionType()).thenReturn(ActionType.SHELL);
        when(shellAction.getActionName()).thenReturn("shellAction");
        when(shellAction.getDescription()).thenReturn("shell desc");
        when(shellAction.getParameterNames()).thenReturn("param1,param2");

        FunctionDeclaration func = executor.buildFunction(shellAction);
        assertNotNull(func);
        assertEquals("shellAction", func.getName());
        assertTrue(executor.getProperties().containsKey("param1"));
        assertTrue(executor.getProperties().containsKey("param2"));
    }

    @Test
    public void testBuildFunctionHttp() throws AIProcessingException {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        HttpPredictedAction httpAction = mock(HttpPredictedAction.class);
        when(httpAction.getActionType()).thenReturn(ActionType.HTTP);
        when(httpAction.getActionName()).thenReturn("httpAction");
        when(httpAction.getDescription()).thenReturn("http desc");
        when(httpAction.isHasJson()).thenReturn(false);
        when(httpAction.getInputObjects()).thenReturn(new ArrayList<>());

        FunctionDeclaration func = executor.buildFunction(httpAction);
        assertNotNull(func);
        assertEquals("httpAction", func.getName());
    }

    @Test
    public void testBuildFunctionExtend() throws AIProcessingException {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        ExtendedPredictedAction extendedAction = mock(ExtendedPredictedAction.class);
        when(extendedAction.getActionType()).thenReturn(ActionType.EXTEND);
        when(extendedAction.getActionName()).thenReturn("extendAction");
        when(extendedAction.getDescription()).thenReturn("extend desc");
        when(extendedAction.getInputParameters()).thenReturn(new ArrayList<>());

        FunctionDeclaration func = executor.buildFunction(extendedAction);
        assertNotNull(func);
        assertEquals("extendAction", func.getName());
    }

    @Test
    public void testActionWithParams() throws InvocationTargetException, IllegalAccessException, AIProcessingException {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        TestAction action = new TestAction();
        executor.buildFunction(action);

        // Find actual parameter names
        Map<String, Object> props = executor.getProperties();
        String nameParam = "name";
        String ageParam = "age";

        // If not compiled with -parameters, they will be arg0, arg1
        if (!props.containsKey("name")) {
            for (String key : props.keySet()) {
                if (key.contains("arg")) {
                    if (nameParam.equals("name"))
                        nameParam = key;
                    else
                        ageParam = key;
                }
            }
        }

        String params = nameParam + "=World, " + ageParam + "=20";
        Object result = executor.action(params, action);
        assertEquals("Hello World 20", result);
    }

    @Test
    public void testActionWithArrayParams()
            throws InvocationTargetException, IllegalAccessException, AIProcessingException {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        TestAction action = new TestAction();
        executor.buildFunction(action);

        Object[] params = new Object[] { "World", "25" };
        Object result = executor.action(params, action);
        assertEquals("Hello World 25", result);
    }

    @Test
    public void testGettersSetters() {
        Gson gson = new Gson();
        JavaMethodExecutor executor = new JavaMethodExecutor(gson);
        assertEquals(gson, executor.getGson());
        assertNotNull(executor.getProperties());

        JavaMethodExecutor executor2 = new JavaMethodExecutor(null);
        assertNotNull(executor2.getGson());
    }

    @Test
    public void testActionFails() throws AIProcessingException {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        TestAction action = new TestAction();
        executor.buildFunction(action);

        // Providing wrong number of params or wrong types
        Object[] params = new Object[] { "OnlyOne" };
        Object result = executor.action(params, action);
        assertEquals("{failed}", result);
    }
}
