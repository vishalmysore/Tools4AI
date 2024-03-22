package com.t4a.examples.basic;

import com.t4a.examples.actions.Customer;
import com.t4a.predict.PojoBuilder;
import com.t4a.processor.AIProcessingException;
import lombok.extern.java.Log;
@Log
public class BridgeTester {
    public BridgeTester(){

    }
    public static void main(String[] args) throws AIProcessingException {

        log.info("Hello");
        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";

        BridgeTester bridge = new BridgeTester();

      //  String promptText = "can you book a dinner reseration for Vishal and 4 other people at Maharaj on 15th august and make sure its cancellable";
     //  String status = bridge.testJavaClass(projectId, location, modelName, promptText);
     //   log.info(promptText+ " : "+status);

        String promptText  = "can you book a dinner reseration in name of Vishal and his family of 4 at Maharaj restaurant on Indian Independence day and make sure its cancellable";
        String status = bridge.testJavaClass(projectId, location, modelName, promptText);
        log.info(promptText+ " : "+status);
    }
    public String testJavaClass(String projectId, String location, String modelName,String promptText) throws AIProcessingException {

        PojoBuilder tools = new PojoBuilder();
        Object pojo = tools.buildPojo(promptText,"com.t4a.examples.basic.RestaurantPojo","RestaurantClass","Create Pojo from the prompt");
        System.out.println(pojo);
        pojo = tools.buildPojo("I went to the part yesterday and met someone it was so good to meet an old friend. A customer is complaining that his computer is not working, his name is Vinod Gupta,  and he stays in Toronto", Customer.class.getName(),"Customer", "get Customer details");
        System.out.println(pojo);
        return pojo.toString();
    }
}
