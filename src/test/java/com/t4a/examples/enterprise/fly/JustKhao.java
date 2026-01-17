package com.t4a.examples.enterprise.fly;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.api.ActionType;
import com.t4a.api.JavaMethodAction;
import com.t4a.api.JavaMethodExecutor;
import com.t4a.processor.AIProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
@Slf4j
public class JustKhao implements JavaMethodAction {
    public JustKhao(){

    }
    public static void main(String[] args) throws IOException, AIProcessingException {

        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";

        String promptText = "can you book a dinner reseration for Vishal his family member and 4 other people at Maharaj on 1st august and make sure its not cancellable, ";

        String status = khao(projectId, location, modelName, promptText);
        log.debug(promptText+ " : "+status);
    }

    public static String khao(String projectId, String location, String modelName,String promptText) throws AIProcessingException {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            JavaMethodExecutor generator = new JavaMethodExecutor();
            JustKhao justKhao = new JustKhao();
            FunctionDeclaration bookMyReservation = generator.buildFunction(justKhao);

            log.debug("Function declaration h1:");
            log.debug(""+bookMyReservation);


            //add the function to the tool
            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(bookMyReservation)
                    .build();


            GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();
            ChatSession chat = model.startChat();

            log.debug(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            log.debug("\nPrint response 1 : ");
            log.debug(""+ResponseHandler.getContent(response));
            log.debug(generator.getPropertyValuesJsonString(response));

           return (String) generator.action(response,justKhao);


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }

    public String bookMyReservation(String name , int numberOfPeople, String restaurantName, boolean cancel, String reserveDate) {
        log.debug(name +":"+numberOfPeople+":"+restaurantName+":"+cancel);
        return "reserved";
    }

    @Override
    public String getDescription() {
        return "Booking Reservation Function";
    }

    @Override
    public String getPrompt() {
        return "";
    }

    @Override
    public String getSubprompt() {
        return "";
    }

    @Override
    public ActionType getActionType() {
        return ActionType.JAVAMETHOD;
    }

    @Override
    public String getActionName() {
        return "bookMyReservation";
    }
}
