package com.t4a.processor.scripts;

public interface BaseScriptProcessor {

    public ScriptResult process(String fileName);

    public ScriptResult process(String fileName, ScriptCallback callback);

    default void processWebAction(String line,SeleniumCallback callback){};
}
