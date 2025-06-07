package com.t4a.test;

import com.google.gson.Gson;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import com.t4a.processor.GeminiV2ActionProcessor;
import com.t4a.processor.scripts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ScriptProcessorTest {

    @Mock
    private GeminiV2ActionProcessor mockActionProcessor;

    @Mock
    private ScriptCallback mockCallback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBasicScriptProcessing() throws AIProcessingException {
        // Arrange
        try (MockedConstruction<GeminiV2ActionProcessor> mockProcessor = Mockito.mockConstruction(GeminiV2ActionProcessor.class,
                (mock, context) -> {
                    when(mock.processSingleAction(anyString())).thenReturn("success");
                    when(mock.query(anyString())).thenReturn("yes");
                    when(mock.query(anyString(), anyString())).thenReturn("yes");
                })) {

            ScriptProcessor scriptProcessor = new ScriptProcessor();

            // Assert basic functionality
            assertNotNull(scriptProcessor);
            ScriptResult result = scriptProcessor.process("test.action");
            assertNotNull(result);
            assertEquals("success", result.getResults().get(0).getResult());
            assertEquals("yes", scriptProcessor.summarize(result));
        }
    }

    @Test
    void testCustomConstructors() {
        // Test with Gson
        Gson gson = new Gson();
        ScriptProcessor withGson = new ScriptProcessor(gson);
        assertNotNull(withGson);
        assertEquals(gson, withGson.getGson());

        // Test with AIProcessor
        AIProcessor processor = mock(AIProcessor.class);
        ScriptProcessor withProcessor = new ScriptProcessor(processor);
        assertNotNull(withProcessor);
        assertEquals(processor, withProcessor.getActionProcessor());

        // Test with both
        ScriptProcessor withBoth = new ScriptProcessor(gson, processor);
        assertNotNull(withBoth);
        assertEquals(gson, withBoth.getGson());
        assertEquals(processor, withBoth.getActionProcessor());
    }

    @Test
    void testScriptLineResult() {
        // Test ScriptLineResult class
        String line = "test command";
        String result = "test result";
        ScriptLineResult lineResult = new ScriptLineResult(line, result);

        assertEquals(line, lineResult.getLine());
        assertEquals(result, lineResult.getResult());

        // Test default constructor
        ScriptLineResult emptyResult = new ScriptLineResult();
        assertNull(emptyResult.getLine());
        assertNull(emptyResult.getResult());
    }

    @Test
    void testScriptResult() {
        // Test ScriptResult management
        ScriptResult result = new ScriptResult();
        assertTrue(result.getResults().isEmpty());

        // Test adding results
        result.addResult("line1", "result1");
        assertEquals(1, result.getResults().size());
        assertEquals("line1", result.getResults().get(0).getLine());
        assertEquals("result1", result.getResults().get(0).getResult());

        // Test adding ScriptLineResult object
        ScriptLineResult lineResult = new ScriptLineResult("line2", "result2");
        result.addResult(lineResult);
        assertEquals(2, result.getResults().size());
        assertEquals("line2", result.getResults().get(1).getLine());
        assertEquals("result2", result.getResults().get(1).getResult());
    }

    @Test
    void testScriptProcessorWithCallback() throws AIProcessingException {
        // Arrange
        ScriptProcessor processor = new ScriptProcessor(mockActionProcessor);
        when(mockActionProcessor.query(anyString())).thenReturn("yes");
        when(mockActionProcessor.processSingleAction(anyString())).thenReturn("success");
        when(mockCallback.lineResult(anyString())).thenReturn("processed");

        // Create test content
        String content = "command1\ncommand2";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        System.setIn(inputStream);

        // Act
        ScriptResult result = processor.process("test.action", mockCallback);

        // Assert
        assertNotNull(result);
        verify(mockCallback, atLeastOnce()).lineResult(anyString());
    }

    @Test
    void testErrorHandling() throws AIProcessingException {
        // Test with failing action processor
        when(mockActionProcessor.processSingleAction(anyString()))
                .thenThrow(new AIProcessingException("Test error"));

        ScriptProcessor processor = new ScriptProcessor(mockActionProcessor);
        ScriptResult result = processor.process("test.action");

        // Should still return a result object even on error
        assertNotNull(result);
    }

    @Test 
    void testProcessingWithPreviousResults() throws AIProcessingException {
        // Arrange
        ScriptProcessor processor = new ScriptProcessor(mockActionProcessor);
        when(mockActionProcessor.query(anyString())).thenReturn("yes");
        when(mockActionProcessor.processSingleAction(anyString())).thenReturn("result1", "result2");

        // Create test content with multiple commands
        String content = "command1\ncommand2";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        System.setIn(inputStream);

        // Act
        ScriptResult result = processor.process("test.action");

        // Assert
        assertNotNull(result);
        assertTrue(result.getResults().size() >= 1, "Should process at least one command");
        verify(mockActionProcessor, atLeast(1)).processSingleAction(contains("previous action results"));
    }
}
