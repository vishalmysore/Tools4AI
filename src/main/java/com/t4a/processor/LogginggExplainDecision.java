package com.t4a.processor;

import com.t4a.detect.ExplainDecision;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogginggExplainDecision implements ExplainDecision {
    @Override
    public String explain(String promptText, String methodName, String reason) {
       log.debug("promptText {} , reason {} ",promptText, reason);
       return reason;
    }
}
