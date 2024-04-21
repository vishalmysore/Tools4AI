package com.t4a.transform;

import com.google.gson.Gson;
import com.t4a.JsonUtils;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenAIPromptTransformer implements PromptTransformer {
    private Gson gson ;
    private ChatLanguageModel openAiChatModel;
    public OpenAIPromptTransformer(Gson gson) {
        this.gson = gson;
        openAiChatModel = PredictionLoader.getInstance().getOpenAiChatModel();
    }
    public OpenAIPromptTransformer() {
        this.gson = new Gson();
        openAiChatModel = PredictionLoader.getInstance().getOpenAiChatModel();
    }






    @Override
    public Object transformIntoPojo(String prompt, String className, String funName, String description) throws AIProcessingException {
        try {
            JsonUtils util = new JsonUtils();
            Class clazz = Class.forName(className);
            String jsonStr = null;
            if(clazz.getName().equalsIgnoreCase("java.util.Map")) {
                jsonStr = util.buildBlankMapJsonObject(null).toString(4); ;

            } else if(clazz.getName().equalsIgnoreCase("java.util.List")) {
                jsonStr = util.buildBlankListJsonObject(null).toString(4); ;

            }else {
                jsonStr = util.convertClassToJSONString(clazz);
            }
            log.info(jsonStr);
            jsonStr = PredictionLoader.getInstance().getOpenAiChatModel().generate(" Here is your prompt {" + prompt + "} - here is the json - " + jsonStr + " - populate the fieldValue and return the json");
            log.info(jsonStr);
            return util.populateClassFromJson(jsonStr);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Override
    public String transformIntoJson(String json, String prompt, String funName, String description) throws AIProcessingException {
        return openAiChatModel.generate(" here is you prompt { "+prompt+"} and here is your json "+json+" - fill the json with values and return");

    }

    public String transformIntoJson(String jsonString, String promptText) throws AIProcessingException{
        return transformIntoJson(jsonString,promptText,"get me values", "Get me the values in json");
    }
}
