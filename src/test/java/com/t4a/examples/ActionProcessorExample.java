package com.t4a.examples;

import com.t4a.processor.ActionProcessor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Log
public class ActionProcessorExample {
    public static void main(String[] args) throws IOException, InvocationTargetException, IllegalAccessException {
        ActionProcessor processor = new ActionProcessor();
      //  String result = (String)processor.processSingleAction("cookgptserver","us-central1","gemini-1.0-pro","ey I am in Toronto do you think i can go out without jacket");
      //  log.info(result);
        List<Object> results = processor.processMultipleAction("cookgptserver","us-central1","gemini-1.0-pro","ey I am in Toronto do you think i can go out without jacket, my friends name is Vinod he lives in Balaghat, please save this information locally ",2);
        for (Object resultObj:results
             ) {
            log.info((String)resultObj);

        }
    }
}
