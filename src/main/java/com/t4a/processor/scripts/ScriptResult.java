package com.t4a.processor.scripts;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ScriptResult {
    private List<ScriptLineResult> results;
    public ScriptResult() {
        results = new ArrayList<>();
    }

    public void addResult(ScriptLineResult result) {
        results.add(result);
    }

    public void addResult(String line, String resultStr) {
        ScriptLineResult result = new ScriptLineResult(line,resultStr);
        results.add(result);
    }
}
