package com.t4a.test;

import com.t4a.predict.PredictionLoader;
import com.t4a.predict.Tools4AI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class Tools4AITest {
    @Test
    void testGetActionListAsJSONRPC() {
        // PredictionLoader is a singleton, let's see what's in there
        String json = Tools4AI.getActionListAsJSONRPC();
        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.startsWith("["));
    }

    @Test
    void testExecuteActionNotFound() {
        try {
            Object result = Tools4AI.executeAction("NonExistentAction", "{}");
            Assertions.assertEquals("Action not found", result);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testExecuteActionSuccess() throws Exception {
        MockActionWithAnnotation mockAction = new MockActionWithAnnotation();
        GenericMockAction genericAction = new GenericMockAction(mockAction);
        PredictionLoader loader = PredictionLoader.getInstance();
        Map<String, com.t4a.api.AIAction> predictions = loader.getPredictions();

        String actionName = "mockAction";
        predictions.put(actionName, genericAction);

        String jsonStr = "{\n" +
                "  \"methodName\": \"mockAction\",\n" +
                "  \"returnType\": \"String\",\n" +
                "  \"parameters\": [\n" +
                "    {\n" +
                "      \"name\": \"mockName\",\n" +
                "      \"type\": \"String\",\n" +
                "      \"fieldValue\": \"testName\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"mockPerson\",\n" +
                "      \"type\": \"com.t4a.test.Person\",\n" +
                "      \"fields\": [\n" +
                "        {\n" +
                "          \"fieldName\": \"name\",\n" +
                "          \"fieldType\": \"String\",\n" +
                "          \"fieldValue\": \"personName\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"fieldName\": \"age\",\n" +
                "          \"fieldType\": \"int\",\n" +
                "          \"fieldValue\": 30\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Object result = Tools4AI.executeAction(actionName, jsonStr);
        Assertions.assertEquals("testName", result);
        Assertions.assertEquals("personName", mockAction.p.getName());
    }
}
