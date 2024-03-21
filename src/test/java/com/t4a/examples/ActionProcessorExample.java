package com.t4a.examples;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.ActionProcessor;
import lombok.extern.java.Log;

@Log
public class ActionProcessorExample {
    public static void main(String[] args) throws AIProcessingException {
        ActionProcessor processor = new ActionProcessor();
       //String result = (String)processor.processSingleAction("search google for best paces to see in toronto");
       // log.info(result);

/*        result = (String)processor.processSingleAction("Hey I am in Toronto my name is Vishal Can you save this info");
        log.info(result);*/

      /*  log.info("==== Multi Command =====");
        List<Object> results = processor.processMultipleAction("hey I am in Toronto do you think i can go out without jacket, also save the weather information , City location and your suggestion in file, also include places to see",2);
        for (Object resultObj:results
             ) {
            log.info((String)resultObj);

        }

        String p1 = "Hey I am in Toronto do you think i can go out without jacket. My friends name is Vinod he lives in Balaghat, please save this information about my friend";
        String p2 = "hey I am in Toronto do you think i can go out without jacket, also save the weather information , City location and your suggestion in file, also include places to see";
*/
       /* log.info("\n\n==== Multi Command 2 =====\n\n");
        List<Object> results1 = processor.processMultipleAction(p2,2);
        for (Object resultObj:results1
        ) {
            log.info((String)resultObj);

        }*/
         String p2 = "hey I am in Toronto do you think i can go out without jacket, also save the weather information , City location and your suggestion in file, also include places to see. and then send this information to flight booking system";
        //processor.processMultipleActionWithoutFail(p2,null,null);
        String noFailPrmt = "hey I am in Toronto do you think i can go out without jacket, also save the weather information , City location and your suggestion in file, also include places to see";
        String processed = processor.processMultipleActionDynamically(noFailPrmt,null,null);
        log.info(processed);
    }


}
