package com.t4a.examples.basic;

import com.t4a.api.AITools;
import lombok.extern.java.Log;

import java.io.IOException;
@Log
public class TestBridge {
    public TestBridge(){

    }
    public static void main(String[] args) throws IOException {

        log.info("Hello");
        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";

        TestBridge bridge = new TestBridge();

        String promptText = "can you book a dinner reseration for Vishal and 4 other people at Maharaj on 15th august and make sure its cancellable";
        String status = bridge.testJavaClass(projectId, location, modelName, promptText);
        log.info(promptText+ " : "+status);

        promptText = "can you book a dinner reseration for Vishal and his family of 4 at Maharaj on Indian Independence day and make sure its cancellable";
        status = bridge.testJavaClass(projectId, location, modelName, promptText);
        log.info(promptText+ " : "+status);
    }
    public String testJavaClass(String projectId, String location, String modelName,String promptText){

        AITools tools = new AITools(projectId,location,modelName);
        RestaurantPojo pojo = (RestaurantPojo)tools.actionClass(promptText,"com.enterprise.basic.RestaurantPojo","RestaurantClass","Create Pojo from the prompt");
        return pojo.toString();
    }
}
