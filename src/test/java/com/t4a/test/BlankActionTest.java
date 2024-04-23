package com.t4a.test;

import com.t4a.action.BlankAction;
import com.t4a.api.ActionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testGetActionName() {
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
        assertEquals("ask remaining question", result);
    }
}