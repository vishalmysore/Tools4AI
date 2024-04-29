package com.t4a.test;

import com.t4a.processor.chain.SubPrompt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubPromptTest {

    @Test
    public void testSubPrompt() {
        // Arrange
        SubPrompt subPrompt = new SubPrompt();
        subPrompt.setId("1");
        subPrompt.setSubprompt("SubPrompt 1");
        subPrompt.setDepend_on("Depend 1");
        subPrompt.setProcessed(true);
        subPrompt.setActionName("Action 1");
        subPrompt.setResult("Result 1");

        // Act & Assert
        assertEquals("1", subPrompt.getId());
        assertEquals("SubPrompt 1", subPrompt.getSubprompt());
        assertEquals("Depend 1", subPrompt.getDepend_on());
        assertTrue(subPrompt.isProcessed());
        assertEquals("Action 1", subPrompt.getActionName());
        assertEquals("Result 1", subPrompt.getResult());
    }
}