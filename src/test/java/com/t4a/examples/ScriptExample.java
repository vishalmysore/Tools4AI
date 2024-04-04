package com.t4a.examples;

import com.t4a.processor.scripts.ScriptProcessor;
import com.t4a.processor.scripts.ScriptResult;
import lombok.extern.java.Log;

@Log
public class ScriptExample {
    public static void main(String[] args) {
        ScriptProcessor script = new ScriptProcessor();
        ScriptResult result= script.process("test.action");
        log.info(script.summarize(result));
    }
}
