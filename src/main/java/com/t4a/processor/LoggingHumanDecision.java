package com.t4a.processor;

import lombok.extern.java.Log;

import java.util.Map;

/**
 * Simple implementation of Human IN Loop, all it does is logging but in real world this could trigger a full
 * human validation and return true or false
 */
@Log
public class LoggingHumanDecision implements HumanInLoop{
    @Override
    public FeedbackLoop allow(String promptText, String methodName, Map<String, Object> params) {
        log.info(" Do you allow "+methodName+" for "+promptText);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            log.info("Key: " + key + ", Value: " + value);
        }
        return new FeedbackLoop() {

            @Override
            public boolean isAIResponseValid() {
                return true;
            }
        };
    }
}
