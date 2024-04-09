package com.t4a.predict;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.google.gson.Gson;
import com.t4a.JsonUtils;
import com.t4a.processor.AIProcessingException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenAIPromptTransformer implements PromptTransformer{
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


    public Object transformIntoPojoDeprecated(String prompt, String className, String funName, String description) throws AIProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        Class clazz  = null;
        try {
            clazz = Class.forName(className);
            mapper.acceptJsonFormatVisitor(clazz, visitor);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        JsonSchema jsonSchema = visitor.finalSchema();
        JsonNode propertiesNode = mapper.valueToTree(jsonSchema).path("properties");

        try {
            String json =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(propertiesNode);
            String response = openAiChatModel.generate(" here is you prompt { "+prompt+"} and here is your json "+json+" - fill the json with values and return");
            Object ret = gson.fromJson(response,(clazz))   ;
            return ret;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Object transformIntoPojo(String prompt, String className, String funName, String description) throws AIProcessingException {
        try {
            JsonUtils util = new JsonUtils();
            Class clazz = Class.forName(className);
            String jsonStr = null;
            if(clazz.getName().equalsIgnoreCase("java.util.Map")) {
                jsonStr = util.buildBlankMapJsonObject(null).toString(4); ;

            } if(clazz.getName().equalsIgnoreCase("java.util.List")) {
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


}
