package com.t4a.test;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.Type;
import com.t4a.action.ExtendedInputParameter;
import com.t4a.action.ExtendedPredictedAction;
import com.t4a.action.http.HttpPredictedAction;
import com.t4a.action.shell.ShellPredictedAction;
import com.t4a.api.ActionType;
import com.t4a.api.JavaMethodExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JavaMethodExecutorCoverageTest {

    @Test
    public void testMapType() {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        Assertions.assertEquals(Type.STRING, executor.mapType(String.class));
        Assertions.assertEquals(Type.INTEGER, executor.mapType(int.class));
        Assertions.assertEquals(Type.INTEGER, executor.mapType(Integer.class));
        Assertions.assertEquals(Type.BOOLEAN, executor.mapType(boolean.class));
        // Assertions.assertEquals(Type.ARRAY, executor.mapType(String[].class));
        Assertions.assertEquals(Type.OBJECT, executor.mapType(Object.class));

        Assertions.assertEquals(Type.STRING, executor.mapType("String"));
        Assertions.assertEquals(Type.INTEGER, executor.mapType("int"));
        Assertions.assertEquals(Type.BOOLEAN, executor.mapType("boolean"));
    }

    @Test
    public void testActionShellMock() throws Exception {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        ShellPredictedAction mockAction = mock(ShellPredictedAction.class);
        when(mockAction.getActionType()).thenReturn(ActionType.SHELL);
        when(mockAction.getParameterNames()).thenReturn("p1,p2");
        when(mockAction.getActionName()).thenReturn("shellAction");

        Object result = executor.action("p1=val1,p2=val2", mockAction);

        Mockito.verify(mockAction).executeShell(new String[] { "val1", "val2" });
        Assertions.assertEquals("Executed shellAction", result);
    }

    @Test
    public void testActionHttpMock() throws Exception {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        HttpPredictedAction mockAction = mock(HttpPredictedAction.class);
        when(mockAction.getActionType()).thenReturn(ActionType.HTTP);

        when(mockAction.executeHttpRequest(any())).thenReturn("httpResponse");

        Object result = executor.action("k=v", mockAction);
        Assertions.assertEquals("httpResponse", result);
    }

    @Test
    public void testBuildFunctionShell() throws Exception {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        ShellPredictedAction mockAction = mock(ShellPredictedAction.class);
        when(mockAction.getActionType()).thenReturn(ActionType.SHELL);
        when(mockAction.getParameterNames()).thenReturn("p1");
        when(mockAction.getActionName()).thenReturn("shell");
        when(mockAction.getDescription()).thenReturn("desc");

        FunctionDeclaration fd = executor.buildFunction(mockAction);
        Assertions.assertNotNull(fd);
        Assertions.assertEquals("shell", fd.getName());
    }

    @Test
    public void testBuildFunctionHttp() throws Exception {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        HttpPredictedAction mockAction = mock(HttpPredictedAction.class);
        when(mockAction.getActionType()).thenReturn(ActionType.HTTP);
        when(mockAction.isHasJson()).thenReturn(false);
        when(mockAction.getInputObjects()).thenReturn(new ArrayList<>());
        when(mockAction.getActionName()).thenReturn("http");
        when(mockAction.getDescription()).thenReturn("desc");

        FunctionDeclaration fd = executor.buildFunction(mockAction);
        Assertions.assertNotNull(fd);
        Assertions.assertEquals("http", fd.getName());
    }

    @Test
    public void testBuildFunctionExtend() throws Exception {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        ExtendedPredictedAction mockAction = mock(ExtendedPredictedAction.class);
        when(mockAction.getActionType()).thenReturn(ActionType.EXTEND);
        when(mockAction.getActionName()).thenReturn("extend");
        when(mockAction.getDescription()).thenReturn("desc");

        List<ExtendedInputParameter> params = new ArrayList<>();
        params.add(new ExtendedInputParameter("p1", "String"));
        when(mockAction.getInputParameters()).thenReturn(params);

        FunctionDeclaration fd = executor.buildFunction(mockAction);
        Assertions.assertNotNull(fd);
        Assertions.assertEquals("extend", fd.getName());
    }

    @Test
    public void testActionExtend() throws Exception {
        JavaMethodExecutor executor = new JavaMethodExecutor();
        ExtendedPredictedAction mockAction = mock(ExtendedPredictedAction.class);
        when(mockAction.getActionType()).thenReturn(ActionType.EXTEND);
        when(mockAction.extendedExecute(any())).thenReturn("extendedResult");

        Object result = executor.action("k=v", mockAction);
        Assertions.assertEquals("extendedResult", result);
    }
}
