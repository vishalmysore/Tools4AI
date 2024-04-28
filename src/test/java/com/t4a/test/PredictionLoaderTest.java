package com.t4a.test;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.api.AIAction;
import com.t4a.api.AIPlatform;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.predict.PredictionLoader;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class PredictionLoaderTest {
    ChatSession chatMock;


    GenerativeModel.Builder builderMock;
    GenerativeModel generativeModelMock;
    @Mock
    private AIAction aiAction;


    @BeforeEach
    public void setUp() {

        MockitoAnnotations.initMocks(this);

         generativeModelMock = Mockito.mock(GenerativeModel.class);

// Mock the Builder
      builderMock = Mockito.mock(GenerativeModel.Builder.class);

// When Builder's setModelName is called, return the builderMock
        when(builderMock.setModelName(anyString())).thenReturn(builderMock);

// When Builder's setVertexAi is called, return the builderMock
        when(builderMock.setVertexAi(any(VertexAI.class))).thenReturn(builderMock);

// When Builder's build is called, return the generativeModelMock
        when(builderMock.build()).thenReturn(generativeModelMock);
         chatMock = Mockito.mock(ChatSession.class);




// When startChat is called on the models, return the corresponding mock ChatSession
        when(generativeModelMock.startChat()).thenReturn(chatMock);

    }

    @Test
    public void testGenerativeModelMock() {


        try (MockedConstruction<GenerativeModel.Builder> mocked = Mockito.mockConstruction(GenerativeModel.Builder.class,
                (mock, context) -> {
                    Mockito.when(mock.setModelName(anyString())).thenReturn(mock);
                    Mockito.when(mock.setVertexAi(any(VertexAI.class))).thenReturn(mock);
                    Mockito.when(mock.build()).thenReturn(generativeModelMock);
                })) {




// When sendMessage is called on the chatMock, return the responseMock

            try (MockedStatic<ResponseHandler> responseHandlerMock = Mockito.mockStatic(ResponseHandler.class)) {

                GenerateContentResponse responseMockGRP = Mockito.mock(GenerateContentResponse.class);
                when(responseMockGRP.getCandidatesCount()).thenReturn(1);
                 // When getText is called on the responseHandlerMock, return a predefined string
              //  when(chatMock.sendMessage(argThat((String argument) -> argument.contains("which group does this prompt belong")))).thenReturn(responseMockGRP);
                String mockGRP = "{'groupName':'No Group','explanation':'mock'}\";";
                responseHandlerMock.when(() -> ResponseHandler.getText(any(GenerateContentResponse.class))).thenReturn(mockGRP);

                GenerateContentResponse responseMockACT = Mockito.mock(GenerateContentResponse.class);
                when(responseMockACT.getCandidatesCount()).thenReturn(1);
                // When getText is called on the responseHandlerMock, return a predefined string
               // when(chatMock.sendMessage(argThat((String argument) -> argument.contains("test the action")))).thenReturn(responseMockACT);

                when(chatMock.sendMessage(argThat((String argument) -> {
                    if (argument.contains("reply back with just one action name in json format")) {
                        return true;
                    } else if (argument.contains("which group does this prompt belong")) {
                        return true;
                    } else {
                        return false;
                    }
                }))).thenAnswer(new Answer<GenerateContentResponse>() {
                                    @Override
                                    public GenerateContentResponse answer(InvocationOnMock invocation) throws Throwable {
                                        String argument = (String) invocation.getArguments()[0];
                                        if (argument.contains("reply back with just one action name in json format")) {
                                            return responseMockACT;
                                        } else if (argument.contains("which group does this prompt belong")) {
                                            return responseMockGRP;
                                        } else {
                                            return null;
                                        }
                                    }

                });




                Assertions.assertNull(PredictionLoader.getInstance().getPredictedAction("test the action", AIPlatform.GEMINI));
            }
            // Add your test assertions here
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testGenerativeModelMockAction() {


        try (MockedConstruction<GenerativeModel.Builder> mocked = Mockito.mockConstruction(GenerativeModel.Builder.class,
                (mock, context) -> {
                    Mockito.when(mock.setModelName(anyString())).thenReturn(mock);
                    Mockito.when(mock.setVertexAi(any(VertexAI.class))).thenReturn(mock);
                    Mockito.when(mock.build()).thenReturn(generativeModelMock);
                })) {




// When sendMessage is called on the chatMock, return the responseMock

            try (MockedStatic<ResponseHandler> responseHandlerMock = Mockito.mockStatic(ResponseHandler.class)) {

                GenerateContentResponse responseMockGRP = Mockito.mock(GenerateContentResponse.class);
                when(responseMockGRP.getCandidatesCount()).thenReturn(1);
                // When getText is called on the responseHandlerMock, return a predefined string
                //  when(chatMock.sendMessage(argThat((String argument) -> argument.contains("which group does this prompt belong")))).thenReturn(responseMockGRP);
                String mockGRP = "{'groupName':'No Group','explanation':'mock'}\";";
                responseHandlerMock.when(() -> ResponseHandler.getText(any(GenerateContentResponse.class))).thenReturn(mockGRP);

                GenerateContentResponse responseMockACT = Mockito.mock(GenerateContentResponse.class);
                when(responseMockACT.getCandidatesCount()).thenReturn(1);
                // When getText is called on the responseHandlerMock, return a predefined string
                // when(chatMock.sendMessage(argThat((String argument) -> argument.contains("test the action")))).thenReturn(responseMockACT);

                when(chatMock.sendMessage(argThat((String argument) -> {
                    if (argument.contains("reply back with just one action name in json format")) {
                        return true;
                    } else if (argument.contains("which group does this prompt belong")) {
                        return true;
                    } else {
                        return false;
                    }
                }))).thenAnswer(new Answer<GenerateContentResponse>() {
                    @Override
                    public GenerateContentResponse answer(InvocationOnMock invocation) throws Throwable {
                        String argument = (String) invocation.getArguments()[0];
                        if (argument.contains("reply back with just one action name in json format")) {
                            return responseMockACT;
                        } else if (argument.contains("which group does this prompt belong")) {
                            return responseMockGRP;
                        } else {
                            return null;
                        }
                    }

                });
                String mockACT = "{'actionName':'whatFoodDoesThisPersonLike','explanation':'mock'}\";";
                responseHandlerMock.when(() -> ResponseHandler.getText(eq(responseMockACT))).thenReturn(mockACT);
                responseHandlerMock.when(() -> ResponseHandler.getText(eq(responseMockGRP))).thenReturn(mockGRP);



                GenericJavaMethodAction action = (GenericJavaMethodAction) PredictionLoader.getInstance().getPredictedAction("test the action", AIPlatform.GEMINI);
                Assertions.assertEquals("whatFoodDoesThisPersonLike", action.getActionName());
            }
            // Add your test assertions here
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGenerativeModelMockOpenAI() {
        try (MockedStatic<OpenAiChatModel> responseHandlerMock = Mockito.mockStatic(OpenAiChatModel.class)) {
            System.setProperty("openAiKey", "test");
            System.setProperty("tools4ai.log", "DEBUG");
            OpenAiChatModel mockGRP = Mockito.mock(OpenAiChatModel.class);
            responseHandlerMock.when(() -> OpenAiChatModel.withApiKey(any(String.class))).thenReturn(mockGRP);
            when(mockGRP.generate(argThat((String argument) -> {
                if (argument.contains("what action do you think we should take out of these")) {
                    return true;
                } else if (argument.contains("which group does it belong")) {
                    return true;
                } else {
                    return false;
                }
            }))).thenAnswer(new Answer<String>() {
                @Override
                public String answer(InvocationOnMock invocation) throws Throwable {
                    String argument = (String) invocation.getArguments()[0];
                    if (argument.contains("what action do you think we should take out of these")) {
                        return "whatFoodDoesThisPersonLike";
                    } else if (argument.contains("which group does it belong")) {
                        return "No Group";
                    } else {
                        return null;
                    }
                }

            });

            PredictionLoader.getInstance().setOpenAiChatModel(mockGRP);
            GenericJavaMethodAction action = (GenericJavaMethodAction)PredictionLoader.getInstance().getPredictedAction("test the action", AIPlatform.OPENAI);
            Assertions.assertEquals("whatFoodDoesThisPersonLike", action.getActionName());
        }
    }
}
