package com.t4a.examples;

import com.t4a.processor.scripts.ScriptResult;
import com.t4a.processor.scripts.SeleniumScriptProcessor;
import lombok.extern.java.Log;

@Log
public class ScriptExample {
    public static void main(String[] args) {
        //ScriptProcessor script = new ScriptProcessor();
       // ScriptResult result= script.process("test.action");
       // log.info(script.summarize(result));

       // script = new ScriptProcessor(new OpenAiActionProcessor());
       // ScriptResult result= script.process("test.action");
        //log.info(script.summarize(result));
        SeleniumScriptProcessor processor = new SeleniumScriptProcessor();
        ScriptResult result= processor.process("web.action");

    }
}
