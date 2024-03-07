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
        String result = (String)processor.processSingleAction("Hey I am in Toronto do you think i can go out without jacket");
        log.info(result);

        result = (String)processor.processSingleAction("Hey I am in Toronto my name is Vishal Can you save this info");
        log.info(result);

        log.info("==== Multi Command =====");
        List<Object> results = processor.processMultipleAction("Hey I am in Delhi do you think i can go out without jacket, also save the temperature and City location in file",2);
        for (Object resultObj:results
             ) {
            log.info((String)resultObj);

        }

        //Hey I am in Toronto do you think i can go out without jacket, my friends name is Vinod he lives in Balaghat, please save this information locally
        //hey I am in Toronto do you think i can go out without jacket, also save the weather information , City location and your suggestion in file, also include places to see

        log.info("\n\n==== Multi Command 2 =====\n\n");
        List<Object> results1 = processor.processMultipleAction("Hey I am in Toronto do you think i can go out without jacket, my friends name is Vinod he lives in Balaghat, please save this information locally",2);
        for (Object resultObj:results1
        ) {
            log.info((String)resultObj);

        }
    }
}