package com.t4a.test;

import com.t4a.action.BlankAction;
import com.t4a.api.ActionRisk;
import com.t4a.api.ActionType;
import com.t4a.processor.AIProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlankActionTest {

    @Test
    void testAskAdditionalQuestion() {
        // Create an instance of BlankAction
        BlankAction blankAction = new BlankAction();

        // Call askAdditionalQuestion method on the object
        String result = blankAction.askAdditionalQuestion("What's the weather?");

        // Assert statements to verify the return value of the method call
        assertEquals("provide answer for this query : What's the weather?", result);
    }

    @Test
    void testAskAdditionalQuestionWithNull() {
        BlankAction blankAction = new BlankAction();
        String result = blankAction.askAdditionalQuestion(null);
        assertNull(result);
    }

    @Test
    void testAskAdditionalQuestionWithEmptyString() {
        BlankAction blankAction = new BlankAction();
        String result = blankAction.askAdditionalQuestion("");
        assertEquals("", result);
    }

    @Test
    void testGetActionName() throws AIProcessingException {
        // Create an instance of BlankAction
        BlankAction blankAction = new BlankAction();

        // Call getActionName method on the object
        String result = blankAction.getActionName();

        // Assert statements to verify the return value of the method call
        assertEquals("askAdditionalQuestion", result);
    }

    @Test
    void testGetActionType() {
        // Create an instance of BlankAction
        BlankAction blankAction = new BlankAction();

        // Call getActionType method on the object
        ActionType result = blankAction.getActionType();

        // Assert statements to verify the return value of the method call
        assertEquals(ActionType.JAVAMETHOD, result);
    }

    @Test
    void testGetDescription() {
        // Create an instance of BlankAction
        BlankAction blankAction = new BlankAction();

        // Call getDescription method on the object
        String result = blankAction.getDescription();

        // Assert statements to verify the return value of the method call
        assertEquals("askAdditionalQuestion", result);
    }

    @Test
    void testGetActionRisk() {
        BlankAction blankAction = new BlankAction();
        assertEquals(ActionRisk.LOW, blankAction.getActionRisk());
    }

    @Test
    void testGetActionGroup() {
        BlankAction blankAction = new BlankAction();
        assertEquals("No group name available", blankAction.getActionGroup());
    }

    @Test
    void testGetGroupDescription() {
        BlankAction blankAction = new BlankAction();
        assertEquals("No group description available", blankAction.getGroupDescription());
    }

    @Test
    void testGetActionParameters() {
        BlankAction blankAction = new BlankAction();
        assertEquals("", blankAction.getActionParameters());
    }

    @Test
    void testGetJsonRPC() {
        BlankAction blankAction = new BlankAction();
        String jsonRpc = blankAction.getJsonRPC();
        assertTrue(jsonRpc.contains("\"actionName\":\"askAdditionalQuestion\""));
        assertTrue(jsonRpc.contains("\"description\":\"askAdditionalQuestion\""));
        assertTrue(jsonRpc.contains("\"actionType\":\"JAVAMETHOD\""));
        assertTrue(jsonRpc.contains("\"actionGroup\":\"No group name available\""));
        assertTrue(jsonRpc.contains("\"actionParameters\":\"\""));
    }
}