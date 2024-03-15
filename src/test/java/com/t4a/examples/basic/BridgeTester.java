package com.t4a.examples.basic;

import lombok.extern.java.Log;

import java.io.IOException;
@Log
public class BridgeTester {
    public BridgeTester(){

    }
    public static void main(String[] args) throws IOException {

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
    public String testJavaClass(String projectId, String location, String modelName,String promptText){

        AITools tools = new AITools(projectId,location,modelName);
        Object pojo = tools.actionClass(promptText,"com.t4a.examples.basic.RestaurantPojo","RestaurantClass","Create Pojo from the prompt");
        System.out.println(pojo);
        return pojo.toString();
    }
}
