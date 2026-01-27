package com.t4a.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApiTest {

    @Test
    void testToolsConstants() {
        Assertions.assertNotNull(new ToolsConstants());
    }

    @Test
    void testPredictedAIAction() {
        PredictedAIAction action = new PredictedAIAction() {
            @Override
            public String getActionName() {
                return "test";
            }

            @Override
            public ActionType getActionType() {
                return ActionType.JAVAMETHOD;
            }

            @Override
            public String getDescription() {
                return "test";
            }
        };
        Assertions.assertEquals("", action.getActionParameters());
    }

    @Test
    void testAIAction() {
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
                return "test description";
            }
        };
        String json = action.getJsonRPC();
        Assertions.assertTrue(json.contains("\"actionName\":\"testAction\""));
        Assertions.assertEquals(ActionRisk.LOW, action.getActionRisk());
        Assertions.assertEquals("default", action.getGroupDescription());
        Assertions.assertEquals("default", action.getActionGroup());
    }
}
