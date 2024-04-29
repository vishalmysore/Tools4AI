package com.t4a.test;

import com.t4a.processor.LogginggExplainDecision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogginggExplainDecisionTest {

    @Test
     void testExplain() {

        LogginggExplainDecision decision = new LogginggExplainDecision();

        String promptText = "promptText";
        String methodName = "methodName";
        String reason = "reason";

        Assertions.assertEquals(reason,decision.explain(promptText, methodName, reason));


    }
}
