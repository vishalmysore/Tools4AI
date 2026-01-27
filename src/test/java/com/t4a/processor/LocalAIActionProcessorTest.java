package com.t4a.processor;

import com.t4a.api.AIAction;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LocalAIActionProcessorTest {

    @Test
    void testProcessMethodsReturnNull() throws AIProcessingException {
        LocalAIActionProcessor processor = new LocalAIActionProcessor();
        Assertions.assertNull(processor.processSingleAction("prompt"));
        Assertions.assertNull(processor.query("prompt"));
        Assertions.assertNull(processor.processSingleAction("prompt", (HumanInLoop) null, (ExplainDecision) null));
        Assertions.assertNull(
                processor.processSingleAction("prompt", (AIAction) null, (HumanInLoop) null, (ExplainDecision) null));
        Assertions.assertNull(processor.processSingleAction("prompt", (com.t4a.detect.ActionCallback) null));
        Assertions.assertNull(processor.processSingleAction("prompt", (AIAction) null, (HumanInLoop) null,
                (ExplainDecision) null, null));
    }
}
