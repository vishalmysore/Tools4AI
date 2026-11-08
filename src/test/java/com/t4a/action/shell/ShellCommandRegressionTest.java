package com.t4a.action.shell;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

class ShellCommandRegressionTest {

    @Test
    void windowsRunsThroughCmd() {
        List<String> cmd = ShellPredictedAction.buildCommand("C:\\s\\run.cmd", new String[]{"Vishal", "Toronto"}, true);
        Assertions.assertEquals(Arrays.asList("cmd", "/c", "C:\\s\\run.cmd", "Vishal", "Toronto"), cmd);
    }

    @Test
    void windowsRejectsCommandChaining() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ShellPredictedAction.buildCommand("run.cmd", new String[]{"Vishal & del /q *"}, true));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ShellPredictedAction.buildCommand("run.cmd", new String[]{"a | b"}, true));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ShellPredictedAction.buildCommand("run.cmd", new String[]{"%PATH%"}, true));
    }

    @Test
    void unixRunsShellScriptsThroughShAndPassesArgumentsVerbatim() {
        List<String> cmd = ShellPredictedAction.buildCommand("/opt/run.sh", new String[]{"a & b"}, false);
        Assertions.assertEquals(Arrays.asList("sh", "/opt/run.sh", "a & b"), cmd);
    }

    @Test
    void unixExecutesOtherScriptsDirectly() {
        List<String> cmd = ShellPredictedAction.buildCommand("/opt/run", new String[]{}, false);
        Assertions.assertEquals(Arrays.asList("/opt/run"), cmd);
    }

    @Test
    void missingScriptIsNotTreatedAsAPath() {
        ShellPredictedAction action = new ShellPredictedAction();
        Assertions.assertNull(action.loadFromClasspath("does_not_exist_anywhere.cmd"));
        action.setScriptPath("does_not_exist_anywhere.cmd");
        Assertions.assertThrows(FileNotFoundException.class, () -> action.executeShell(new String[]{}));
    }

    @Test
    void executeShellDoesNotOverwriteScriptPath() throws Exception {
        ShellPredictedAction action = new ShellPredictedAction();
        action.setScriptPath("does_not_exist_anywhere.cmd");
        try {
            action.executeShell(new String[]{});
        } catch (FileNotFoundException expected) {
            // expected
        }
        Assertions.assertEquals("does_not_exist_anywhere.cmd", action.getScriptPath());
    }
}
