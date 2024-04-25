package com.t4a.test;

import com.t4a.action.shell.ShellPredictedAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

 class ShellPredictedActionTest {

    private ShellPredictedAction shellPredictedAction;

    @BeforeEach
    public void setUp() {
        shellPredictedAction = Mockito.spy(new ShellPredictedAction());
        shellPredictedAction.setActionName("TestAction");
        shellPredictedAction.setDescription("Test Description");
        shellPredictedAction.setScriptPath("testScript.sh");
    }

    @Test
     void testExecuteShell() throws IOException, InterruptedException {
        String[] arguments = {"arg1", "arg2"};
        shellPredictedAction.executeShell(arguments);

        verify(shellPredictedAction, times(1)).executeShell(arguments);
    }

    @Test
     void testGetActionName() {
        String actionName = shellPredictedAction.getActionName();

        assertEquals("TestAction", actionName);
    }

    @Test
     void testGetDescription() {
        String description = shellPredictedAction.getDescription();

        assertEquals("Test Description", description);
    }
}
