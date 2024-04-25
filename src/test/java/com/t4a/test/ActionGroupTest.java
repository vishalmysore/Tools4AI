package com.t4a.test;

import com.t4a.api.AIAction;
import com.t4a.api.ActionGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class ActionGroupTest {

    private ActionGroup actionGroup;

    @BeforeEach
    public void setUp() {
        actionGroup = new ActionGroup("TestGroup", "Test Description");
    }

    @Test
     void testAddAction() {
        AIAction action = new MockAction(); // Assuming AIAction is a valid class
        actionGroup.addAction(action);

        assertEquals(1, actionGroup.getActions().size());
        assertEquals(action.getActionName(), actionGroup.getActions().get(0).getActionName()); // Assuming ActionKey has a getAction method
    }

    @Test
     void testEquals() {
        ActionGroup anotherActionGroup = new ActionGroup("TestGroup", "Test Description");

        assertTrue(actionGroup.equals(anotherActionGroup));
    }
}