package com.t4a.test;

import com.t4a.api.AIAction;
import com.t4a.api.ActionRisk;
import com.t4a.api.ActionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AIActionTest {
    @Test
    void testDefaultMethods() {
        AIAction action = new AIAction() {
            @Override
            public String getActionName() {
                return "testAction";
            }

            @Override
            public ActionType getActionType() {
                return ActionType.JAVAMETHOD;
            }

            @Override
            public String getDescription() {
                return "Test description";
            }
        };

        assertEquals(ActionRisk.LOW, action.getActionRisk());
        assertEquals("default", action.getActionGroup());
        assertEquals("default", action.getGroupDescription());
    }
}
