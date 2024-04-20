package com.t4a.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;
import com.t4a.JsonUtils;
import com.t4a.api.JavaClassExecutor;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

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

    /**
     * Build a Java Pojo out of the Prompt
     *
     * @param promptText
     * @param className
     * @param funName
     * @param description
     * @return
     * @throws AIProcessingException
     */

    public  Object transformIntoPojoDeperecated(String promptText, String className, String funName, String description) throws AIProcessingException {
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(), PredictionLoader.getInstance().getLocation())) {
            JavaClassExecutor generator = new JavaClassExecutor(gson);
            FunctionDeclaration functionDeclaration = generator.buildFunctionFromClass(className,funName,description);

            log.debug("Function declaration h1:");
            log.debug(functionDeclaration.toString());


            //add the function to the tool
            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(functionDeclaration)
                    .build();


            GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName(PredictionLoader.getInstance().getModelName())
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();
            ChatSession chat = model.startChat();

            log.debug(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            log.debug("\nPrint response 1 : ");
            log.debug(ResponseHandler.getContent(response).toString());
            Map<String,Object> map=  generator.getPropertyValuesMapMap(response);

            String jsonString = getGson().toJson(map);
            log.debug(jsonString);
            return generator.action(response,jsonString);


        } catch (IOException e) {
            throw new AIProcessingException(e);
        } catch (ClassNotFoundException e) {
            throw new AIProcessingException(e);
        }

    }


    /**
     * Build a Java Pojo out of the Prompt
     *
     * @param prompt
     * @param className
     * @param funName
     * @param description
     * @return
     * @throws AIProcessingException
     */
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
            jsonStr = ResponseHandler.getText(PredictionLoader.getInstance().getChatExplain().sendMessage(" Here is your prompt {" + prompt + "} - here is the json - " + jsonStr + " - populate the fieldValue and return the json"));
            log.info(jsonStr);
            return util.populateClassFromJson(jsonStr);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public String transformIntoJson(String jsonString, String promptText) throws AIProcessingException{
        return transformIntoJson(jsonString,promptText,"get me values", "Get me the values in json");
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

        } catch (JsonProcessingException e) {
            throw new AIProcessingException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
