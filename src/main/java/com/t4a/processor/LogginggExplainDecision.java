package com.t4a.processor;

import lombok.extern.java.Log;

@Log
public class LogginggExplainDecision implements ExplainDecision{
    @Override
    public void explain(String promptText, String methodName, String reason) {
       log.info("promptText "+promptText +" reason "+reason);
    }
}
