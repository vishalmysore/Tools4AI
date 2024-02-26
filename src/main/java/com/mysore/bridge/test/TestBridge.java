package com.mysore.bridge.test;

import com.mysore.bridge.AITools;

import java.io.IOException;

public class TestBridge {
    public TestBridge(){

    }
    public static void main(String[] args) throws IOException {

        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";

        TestBridge bridge = new TestBridge();

        String promptText = "can you book a dinner reseration for Vishal and 4 other people at Maharaj on 15th august and make sure its cancellable";
        String status = bridge.testJavaClass(projectId, location, modelName, promptText);
        System.out.println(promptText+ " : "+status);

        promptText = "can you book a dinner reseration for Vishal and his family of 4 at Maharaj on Indian Independence day and make sure its cancellable";
        status = bridge.testJavaClass(projectId, location, modelName, promptText);
        System.out.println(promptText+ " : "+status);
    }
    public String testJavaClass(String projectId, String location, String modelName,String promptText){

        AITools tools = new AITools(projectId,location,modelName);
        RestaurantPojo pojo = (RestaurantPojo)tools.invokeClass(promptText,"com.mysore.bridge.test.RestaurantPojo","RestaurantClass","Create Pojo from the prompt");
        return pojo.toString();
    }
}
