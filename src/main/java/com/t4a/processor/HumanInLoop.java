package com.t4a.processor;

import java.util.Map;


public interface HumanInLoop {
    public FeedbackLoop allow(String promptText, String methodName, Map<String,Object> params) ;
}
