package com.t4a.processor;

import com.t4a.detect.FeedbackLoop;
import com.t4a.detect.HumanInLoop;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Simple implementation of Human IN Loop, all it does is logging but in real world this could trigger a full
 * human validation and return true or false
 */
@Slf4j
public class LoggingHumanDecision implements HumanInLoop {
    @Override
    public FeedbackLoop allow(String promptText, String methodName, Map<String, Object> params) {

        log.debug(" Do you allow "+methodName+" for "+promptText);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            log.debug("Key: " + key + ", Value: " + value);
        }
        return () -> true;
    }
    @Override
    public FeedbackLoop allow(String promptText, String methodName, String params) {

        log.debug(" Do you allow "+methodName+" for "+promptText);

        return () -> true;
    }
}
