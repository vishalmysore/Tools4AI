package com.t4a.test;

import com.t4a.processor.LoggingHumanDecision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class LoggingHumanDecisionTest {

    @Test
     void testAllowWithMap() {

        LoggingHumanDecision decision = new LoggingHumanDecision();


        String promptText = "promptText";
        String methodName = "methodName";
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");

        Assertions.assertTrue(decision.allow(promptText, methodName, params).isAIResponseValid());


    }

    @Test
     void testAllowWithString() {

        LoggingHumanDecision decision = new LoggingHumanDecision();


        String promptText = "promptText";
        String methodName = "methodName";
        String params = "params";

        Assertions.assertTrue(decision.allow(promptText, methodName, params).isAIResponseValid());


    }
}
