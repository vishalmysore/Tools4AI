package com.t4a.predict;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;
import com.t4a.api.JavaClassExecutor;
import com.t4a.processor.AIProcessingException;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * This class takes a prompt and build Java Pojo out of it
 */
@Log
@NoArgsConstructor
public class PojoBuilder {
    public  Object buildPojo(String promptText,String className, String funName, String description) throws AIProcessingException {
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(), PredictionLoader.getInstance().getLocation())) {
            JavaClassExecutor generator = new JavaClassExecutor();
            FunctionDeclaration functionDeclaration = generator.buildFunctionFromClass(className,funName,description);

            log.info("Function declaration h1:");
            log.info(functionDeclaration.toString());


            //add the function to the tool
            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(functionDeclaration)
                    .build();


            GenerativeModel model =
                    GenerativeModel.newBuilder()
                            .setModelName(PredictionLoader.getInstance().getModelName())
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();
            ChatSession chat = model.startChat();

            log.info(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            log.info("\nPrint response 1 : ");
            log.info(ResponseHandler.getContent(response).toString());
            Map<String,Object> map=  generator.getPropertyValuesMapMap(response);
            Gson gson = new Gson();
            String jsonString = gson.toJson(map);
            log.info(jsonString);
            return generator.action(response,jsonString);


        } catch (IOException e) {
            throw new AIProcessingException(e);
        } catch (ClassNotFoundException e) {
            throw new AIProcessingException(e);
        }


    }
}
