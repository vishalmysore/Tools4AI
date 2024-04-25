package com.t4a.transform;

import com.google.gson.Gson;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
/**
 * The AnthropicTransformer class implements the PromptTransformer interface and provides methods for transforming prompts into Java POJOs and JSON using Anthropic's chat model.
 * It uses the Gson library for JSON processing and the PredictionLoader singleton to access the Anthropic chat model.
 */
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
    public  String getJSONResponseFromAI(String prompt, String jsonStr) {
        jsonStr = PredictionLoader.getInstance().getAnthropicChatModel().generate(" Here is your prompt {" + prompt + "} - here is the json - " + jsonStr + " - populate the fieldValue and return the json");
        return jsonStr;
    }

    @Override
    public String transformIntoJson(String json, String prompt, String funName, String description) throws AIProcessingException {
        return anthropicChatModel.generate(" here is you prompt { " + prompt + "} and here is your json " + json + " - fill the json with values and return");

    }
}