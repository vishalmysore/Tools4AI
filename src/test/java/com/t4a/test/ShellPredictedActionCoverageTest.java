package com.t4a.test;

import com.t4a.action.shell.ShellPredictedAction;
import com.t4a.api.ActionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShellPredictedActionCoverageTest {

    @Test
    public void testDetectPathTypeAbsolute() {
        String absolutePath = new java.io.File("script.cmd").getAbsolutePath();
        String result = ShellPredictedAction.detectPathType(absolutePath);
        Assertions.assertEquals("Absolute path", result);
    }

    @Test
    public void testDetectPathTypeRelative() {
        String relativePath = "scripts" + java.io.File.separator + "test.cmd";
        String result = ShellPredictedAction.detectPathType(relativePath);
        Assertions.assertEquals("Relative path", result);
    }

    @Test
    public void testDetectPathTypeFilenameOnly() {
        String result = ShellPredictedAction.detectPathType("test.cmd");
        Assertions.assertEquals("Filename only", result);
    }

    @Test
    public void testLoadFromAbsolutePath() {
        ShellPredictedAction action = new ShellPredictedAction();
        String absolutePath = new java.io.File("script.cmd").getAbsolutePath();
        String result = action.loadFromAbsolutePath(absolutePath);
        Assertions.assertTrue(result.contains("script.cmd"));
    }

    @Test
    public void testGetActionType() {
        ShellPredictedAction action = new ShellPredictedAction();
        Assertions.assertEquals(ActionType.SHELL, action.getActionType());
    }

    @Test
    public void testConstructor() {
        ShellPredictedAction action = new ShellPredictedAction("desc", "path", "name");
        Assertions.assertEquals("desc", action.getDescription());
        Assertions.assertEquals("path", action.getScriptPath());
        Assertions.assertEquals("name", action.getActionName());
    }
}
