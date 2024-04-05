package com.t4a.examples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.t4a.examples.actions.Customer;
import com.t4a.examples.basic.DateDeserializer;
import com.t4a.predict.GeminiPromptTransformer;
import com.t4a.predict.OpenAIPromptTransformer;
import com.t4a.predict.PromptTransformer;
import com.t4a.processor.AIProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class PromptTransformerExample {
    public PromptTransformerExample(){

    }
    public static void main(String[] args) throws AIProcessingException {

        log.debug("Hello");
        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";

        PromptTransformerExample bridge = new PromptTransformerExample();

        //  String promptText = "can you book a dinner reseration for Vishal and 4 other people at Maharaj on 15th august and make sure its cancellable";
        //  String status = bridge.testJavaClass(projectId, location, modelName, promptText);
        //   log.debug(promptText+ " : "+status);

        String promptText  = "can you book a dinner reseration in name of Vishal and his family of 4 at Maharaj restaurant on Indian Independence day and make sure its cancellable";
        String status = bridge.testJavaClass(projectId, location, modelName, promptText);
        log.debug(promptText+ " : "+status);
    }
    public String testJavaClass(String projectId, String location, String modelName,String promptText) throws AIProcessingException {

        PromptTransformer tools = new GeminiPromptTransformer();
        Object pojo = tools.transformIntoPojo(promptText,"com.t4a.examples.basic.RestaurantPojo","RestaurantClass","Create Pojo from the prompt");
        System.out.println("Gemini "+pojo);

        tools = new OpenAIPromptTransformer();
        pojo = tools.transformIntoPojo(promptText,"com.t4a.examples.basic.RestaurantPojo","RestaurantClass","Create Pojo from the prompt");
        System.out.println("Openai "+pojo);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer("dd MMMM yyyy"));
        Gson gson = gsonBuilder.create();

        String jsonString = "{\"lastName\":\"String\",\"firstName\":\"String\",\"reasonForCalling\":\"String\",\"dateJoined\":\"date\",\"location\":\"String\"}";

        PromptTransformer tools2 = new GeminiPromptTransformer(gson);
        pojo = tools2.transformIntoPojo("I went to the part yesterday and met someone it was so good to meet an old friend. A customer is complaining that his computer is not working, his name is Vinod Gupta,  and he stays in Toronto he joined on 12 May 2008", Customer.class.getName(),"Customer", "get Customer details");
        System.out.println("Gemini "+pojo);
        jsonString = tools2.transformIntoJson(jsonString,"I went to the part yesterday and met someone it was so good to meet an old friend. A customer is complaining that his computer is not working, his name is Vinod Gupta,  and he stays in Toronto he joined on 12 May 2008", "Customer", "get Customer details");
        System.out.println("Gemini "+jsonString);

        tools2 = new OpenAIPromptTransformer(gson);
        pojo = tools2.transformIntoPojo("I went to the part yesterday and met someone it was so good to meet an old friend. A customer is complaining that his computer is not working, his name is Vinod Gupta,  and he stays in Toronto he joined on 12 May 2008", Customer.class.getName(),"Customer", "get Customer details");
        System.out.println("Openai "+pojo);
        jsonString = tools2.transformIntoJson(jsonString,"I went to the part yesterday and met someone it was so good to meet an old friend. A customer is complaining that his computer is not working, his name is Vinod Gupta,  and he stays in Toronto he joined on 12 May 2008", "Customer", "get Customer details");
        System.out.println("Openai "+jsonString);
        return pojo.toString();
    }
}
class Data {
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}