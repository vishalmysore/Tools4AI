package com.t4a.detect;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.api.AIAction;
import com.t4a.api.ActionType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class HallucinationAction implements AIAction {
    private String projectId;
    private String location ;
    private String modelName ;
    private String context;


    public List<HallucinationQA> askQuestions(String questions[]) {
        List<HallucinationQA> haList = new ArrayList<HallucinationQA>();

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerativeModel model =
                  new GenerativeModel.Builder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .build();
            ChatSession chatSession = new ChatSession(model);


            for (String question : questions
            ) {
                try {

                    log.debug(question);
                    GenerateContentResponse response = chatSession.sendMessage(question);
                    String answer = ResponseHandler.getText(response);
                    response = chatSession.sendMessage("Look at both paragraphs what % of truth is there in the first paragraph based on the 2nd paragraph, Paragraph 1- "+context+" - Paragraph 2 -"+answer+" - provide your answer in just percentage and nothing else");
                    String truth =  ResponseHandler.getText(response);
                    log.debug(truth);
                    HallucinationQA hallu = new HallucinationQA(question,answer,context,truth);
                    haList.add(hallu);

                }catch (Exception e) {
                    log.error(e.getMessage());
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();

        }
        return haList;
    }

    @Override
    public String getActionName() {
        return "askQuestions";
    }

    @Override
    public String getDescription() {
        return "uses self check mechansims to detect hallucinations";
    }

    @Override
    public ActionType getActionType() {
        return ActionType.HALLUCINATION;
    }
}
