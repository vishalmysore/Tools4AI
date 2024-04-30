package com.t4a.test;

import com.t4a.processor.chain.SubPrompt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubPromptTest {
    @Test
     void testCanBeExecutedParallely() {
        SubPrompt subPrompt = new SubPrompt();
        subPrompt.setDepend_on(null);
        assertTrue(subPrompt.canBeExecutedParallely());

        subPrompt.setDepend_on("");
        assertTrue(subPrompt.canBeExecutedParallely());

        subPrompt.setDepend_on("Depend 1");
        assertFalse(subPrompt.canBeExecutedParallely());
    }

    @Test
     void testToString() {
        SubPrompt subPrompt = new SubPrompt();
        subPrompt.setId("1");
        subPrompt.setSubprompt("SubPrompt 1");
        subPrompt.setDepend_on("Depend 1");

        String expected = "SubPrompt{id='1', subprompt='SubPrompt 1', depend_on='Depend 1'}";
        assertEquals(expected, subPrompt.toString());
    }

    @Test
     void testEqualsAndHashCode() {
        SubPrompt subPrompt1 = new SubPrompt();
        subPrompt1.setId("1");

        SubPrompt subPrompt2 = new SubPrompt();
        subPrompt2.setId("1");

        assertEquals(subPrompt1, subPrompt2);
        assertEquals(subPrompt1.hashCode(), subPrompt2.hashCode());

        subPrompt2.setId("2");
        assertNotEquals(subPrompt1, subPrompt2);
        assertNotEquals(subPrompt1.hashCode(), subPrompt2.hashCode());
    }
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