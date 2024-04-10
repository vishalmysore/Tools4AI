package com.t4a.examples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.t4a.JsonUtils;
import com.t4a.examples.actions.Customer;
import com.t4a.examples.actions.ListAction;
import com.t4a.examples.basic.DateDeserializer;
import com.t4a.examples.pojo.Organization;
import com.t4a.predict.GeminiPromptTransformer;
import com.t4a.predict.OpenAIPromptTransformer;
import com.t4a.predict.PredictionLoader;
import com.t4a.predict.PromptTransformer;
import com.t4a.processor.AIProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
public class PromptTransformerExample {
    public PromptTransformerExample(){

    }

    public static void main(String[] args) throws AIProcessingException{
        OpenAIPromptTransformer tra = new OpenAIPromptTransformer();
        String promptText = "Shahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay.Hrithik roshan is another employee of same company based in Chennai his taks are jumping and Gym he joined on Indian Independce Day";
        JsonUtils utils = new JsonUtils();
        String jsonStr = utils.convertClassToJSONString(Organization.class);
        System.out.println(jsonStr);
        System.out.println("=============================");
        jsonStr = PredictionLoader.getInstance().getOpenAiChatModel().generate(" Here is your prompt {" + promptText + "} - here is the json - " + jsonStr + " - populate the fieldValue and return the json");
        System.out.println(jsonStr);
        System.out.println("=============================");
        Method[] met = ListAction.class.getMethods();
        for (Method m:met
        ) {
            if(m.getName().equals("addOrganization")){
                 jsonStr = utils.convertMethodTOJsonString(m);
                System.out.println(jsonStr);
                System.out.println("=============================");
                jsonStr = PredictionLoader.getInstance().getOpenAiChatModel().generate(" Here is your prompt {" + promptText + "} - here is the json - " + jsonStr + " - populate the fieldValue and return the json");
                System.out.println(jsonStr);
            }
        }


    }
    public static void main2(String[] args) throws AIProcessingException {
        OpenAIPromptTransformer tra = new OpenAIPromptTransformer();
       // System.out.println(tra.transformIntoPojo(" Customer name is Vishal and he is Toronto , his complaint is computer not working date is labor day", Customer.class.getName(),"",""));
        String promptText  = "can you book a dinner reseration in name of Vishal and his family of 4 at Maharaj restaurant on Indian Independence day and make sure its cancellable";
       // System.out.println(tra.transformIntoPojo(promptText, RestaurantPojo.class.getName(),"",""));
        promptText = "Shahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay";
        System.out.println(tra.transformIntoPojo(promptText, Organization.class.getName(),"",""));
    }

    public static void main1(String[] args) throws AIProcessingException {

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