package com.t4a.detect;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.bridge.ActionType;
import com.t4a.bridge.DetectorAction;
import com.t4a.bridge.GuardRailException;
import com.t4a.bridge.JavaMethodExecutor;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@Log
@NoArgsConstructor
@AllArgsConstructor
public class HallucinationDetector implements DetectorAction {
    static String projectId = "cookgptserver";
    static String location = "us-central1";
    static String modelName = "gemini-1.0-pro";
    private int numberOfQuestions = 4;
    private static String breakIntoQuestionPrompt = "Can you derive 4 questions from this context and provide me a single line without line breaks or backslash n character, you should reply with questions and nothing else - ";

    private static String sampleResponse = "Mohandas Karamchand Gandhi (ISO: Mōhanadāsa Karamacaṁda Gāṁdhī;[pron 1] 2 October 1869 – 30 January 1948) was an Indian lawyer, anti-colonial nationalist and political ethicist who employed nonviolent resistance to lead the successful campaign for India's independence from British rule. He inspired movements for civil rights and freedom across the world. The honorific Mahātmā (from Sanskrit 'great-souled, venerable'), first applied to him in South Africa in 1914, is now used throughout the world. Born and raised in a Hindu family in coastal Gujarat, Gandhi trained in the law at the Inner Temple in London, and was called to the bar in June 1891, at the age of 22. After two uncertain years in India, where he was unable to start a successful law practice, he moved to South Africa in 1893 to represent an Indian merchant in a lawsuit. He went on to live in South Africa for 21 years. There, Gandhi raised a family and first employed nonviolent resistance in a campaign for civil rights. In 1915, aged 45, he returned to India and soon set about organising peasants, farmers, and urban labourers to protest against discrimination and excessive land-tax.";
    @Override
    public ActionType getActionType() {
        return ActionType.HALLUCINATION;
    }

    @Override
    public String getDescription() {
        return "Detect Hallucination in response";
    }

    @Override
    public boolean execute(DetectData dd) throws GuardRailException {

        return false;
    }

    public static void main(String[] args) {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerateContentResponse response;


            GenerativeModel model =
                    GenerativeModel.newBuilder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .build();
            ChatSession chatSession = new ChatSession(model);

            response = chatSession.sendMessage(breakIntoQuestionPrompt + sampleResponse);

            String questions = ResponseHandler.getText(response);
            log.info(questions);
            JavaMethodExecutor methodAction = new JavaMethodExecutor();
            HallucinationAction questionAction = new HallucinationAction(projectId,location,modelName,sampleResponse);

            FunctionDeclaration questionActionFun = methodAction.buildFunciton(questionAction);
            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(questionActionFun)
                    .build();
            model =
                    GenerativeModel.newBuilder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();
            chatSession = new ChatSession(model);
            questions= questions.replaceAll("[\\r\\n]+", ", ");
            log.info(questions);
            response = chatSession.sendMessage("These are my 4 questions-  When was Gandhi born?,Who inspired Gandhi?Where did Gandhi first employ nonviolent resistance?Why did Gandhi leave India for South Africa?");
            log.info(""+ResponseHandler.getContent(response));
            methodAction.action(response,questionAction);


        } catch (IOException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
