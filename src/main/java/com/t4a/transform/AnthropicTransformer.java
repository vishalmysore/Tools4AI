package com.t4a.transform;

import com.google.gson.Gson;
import com.t4a.JsonUtils;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnthropicTransformer implements PromptTransformer {
    private Gson gson;
    private ChatLanguageModel anthropicChatModel;

    public AnthropicTransformer(Gson gson) {
        this.gson = gson;
        anthropicChatModel = PredictionLoader.getInstance().getAnthropicChatModel();
    }

    public AnthropicTransformer() {
        this.gson = new Gson();
        anthropicChatModel = PredictionLoader.getInstance().getAnthropicChatModel();
    }


    @Override
    public Object transformIntoPojo(String prompt, String className, String funName, String description) throws AIProcessingException {
        try {
            JsonUtils util = new JsonUtils();
            Class clazz = Class.forName(className);
            String jsonStr = null;
            if (clazz.getName().equalsIgnoreCase("java.util.Map")) {
                jsonStr = util.buildBlankMapJsonObject(null).toString(4);
                ;

            } else if (clazz.getName().equalsIgnoreCase("java.util.List")) {
                jsonStr = util.buildBlankListJsonObject(null).toString(4);
                ;

            } else {
                jsonStr = util.convertClassToJSONString(clazz);
            }
            log.info(jsonStr);
            jsonStr = PredictionLoader.getInstance().getAnthropicChatModel().generate(" Here is your prompt {" + prompt + "} - here is the json - " + jsonStr + " - populate the fieldValue and return the json");
            log.info(jsonStr);
            jsonStr = jsonStr.trim();
            if (!jsonStr.trim().startsWith("{")) {
                jsonStr = util.extractJson(jsonStr.trim());
            }
            return util.populateClassFromJson(jsonStr);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Override
    public String transformIntoJson(String json, String prompt, String funName, String description) throws AIProcessingException {
        return anthropicChatModel.generate(" here is you prompt { " + prompt + "} and here is your json " + json + " - fill the json with values and return");

    }
}