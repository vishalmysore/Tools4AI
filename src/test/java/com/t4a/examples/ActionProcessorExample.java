package com.t4a.examples;

import com.t4a.examples.actions.PlayerWithRestaurant;
import com.t4a.processor.AIProcessingException;
import com.t4a.deperecated.GeminiActionProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionProcessorExample {
    public static void main(String[] args) throws AIProcessingException {
       GeminiActionProcessor processor = new GeminiActionProcessor();
       String result = (String)processor.processSingleAction(" Customer name is Vishal Mysore, his computer needs repair and he is in Toronto");
       log.debug(result);
        result = (String)processor.processSingleAction("My employee name is Vishal he is toronto save this");
        log.debug(result);
        result = (String)processor.processSingleAction("Post a book with title Harry Poster and Problem rat, id of the book is 887 and discription is about harry ");
        log.debug(result);

        String prm = "sachin tendular is a cricket player and he has played 400 matches, his max score is 1000, he wants to go to " +
                "Maharaja restaurant in toronto with 4 of his friends on Indian Independence Day, can you notify him and the restarurant";
        result = (String)processor.processSingleAction(prm,new PlayerWithRestaurant(),"notifyPlayerAndRestaurant");
        log.debug(result);
     /*   log.debug("==== Multi Command =====");
        List<Object> results = processor.processMultipleAction("hey I am in Toronto do you think i can go out without jacket, also save the weather information , City location and your suggestion in file, also include places to see",2);
        for (Object resultObj:results
             ) {
            log.debug((String)resultObj);

        }

        /*
        String p1 = "Hey I am in Toronto do you think i can go out without jacket. My friends name is Vinod he lives in Balaghat, please save this information about my friend";
        String p2 = "hey I am in Toronto do you think i can go out without jacket, also save the weather information , City location and your suggestion in file, also include places to see";

        log.debug("\n\n==== Multi Command 2 =====\n\n");
        List<Object> results1 = processor.processMultipleAction(p2,2);
        for (Object resultObj:results1
        ) {
            log.debug((String)resultObj);

        }
        p2 = "hey I am in Toronto do you think i can go out without jacket, also save the weather information , City location and your suggestion in file, also include places to see. and then send this information to flight booking system";
        processor.processMultipleActionDynamically(p2,null,null);
        String noFailPrmt = "hey I am in Toronto do you think i can go out without jacket, also save the weather information , City location and your suggestion in file, also include places to see";
        String processed = processor.processMultipleActionDynamically(noFailPrmt,null,null);
        log.debug(processed);

         */
    }


}
