package com.t4a.transform;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;
import com.t4a.JsonUtils;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * This class takes a prompt and can build Java Pojo out of it, it could also transform the prompt into json with
 * name and value
 * so if you pass it a json like this
 * String jsonString = "{\"lastName\":\"String\",\"firstName\":\"String\",\"reasonForCalling\":\"String\",\"dateJoined\":\"date\",\"location\":\"String\"}";
 * and then pass a prompt
 * I went to the part yesterday and met someone it was so good to meet an old friend. A customer is complaining that his computer is not working, his name is Vinod Gupta,  and he stays in Toronto he joined on 12 May 2008
 * The result will be
 * {"lastName":"Gupta","firstName":"Vinod","reasonForCalling":"computer is not working","dateJoined":"12 May 2008","location":"Toronto"}
 * Uses Gemini
 *
 */
@Slf4j

public class GeminiV2PromptTransformer implements PromptTransformer {
    Gson gson;

    public GeminiV2PromptTransformer(Gson gson) {
        this.gson = gson;
    }

    public GeminiV2PromptTransformer() {
        gson = new Gson();
    }

    public Gson getGson() {
        return gson;
    }

    @Override
    public String getJSONResponseFromAI(String prompt, String jsonStr) throws AIProcessingException {
        try {
            return ResponseHandler.getText(PredictionLoader.getInstance().getChatExplain().sendMessage(" Here is your prompt {" + prompt + "} - here is the json - " + jsonStr + " - populate the fieldValue and return the json"));
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
    }










    public String transformIntoJsonWithValues(String promptText, String... name) throws AIProcessingException{
        JsonUtils utils = new JsonUtils();
        return transformIntoJson(utils.createJson(name),promptText,"get me values", "Get me the values in json");
    }

    /**
     * Build Json from the Prompt you need to pass Json in this format
     * {"parametername","parametertype}
     *
     * you will get back the result like this
     *
     * {"parametername","parametervalue"}
     *
     * @param jsonString
     * @param promptText
     * @param funName
     * @param description
     * @return
     * @throws AIProcessingException
     */

    public String transformIntoJson(String jsonString, String promptText, String funName, String description) throws AIProcessingException {

        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(), PredictionLoader.getInstance().getLocation())) {
            return ResponseHandler.getText(PredictionLoader.getInstance().getChatExplain().sendMessage(" Here is your prompt {" + promptText + "} - here is the json - " + jsonString + " - populate the fieldValue and return the json"));

        }  catch (IOException e) {
            throw new AIProcessingException(e);
        }

    }
}
