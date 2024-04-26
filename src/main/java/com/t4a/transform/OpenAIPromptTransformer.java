package com.t4a.transform;

import com.google.gson.Gson;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
/**
 * The OpenAIPromptTransformer class implements the PromptTransformer interface and provides methods for transforming prompts into Java POJOs and JSON using OpenAI's GPT-3 model.
 * It uses the Gson library for JSON processing and the PredictionLoader singleton to access the OpenAI chat model.
 */
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
    public  String getJSONResponseFromAI(String prompt, String jsonStr) {
        jsonStr = PredictionLoader.getInstance().getOpenAiChatModel().generate(" Here is your prompt {" + prompt + "} - here is the json - " + jsonStr + " - populate the fieldValue and return the json");
        return jsonStr;
    }

    @Override
    public String transformIntoJson(String json, String prompt, String funName, String description) throws AIProcessingException {
        return openAiChatModel.generate(" here is you prompt { "+prompt+"} and here is your json "+json+" - fill the json with values and return");

    }

   
}
