package com.t4a.api;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class AITools {
    private String projectId;
    private String location;
    private String modelName;

    public AITools(String projectId, String location, String modelName) {
        this.projectId = projectId;
        this.location = location;
        this.modelName = modelName;
    }

    public AITools() {

    }

    public Object action(ActionType type,GuardRails guard, Map<String,Object> arguments) throws GuardRailException {
        guard.validateRequest((String)arguments.get("promptText"));
        if (type.equals(ActionType.JAVACLASS)) {
            // Extract arguments from the map
            String promptText = (String) arguments.get("promptText");
            String className = (String) arguments.get("className");
            String funName = (String) arguments.get("funName");
            String description = (String) arguments.get("description");

            // Call invokeClass method with extracted arguments
            return actionClass(promptText, className, funName, description);
        } else return null;

    }
    public  Object actionClass(String promptText,String className, String funName, String description) {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            JavaClassExecutor generator = new JavaClassExecutor();
            FunctionDeclaration bookMyReservation = generator.buildFunction(className,funName,description);

            System.out.println("Function declaration h1:");
            System.out.println(bookMyReservation);


            //add the function to the tool
            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(bookMyReservation)
                    .build();


            GenerativeModel model =
                    GenerativeModel.newBuilder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();
            ChatSession chat = model.startChat();

            System.out.println(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            System.out.println("\nPrint response 1 : ");
            System.out.println(ResponseHandler.getContent(response));
           String jsonString = generator.getPropertyValuesJsonString(response);

            return generator.action(response,jsonString);


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

}
