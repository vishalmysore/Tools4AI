package com.t4a.test;

import com.t4a.JsonUtils;
import com.t4a.api.*;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.examples.ArrayAction;
import com.t4a.examples.actions.*;
import com.t4a.examples.pojo.Dictionary;
import com.t4a.examples.pojo.Organization;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

 class JavaMethodInvokerTest {
    private AIAction mockAction;
    private HumanInLoop mockHumanInLoop;
    private ExplainDecision mockExplainDecision;
    private PredictionLoader mockPredictionLoader;
    private OpenAiChatModel mockOpenAiChatModel;
    private JavaMethodInvoker mockJavaMethodInvoker;
    private JavaMethodExecutor mockJavaMethodExecutor;

    @BeforeEach
    void setUp() throws InvocationTargetException, IllegalAccessException {

        mockAction = mock(AIAction.class);
        mockHumanInLoop = mock(HumanInLoop.class);
        mockExplainDecision = mock(ExplainDecision.class);
        mockPredictionLoader = mock(PredictionLoader.class);
        mockOpenAiChatModel = mock(OpenAiChatModel.class);
        mockJavaMethodInvoker = mock(JavaMethodInvoker.class);
        mockJavaMethodExecutor = mock(JavaMethodExecutor.class);
        try (MockedStatic<PredictionLoader> mocked = Mockito.mockStatic(PredictionLoader.class)) {
            mocked.when(PredictionLoader::getInstance).thenReturn(mockPredictionLoader);
        }
        // Mock the static method getPredictedAction

        when(mockPredictionLoader.getPredictedAction(anyString(), any(AIPlatform.class))).thenReturn(mockAction);
        when(mockPredictionLoader.getOpenAiChatModel()).thenReturn(mockOpenAiChatModel);
        when(mockAction.getActionRisk()).thenReturn(ActionRisk.LOW);
        when(mockAction.getActionType()).thenReturn(ActionType.JAVAMETHOD);
        when(mockAction.getActionName()).thenReturn("Test Action");
        when(mockOpenAiChatModel.generate(anyString())).thenReturn("Test Json");
        when(mockJavaMethodInvoker.parse(anyString())).thenReturn(new Object[]{new Class<?>[0], new Object[0]});
        when(mockJavaMethodExecutor.action(anyString(), any(AIAction.class))).thenReturn("Test Result");
    }

    @Test
     void testPojoWithMapInsideClass() throws Exception {
        String jsonString = "{\n" +
                "    \"className\": \"com.t4a.examples.pojo.Dictionary\",\n" +
                "    \"fields\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"nameOfDictionary\",\n" +
                "            \"fieldType\": \"String\",\n" +
                "            \"fieldValue\": \"Hindi Kosh\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"wordMeanings\",\n" +
                "            \"valueType\": \"java.lang.String\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"value\": \"large thing\",\n" +
                "                    \"key\": \"big\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"value\": \"tiny thing\",\n" +
                "                    \"key\": \"small\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"type\": \"java.util.Map\",\n" +
                "            \"keyType\": \"java.lang.String\",\n" +
                "            \"fieldType\": \"map\",\n" +
                "            \"prompt\": \"create the key value pair and put in fields\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"locations\",\n" +
                "            \"isArray\": true,\n" +
                "            \"fieldType\": \"String[]\",\n" +
                "            \"fieldValue\": [\"class java.lang.String\"]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JsonUtils utils = new JsonUtils();
        Dictionary dict = (Dictionary) utils.populateClassFromJson(jsonString);
        Assertions.assertEquals(dict.getWordMeanings().keySet().size(),2);
        Assertions.assertEquals(dict.getWordMeanings().get("small"),"tiny thing");
    }

    @Test
     void testPojoWithArray() throws Exception {

        String jsonString = "{\n" +
                "    \"className\": \"java.util.List\",\n" +
                "    \"fields\": [\n" +
                "        {\"fieldValue\": \"Toronto\"},\n" +
                "        {\"fieldValue\": \"Bangalore\"}\n" +
                "    ],\n" +
                "    \"prompt\": \"put each value inside fieldValue\"\n" +
                "}";
        JsonUtils utils = new JsonUtils();
        List<String> list = (List<String>) utils.populateClassFromJson(jsonString);
        Assertions.assertTrue(list.size()==2);
        Assertions.assertTrue(list.get(0).equals("Toronto"));
    }
    @Test
     void testActionWithArray() throws Exception {
        String jsonString = "{\n" +
                "    \"methodName\": \"addCustomers\",\n" +
                "    \"parameters\": [{\n" +
                "        \"name\": \"allCustomers\",\n" +
                "        \"isArray\": true,\n" +
                "        \"className\": \"com.t4a.examples.actions.Customer\",\n" +
                "        \"type\": \"Customer[]\",\n" +
                "        \"fieldValue\": [{\n" +
                "                \"className\": \"com.t4a.examples.actions.Customer\",\n" +
                "                \"fields\": [{\n" +
                "                        \"fieldName\": \"firstName\",\n" +
                "                        \"fieldType\": \"String\",\n" +
                "                        \"fieldValue\": \"Vishal\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"fieldName\": \"lastName\",\n" +
                "                        \"fieldType\": \"String\",\n" +
                "                        \"fieldValue\": \"Mysore\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"fieldName\": \"reasonForCalling\",\n" +
                "                        \"description\": \"convert this to Hindi\",\n" +
                "                        \"fieldType\": \"String\",\n" +
                "                        \"fieldValue\": \"विशाल की खुशी\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"fieldName\": \"dateJoined\",\n" +
                "                        \"dateFormat\": \"yyyy-MM-dd\",\n" +
                "                        \"description\": \"if you dont find date provide todays date in fieldValue\",\n" +
                "                        \"fieldType\": \"Date\",\n" +
                "                        \"fieldValue\": \"2022-09-05\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"className\": \"com.t4a.examples.actions.Customer\",\n" +
                "                \"fields\": [{\n" +
                "                        \"fieldName\": \"firstName\",\n" +
                "                        \"fieldType\": \"String\",\n" +
                "                        \"fieldValue\": \"Deepak\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"fieldName\": \"lastName\",\n" +
                "                        \"fieldType\": \"String\",\n" +
                "                        \"fieldValue\": \"Rao\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"fieldName\": \"reasonForCalling\",\n" +
                "                        \"description\": \"convert this to Hindi\",\n" +
                "                        \"fieldType\": \"String\",\n" +
                "                        \"fieldValue\": \"\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"fieldName\": \"dateJoined\",\n" +
                "                        \"dateFormat\": \"yyyy-MM-dd\",\n" +
                "                        \"description\": \"if you dont find date provide todays date in fieldValue\",\n" +
                "                        \"fieldType\": \"Date\",\n" +
                "                        \"fieldValue\": \"2022-07-04\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ],\n" +
                "        \"prompt\": \"If you find more than 1 Customer add it as another object inside fields array  \"\n" +
                "    }],\n" +
                "    \"returnType\": \"Customer[]\"\n" +
                "}";
        ArrayAction action = new ArrayAction();
        Customer[] org = (Customer[]) callParse(action, jsonString);
        Assertions.assertTrue(org.length == 2);
        Assertions.assertEquals("Vishal", org[0].getFirstName());
        Assertions.assertEquals("Mysore", org[0].getLastName());
        Assertions.assertEquals("विशाल की खुशी", org[0].getReasonForCalling());
        Assertions.assertTrue(org[0].getDateJoined().toString().contains("Mon Sep 05 "));
        Assertions.assertEquals("Deepak", org[1].getFirstName());
        Assertions.assertEquals("Rao", org[1].getLastName());
        Assertions.assertEquals("", org[1].getReasonForCalling());
        Assertions.assertEquals("Mon Jul 04 00:00:00 EDT 2022", org[1].getDateJoined().toString());





    }

    @Test
     void testActionWithListAndArray() throws AIProcessingException, IOException, InvocationTargetException, IllegalAccessException {

        String jsonString = "{\n" +
                "    \"methodName\": \"addOrganization\",\n" +
                "    \"parameters\": [{\n" +
                "        \"name\": \"org\",\n" +
                "        \"fields\": [\n" +
                "            {\n" +
                "                \"fieldName\": \"name\",\n" +
                "                \"fieldType\": \"String\",\n" +
                "                \"fieldValue\": \"MovieHits inc\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"fieldName\": \"em\",\n" +
                "                \"className\": \"com.t4a.examples.pojo.Employee\",\n" +
                "                \"fieldType\": \"list\",\n" +
                "                \"prompt\": \"If you find more than 1 Employee add it as another object inside fields array \",\n" +
                "                \"fieldValue\": [{\n" +
                "                    \"fieldName\": \"em\",\n" +
                "                    \"className\": \"com.t4a.examples.pojo.Employee\",\n" +
                "                    \"fields\": [\n" +
                "                        {\n" +
                "                            \"fieldName\": \"name\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Shahrukh Khan\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"department\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Acting and Dancing\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"salary\",\n" +
                "                            \"fieldType\": \"double\",\n" +
                "                            \"fieldValue\": 100.0\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"location\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Toronto, Montreal, Bombay\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"dateJoined\",\n" +
                "                            \"dateFormat\": \"ddMMyyyy\",\n" +
                "                            \"description\": \"convert to actual date\",\n" +
                "                            \"fieldType\": \"Date\",\n" +
                "                            \"fieldValue\": \"07092021\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"tasks\",\n" +
                "                            \"isArray\": true,\n" +
                "                            \"fieldType\": \"String[]\",\n" +
                "                            \"fieldValue\": [\"acting\", \"dancing\"]\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"fieldType\": \"com.t4a.examples.pojo.Employee\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"em\",\n" +
                "                    \"className\": \"com.t4a.examples.pojo.Employee\",\n" +
                "                    \"fields\": [\n" +
                "                        {\n" +
                "                            \"fieldName\": \"name\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Hrithik Roshan\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"department\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Jumping and Gym\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"salary\",\n" +
                "                            \"fieldType\": \"double\",\n" +
                "                            \"fieldValue\": 0.0\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"location\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Chennai\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"dateJoined\",\n" +
                "                            \"dateFormat\": \"ddMMyyyy\",\n" +
                "                            \"description\": \"convert to actual date\",\n" +
                "                            \"fieldType\": \"Date\",\n" +
                "                            \"fieldValue\": \"15082021\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"tasks\",\n" +
                "                            \"isArray\": true,\n" +
                "                            \"fieldType\": \"String[]\",\n" +
                "                            \"fieldValue\": [\"jumping\", \"gym\"]\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"fieldType\": \"com.t4a.examples.pojo.Employee\"\n" +
                "                }]\n" +
                "            },\n" +
                "            {\n" +
                "                \"fieldName\": \"locations\",\n" +
                "                \"description\": \"there could be multiple String\",\n" +
                "                \"className\": \"java.lang.String\",\n" +
                "                \"fieldType\": \"list\",\n" +
                "                \"fieldValue\": [\"Toronto\", \"Montreal\", \"Bombay\", \"Chennai\"]\n" +
                "            },\n" +
                "            {\n" +
                "                \"fieldName\": \"customers\",\n" +
                "                \"isArray\": true,\n" +
                "                \"className\": \"com.t4a.examples.actions.Customer\",\n" +
                "                \"type\": \"Customer[]\",\n" +
                "                \"fieldValue\": [{\n" +
                "                    \"className\": \"com.t4a.examples.actions.Customer\",\n" +
                "                    \"fields\": [\n" +
                "                        {\n" +
                "                            \"fieldName\": \"firstName\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Vishal\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"lastName\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Mysore\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"reasonForCalling\",\n" +
                "                            \"description\": \"convert this to Hindi\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"वह उन्हें फिल्म की तारीख पसंद नहीं थी\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"dateJoined\",\n" +
                "                            \"dateFormat\": \"yyyy-MM-dd\",\n" +
                "                            \"description\": \"if you dont find date provide todays date in fieldValue\",\n" +
                "                            \"fieldType\": \"Date\",\n" +
                "                            \"fieldValue\": \"2021-09-13\"\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"className\": \"com.t4a.examples.actions.Customer\",\n" +
                "                    \"fields\": [\n" +
                "                        {\n" +
                "                            \"fieldName\": \"firstName\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Deepak\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"lastName\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Rao\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"reasonForCalling\",\n" +
                "                            \"description\": \"convert this to Hindi\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"वह उन्हें फिल्म की तारीख पसंद नहीं थी\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"dateJoined\",\n" +
                "                            \"dateFormat\": \"yyyy-MM-dd\",\n" +
                "                            \"description\": \"if you dont find date provide todays date in fieldValue\",\n" +
                "                            \"fieldType\": \"Date\",\n" +
                "                            \"fieldValue\": \"2021-09-13\"\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }],\n" +
                "                \"prompt\": \"If you find more than 1 Customer add it as another object inside fields array \",\n" +
                "                \"fieldType\": \"Customer[]\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"type\": \"com.t4a.examples.pojo.Organization\"\n" +
                "    }],\n" +
                "    \"returnType\": \"Organization\"\n" +
                "}";
        ListAction action = new ListAction();
        Organization org = (Organization) callParse(action,jsonString);
        Assertions.assertTrue(org.getEm().get(0).getName().contains("Shahrukh"));
        Assertions.assertTrue(org.getEm().get(1).getName().contains("Hrithik"));
        Assertions.assertEquals("MovieHits inc", org.getName());
        Assertions.assertEquals(2, org.getEm().size());
        Assertions.assertEquals(4, org.getLocations().size());
        Assertions.assertEquals(2, org.getCustomers().length);
        Assertions.assertEquals("Vishal", org.getCustomers()[0].getFirstName());
        Assertions.assertEquals("Mysore", org.getCustomers()[0].getLastName());
        Assertions.assertEquals("वह उन्हें फिल्म की तारीख पसंद नहीं थी", org.getCustomers()[0].getReasonForCalling());
        Assertions.assertTrue( org.getCustomers()[0].getDateJoined().toString().contains("Mon Sep 13"));
        Assertions.assertEquals("Deepak", org.getCustomers()[1].getFirstName());
        Assertions.assertEquals("Rao", org.getCustomers()[1].getLastName());
        Assertions.assertEquals("वह उन्हें फिल्म की तारीख पसंद नहीं थी", org.getCustomers()[1].getReasonForCalling());
        Assertions.assertEquals("Mon Sep 13 00:00:00 EDT 2021", org.getCustomers()[1].getDateJoined().toString());


    }

    @Test
     void testInvokeForComplexObject() throws Exception {
        JsonUtils utils = new JsonUtils();
        String jsonString = "{\n" +
                "    \"methodName\": \"addOrganization\",\n" +
                "    \"parameters\": [{\n" +
                "        \"name\": \"org\",\n" +
                "        \"fields\": [\n" +
                "            {\n" +
                "                \"fieldName\": \"name\",\n" +
                "                \"fieldType\": \"String\",\n" +
                "                \"fieldValue\": \"MovieHits inc\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"fieldName\": \"em\",\n" +
                "                \"className\": \"com.t4a.examples.pojo.Employee\",\n" +
                "                \"fieldType\": \"list\",\n" +
                "                \"prompt\": \"If you find more than 1 Employee add it as another object inside fields array \",\n" +
                "                \"fieldValue\": [{\n" +
                "                    \"fieldName\": \"em\",\n" +
                "                    \"className\": \"com.t4a.examples.pojo.Employee\",\n" +
                "                    \"fields\": [\n" +
                "                        {\n" +
                "                            \"fieldName\": \"name\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Shahrukh Khan\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"department\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Acting and Dancing\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"salary\",\n" +
                "                            \"fieldType\": \"double\",\n" +
                "                            \"fieldValue\": 100.0\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"location\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": [\"Toronto\",\"Montreal\",\"Bombay\"]\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"dateJoined\",\n" +
                "                            \"dateFormat\": \"ddMMyyyy\",\n" +
                "                            \"description\": \"convert to actual date\",\n" +
                "                            \"fieldType\": \"Date\",\n" +
                "                            \"fieldValue\": \"01092021\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"tasks\",\n" +
                "                            \"isArray\": true,\n" +
                "                            \"fieldType\": \"String[]\",\n" +
                "                            \"fieldValue\": [\"Acting\",\"Dancing\"]\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"fieldType\": \"com.t4a.examples.pojo.Employee\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"em\",\n" +
                "                    \"className\": \"com.t4a.examples.pojo.Employee\",\n" +
                "                    \"fields\": [\n" +
                "                        {\n" +
                "                            \"fieldName\": \"name\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Hrithik Roshan\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"department\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Jumping and Gym\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"salary\",\n" +
                "                            \"fieldType\": \"double\",\n" +
                "                            \"fieldValue\": 0.0\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"location\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": [\"Chennai\"]\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"dateJoined\",\n" +
                "                            \"dateFormat\": \"ddMMyyyy\",\n" +
                "                            \"description\": \"convert to actual date\",\n" +
                "                            \"fieldType\": \"Date\",\n" +
                "                            \"fieldValue\": \"15082021\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"tasks\",\n" +
                "                            \"isArray\": true,\n" +
                "                            \"fieldType\": \"String[]\",\n" +
                "                            \"fieldValue\": [\"Jumping\",\"Gym\"]\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"fieldType\": \"com.t4a.examples.pojo.Employee\"\n" +
                "                }]\n" +
                "            },\n" +
                "            {\n" +
                "                \"fieldName\": \"locations\",\n" +
                "                \"description\": \"there could be multiple String\",\n" +
                "                \"className\": \"java.lang.String\",\n" +
                "                \"fieldType\": \"list\",\n" +
                "                \"fieldValue\": [\"Toronto\",\"Montreal\",\"Bombay\",\"Chennai\"]\n" +
                "            },\n" +
                "            {\n" +
                "                \"fieldName\": \"customers\",\n" +
                "                \"isArray\": true,\n" +
                "                \"className\": \"com.t4a.examples.actions.Customer\",\n" +
                "                \"type\": \"Customer[]\",\n" +
                "                \"fieldValue\": [],\n" +
                "                \"prompt\": \"If you find more than 1 Customer add it as another object inside fields array \",\n" +
                "                \"fieldType\": \"Customer[]\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"type\": \"com.t4a.examples.pojo.Organization\"\n" +
                "    }],\n" +
                "    \"returnType\": \"Organization\"\n" +
                "}";
        try (MockedStatic<PredictionLoader> mocked = Mockito.mockStatic(PredictionLoader.class)) {
            ListAction action =  new ListAction();

            Organization result = (Organization)callParse(action, jsonString);

                Assertions.assertEquals("MovieHits inc", result.getName());
               Assertions.assertEquals(2, result.getEm().size());
                Assertions.assertEquals("Shahrukh Khan", result.getEm().get(0).getName());
                Assertions.assertEquals("Hrithik Roshan", result.getEm().get(1).getName());
                Assertions.assertEquals(100.0, result.getEm().get(0).getSalary());
                Assertions.assertEquals(0.0, result.getEm().get(1).getSalary());
                Assertions.assertEquals("Jumping and Gym", result.getEm().get(1).getDepartment());
                Assertions.assertEquals("Acting and Dancing", result.getEm().get(0).getDepartment());
                Assertions.assertEquals("[\"Toronto\",\"Montreal\",\"Bombay\"]", result.getEm().get(0).getLocation());
                Assertions.assertEquals("[\"Toronto\",\"Montreal\",\"Bombay\"]", result.getEm().get(0).getLocation());
                Assertions.assertEquals("[\"Toronto\",\"Montreal\",\"Bombay\"]", result.getEm().get(0).getLocation());
                Assertions.assertEquals("[\"Chennai\"]", result.getEm().get(1).getLocation());
                Assertions.assertEquals("Acting", result.getEm().get(0).getTasks()[0]);
                Assertions.assertEquals("Dancing", result.getEm().get(0).getTasks()[1]);
                Assertions.assertEquals("Jumping", result.getEm().get(1).getTasks()[0]);
                Assertions.assertEquals("Gym", result.getEm().get(1).getTasks()[1]);
                Assertions.assertEquals("Toronto", result.getLocations().get(0));
                Assertions.assertEquals("Montreal", result.getLocations().get(1));
                Assertions.assertEquals("Bombay", result.getLocations().get(2));
                Assertions.assertEquals("Chennai", result.getLocations().get(3));
                Assertions.assertEquals(0, result.getCustomers().length);







        }
    }
    @Test
     void testInvokeForArray() throws Exception {
        String jsonString = "{\n" +
                "    \"methodName\": \"allTheDates\",\n" +
                "    \"parameters\": [{\n" +
                "        \"name\": \"allTheDates\",\n" +
                "        \"isArray\": true,\n" +
                "        \"className\": \"java.lang.String\",\n" +
                "        \"type\": \"String[]\",\n" +
                "        \"fieldValue\": [\"2nd August\", \"3rd September\", \"11th November\"],\n" +
                "        \"prompt\": \"If you find more than 1 String add it as another object inside fields array  \"\n" +
                "    }],\n" +
                "    \"returnType\": \"String[]\"\n" +
                "}";
        ArrayOfObjectAction action = new ArrayOfObjectAction();
        String[] result = (String[])callParse(action, jsonString);
        Assertions.assertEquals("2nd August", result[0]);
    }

    @Test
     void testComplexActionOpenAI() throws AIProcessingException, IOException, InvocationTargetException, IllegalAccessException {

        String jsonString = "{\n" +
                "    \"methodName\": \"notifyPlayerAndRestaurant\",\n" +
                "    \"parameters\": [\n" +
                "        {\n" +
                "            \"name\": \"player\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"fieldName\": \"matches\",\n" +
                "                    \"fieldType\": \"int\",\n" +
                "                    \"fieldValue\": \"400\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"maxScore\",\n" +
                "                    \"fieldType\": \"int\",\n" +
                "                    \"fieldValue\": \"1000\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"firstName\",\n" +
                "                    \"fieldType\": \"String\",\n" +
                "                    \"fieldValue\": \"Sachin\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"lastName\",\n" +
                "                    \"fieldType\": \"String\",\n" +
                "                    \"fieldValue\": \"Tendulkar\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"type\": \"com.t4a.examples.actions.Player\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"restaurantPojo\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"fieldName\": \"name\",\n" +
                "                    \"fieldType\": \"String\",\n" +
                "                    \"fieldValue\": \"Maharaja\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"numberOfPeople\",\n" +
                "                    \"fieldType\": \"int\",\n" +
                "                    \"fieldValue\": \"5\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"restaurantDetails\",\n" +
                "                    \"fields\": [\n" +
                "                        {\n" +
                "                            \"fieldName\": \"name\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Maharaja\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"fieldName\": \"location\",\n" +
                "                            \"fieldType\": \"String\",\n" +
                "                            \"fieldValue\": \"Toronto\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"fieldType\": \"com.t4a.examples.basic.RestaurantDetails\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"cancel\",\n" +
                "                    \"fieldType\": \"boolean\",\n" +
                "                    \"fieldValue\": \"false\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"reserveDate\",\n" +
                "                    \"fieldType\": \"String\",\n" +
                "                    \"fieldValue\": \"Indian Independence Day\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"type\": \"com.t4a.examples.basic.RestaurantPojo\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"returnType\": \"String\"\n" +
                "}";
        PlayerWithRestaurant playerAc = new PlayerWithRestaurant();

        String result = (String) callParse(playerAc, jsonString);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(playerAc.getRestaurantPojo());
        Assertions.assertNotNull(playerAc.getPlayer());
        Assertions.assertEquals(playerAc.getPlayer().getFirstName(),"Sachin");
        Assertions.assertEquals(playerAc.getPlayer().getLastName(),"Tendulkar");
    }

    private Object callParse(Object instance, String jsonString) throws InvocationTargetException, IllegalAccessException, AIProcessingException {
        GenericJavaMethodAction action = new GenericJavaMethodAction(instance);

        Method m = action.getActionMethod();

        JavaMethodInvoker invoker = new JavaMethodInvoker();
        Object obj[] = invoker.parse(jsonString);

        List<Object> parameterValues = (List<Object>) obj[1];
        List<Class<?>> parameterTypes = (List<Class<?>>) obj[0];
        Method method = null;
        try {
            method = action.getActionClass().getMethod(m.getName(), parameterTypes.toArray(new Class<?>[0]));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
       return method.invoke(action.getActionInstance(), parameterValues.toArray());
    }

    @Test
    public void testGoogleSearch() {
        String jsonString = "{\n" +
                "    \"methodName\": \"googleSearch\",\n" +
                "    \"parameters\": [\n" +
                "        {\n" +
                "            \"name\": \"searchString\",\n" +
                "            \"type\": \"String\",\n" +
                "            \"fieldValue\": \"Toronto weather forecast\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"isNews\",\n" +
                "            \"type\": \"boolean\",\n" +
                "            \"fieldValue\": false\n" +
                "        }\n" +
                "    ],\n" +
                "    \"returnType\": \"String\"\n" +
                "}";
        SearchAction action = new SearchAction();
        try {
            String result = (String) callParse(action, jsonString);
            Assertions.assertNotNull(action.searchString);
            Assertions.assertEquals("Toronto weather forecast", action.searchString);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (AIProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
