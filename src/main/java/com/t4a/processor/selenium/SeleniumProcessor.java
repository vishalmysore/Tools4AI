package com.t4a.processor.selenium;

import com.t4a.processor.AIProcessingException;

public interface SeleniumProcessor {
    public void processWebAction(String prompt) throws AIProcessingException ;
    public boolean trueFalseQuery(String question) throws AIProcessingException ;
}
