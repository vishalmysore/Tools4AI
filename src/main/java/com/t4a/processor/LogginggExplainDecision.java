package com.t4a.processor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogginggExplainDecision implements ExplainDecision{
    @Override
    public void explain(String promptText, String methodName, String reason) {
       log.debug("promptText "+promptText +" reason "+reason);
    }
}
