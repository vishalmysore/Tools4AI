package com.t4a.test;

import com.t4a.action.shell.ShellPredictedAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    public void testExecuteShell() throws Exception {
        // Mock Process
        Process process = mock(Process.class);

        // Mock InputStream for Process
        String mockOutput = "Mock output from script\n";
        InputStream mockInputStream = new ByteArrayInputStream(mockOutput.getBytes());
        when(process.getInputStream()).thenReturn(mockInputStream);



        // Mock exit code
        when(process.waitFor()).thenReturn(0);
        try (MockedConstruction<ProcessBuilder> mocked = Mockito.mockConstruction(ProcessBuilder.class, (mock, context) -> {
            when(mock.start()).thenReturn(process);
        })) {
            // Call the method under test
            ShellPredictedAction shellPredictedAction = new ShellPredictedAction();
            shellPredictedAction.setScriptPath("path/to/your/script"); // Set the script path here
            shellPredictedAction.executeShell(new String[]{});

            // Verify the interactions

            verify(process, times(1)).getInputStream();
            verify(process, times(1)).waitFor();
        }
    }
    @Test
    public void testDetectPathType_AbsolutePath() {
        // Arrange
        String scriptPath = "/path/to/file";
        try (MockedConstruction<File> mocked = mockConstruction(File.class,
                (mock, context) -> when(mock.isAbsolute()).thenReturn(true))) {
            // Act
            String result = shellPredictedAction.detectPathType(scriptPath);

            // Assert
            assertEquals("Absolute path", result);
        }
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
