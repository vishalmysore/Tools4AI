package com.t4a.test;

import com.t4a.api.ToolsConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToolsConstantsTest {

    @Test
    public void testConstants() {
        assertEquals("BlankAction", ToolsConstants.BLANK_ACTION);
        assertEquals("SeleniumAction", ToolsConstants.SELENIUM_ACTION);
        assertEquals("AIProcessor", ToolsConstants.AI_PROCESSOR);
        assertEquals("Predict", ToolsConstants.PREDICT);
        assertEquals("No Group", ToolsConstants.GROUP_NAME);
        assertEquals("tasks which are not categorized", ToolsConstants.GROUP_DESCRIPTION);
    }
}