package com.t4a.processor;

import lombok.extern.java.Log;

import java.util.Map;

@Log
public class HumanInLoop {
    public boolean allow(String promptText, String methodName, Map<String,Object> params) {

        return true;
    }
}
