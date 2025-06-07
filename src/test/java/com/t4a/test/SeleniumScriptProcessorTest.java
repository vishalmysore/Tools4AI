package com.t4a.test;

import com.google.gson.Gson;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ActionState;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.scripts.ScriptResult;
import com.t4a.processor.scripts.SeleniumCallback;
import com.t4a.processor.scripts.SeleniumScriptProcessor;
import com.t4a.processor.selenium.SeleniumGeminiProcessor;
import com.t4a.processor.selenium.SeleniumProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SeleniumScriptProcessorTest {

    @Mock
    private SeleniumProcessor mockSeleniumProcessor;

    @Mock
    private WebDriver mockWebDriver;

    @Mock
    private SeleniumCallback mockSeleniumCallback;

    @Mock
    private ActionCallback mockActionCallback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockSeleniumProcessor.getDriver()).thenReturn(mockWebDriver);
    }

    @Test
    void testConstructors() {
        // Test default constructor
        SeleniumScriptProcessor processor = new SeleniumScriptProcessor();
        assertNotNull(processor);
        assertNotNull(processor.getGson());
        assertNotNull(processor.getSeleniumProcessor());
        assertTrue(processor.getSeleniumProcessor() instanceof SeleniumGeminiProcessor);

        // Test constructor with Gson
        Gson gson = new Gson();
        processor = new SeleniumScriptProcessor(gson);
        assertEquals(gson, processor.getGson());

        // Test constructor with SeleniumProcessor
        processor = new SeleniumScriptProcessor(mockSeleniumProcessor);
        assertEquals(mockSeleniumProcessor, processor.getSeleniumProcessor());

        // Test constructor with both
        processor = new SeleniumScriptProcessor(gson, mockSeleniumProcessor);
        assertEquals(gson, processor.getGson());
        assertEquals(mockSeleniumProcessor, processor.getSeleniumProcessor());
    }

    @Test
    void testBasicProcessing() throws AIProcessingException {
        // Arrange
        SeleniumScriptProcessor processor = new SeleniumScriptProcessor(mockSeleniumProcessor);

        // Act
        ScriptResult result = processor.process("test.action");

        // Assert
        assertNotNull(result);
        verify(mockSeleniumProcessor, atLeastOnce()).getDriver();
    }

    @Test
    void testProcessingWithCallback() throws AIProcessingException {
        // Arrange
        SeleniumScriptProcessor processor = new SeleniumScriptProcessor(mockSeleniumProcessor);
        when(mockSeleniumCallback.beforeWebAction(anyString(), any(WebDriver.class))).thenReturn(true);

        // Create test content
        String content = "click button\ntype text";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        System.setIn(inputStream);

        // Act
        ScriptResult result = processor.process("test.action", mockSeleniumCallback);

        // Assert
        assertNotNull(result);
        verify(mockSeleniumCallback, atLeastOnce()).beforeWebAction(anyString(), any(WebDriver.class));
        verify(mockSeleniumCallback, atLeastOnce()).afterWebAction(anyString(), any(WebDriver.class));
    }

    @Test
    void testProcessingWithActionCallback() throws AIProcessingException {
        // Arrange
        SeleniumScriptProcessor processor = new SeleniumScriptProcessor(mockSeleniumProcessor);

        // Create test content
        String content = "click button\ntype text";

        // Act
        ScriptResult result = processor.process(content, mockActionCallback);

        // Assert
        assertNotNull(result);
        verify(mockActionCallback, atLeastOnce()).sendtStatus(anyString(), eq(ActionState.WORKING));
    }    @Test
    void testErrorHandling() throws AIProcessingException {
        // Arrange
        SeleniumScriptProcessor processor = new SeleniumScriptProcessor(mockSeleniumProcessor);
        
        // Mock beforeWebAction to allow processing
        when(mockSeleniumCallback.beforeWebAction(anyString(), any(WebDriver.class)))
                .thenReturn(true);
                
        // Mock first call to fail, retry to succeed
        doThrow(new AIProcessingException("Test error"))
                .doNothing()
                .when(mockSeleniumProcessor).processWebAction(anyString());

        // Set up error handler to return retry command
        when(mockSeleniumCallback.handleError(eq("click button"), eq("Test error"), any(WebDriver.class), eq(0)))
                .thenReturn("retry_command");

        // Create test content
        String content = "click button";
        StringBuffer contentBuffer = new StringBuffer(content);

        // Act 
        ScriptResult result = processor.process(contentBuffer, mockSeleniumCallback);

        // Assert
        // Verify error handler was called with correct parameters
        verify(mockSeleniumCallback).handleError(eq("click button"), eq("Test error"), any(WebDriver.class), eq(0));
        // Verify the retry was attempted
        verify(mockSeleniumProcessor, times(2)).processWebAction(anyString());
        // Verify afterWebAction was called after successful retry
        verify(mockSeleniumCallback).afterWebAction(anyString(), any(WebDriver.class));
        
        assertNotNull(result);
    }

    @Test
    void testWebActionProcessingWithRetry() throws AIProcessingException {
        // Arrange
        SeleniumScriptProcessor processor = new SeleniumScriptProcessor(mockSeleniumProcessor);
        
        // Mock first attempt fails, second succeeds
        doThrow(new AIProcessingException("First try failed"))
                .doNothing()
                .when(mockSeleniumProcessor).processWebAction(anyString());

        when(mockSeleniumCallback.handleError(anyString(), anyString(), any(WebDriver.class), anyInt()))
                .thenReturn("retry command");

        // Act
        processor.processWebAction("click button", mockSeleniumCallback, 0);

        // Assert
        // Should have attempted the action twice (original + retry)
        verify(mockSeleniumProcessor, times(2)).processWebAction(anyString());
        verify(mockSeleniumCallback, times(1)).handleError(anyString(), anyString(), any(WebDriver.class), anyInt());
    }

    @Test
    void testContentBufferProcessing() throws AIProcessingException {
        // Arrange
        SeleniumScriptProcessor processor = new SeleniumScriptProcessor(mockSeleniumProcessor);
        StringBuffer content = new StringBuffer("click button\ntype text");

        // Act
        ScriptResult result = processor.process(content, mockSeleniumCallback);

        // Assert
        assertNotNull(result);
        verify(mockSeleniumCallback, atLeastOnce()).beforeWebAction(anyString(), any(WebDriver.class));
    }
}
