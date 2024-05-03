package com.t4a.regression;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.examples.ArrayAction;
import com.t4a.examples.actions.*;
import com.t4a.examples.basic.DateDeserializer;
import com.t4a.examples.basic.RestaurantPojo;
import com.t4a.examples.pojo.Dictionary;
import com.t4a.examples.pojo.MyDiary;
import com.t4a.examples.pojo.Organization;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.OpenAiActionProcessor;
import com.t4a.transform.OpenAIPromptTransformer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
 class OpenAIActionValidation {
    @Test
     void testRestaurantPojoOpenAI() throws AIProcessingException {
        String promptText = "can you book a dinner reseration in name of Vishal and his family of 4 at Maharaj restaurant in Toronto, on Indian Independence day and make sure its cancellable";
        OpenAIPromptTransformer tools = new OpenAIPromptTransformer();
        RestaurantPojo pojo = (RestaurantPojo) tools.transformIntoPojo(promptText, "com.t4a.examples.basic.RestaurantPojo", "RestaurantClass", "Create Pojo from the prompt");
        Assertions.assertTrue(pojo != null);
        Assertions.assertTrue(pojo.getName().contains("Vishal"));
        Assertions.assertEquals(pojo.getNumberOfPeople(), 4);
        Assertions.assertTrue(pojo.getRestaurantDetails().getName().contains("Maharaj"));
        Assertions.assertEquals(pojo.getRestaurantDetails().getLocation(), "Toronto");

    }

    @Test
     void testCustomerPojoOpenAI() throws AIProcessingException, IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer("dd MMMM yyyy"));
        Gson gson = gsonBuilder.create();
        OpenAIPromptTransformer tools2 = new OpenAIPromptTransformer(gson);

        Customer pojo = (Customer) tools2.transformIntoPojo("I went to the part yesterday and met someone it was so good to meet an old friend. A customer is complaining that his computer is not working, his name is Vinod Gupta,  and he stays in Toronto he joined on 12 May 2008", Customer.class.getName(),"Customer", "get Customer details");
        Assertions.assertTrue(pojo != null);
         String reasonMatches = TestHelperOpenAI.getInstance().sendMessage("reply in true or false only - is this "+pojo.getReasonForCalling()+" same as computer not working");
         Assertions.assertTrue("True".equalsIgnoreCase(reasonMatches));
        Assertions.assertEquals(pojo.getFirstName(),"Vinod");
        Assertions.assertEquals(pojo.getLastName(),"Gupta");
    }

    @Test
     void testComplexActionOpenAI() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String prm = "Sachin Tendulkar is a cricket player and he has played 400 matches, his max score is 1000, he wants to go to " +
                "Maharaja restaurant in toronto with 4 of his friends on Indian Independence Day, can you notify him and the restarurant";
        PlayerWithRestaurant playerAc = new PlayerWithRestaurant();
        String result = (String)processor.processSingleAction(prm,playerAc,"notifyPlayerAndRestaurant");
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(playerAc.getRestaurantPojo());
        Assertions.assertNotNull(playerAc.getPlayer());
        Assertions.assertEquals(playerAc.getPlayer().getFirstName(),"Sachin");
        Assertions.assertEquals(playerAc.getPlayer().getLastName(),"Tendulkar");
    }

    @Test
     void testHttpActionOpenAI() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String postABook = "post a book harry poster with id 189 the publish date is 2024-03-22 and the description is about harry who likes poster its around 500 pages  ";
        String result = (String)processor.processSingleAction(postABook);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));

    }

    @Test
     void testShellActionOpenAI() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String shellAction = "An Employee joined the organization, his name is Vishal and his location is Toronto, save this information ";
        String result = (String)processor.processSingleAction(shellAction);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }

    @Test
     void testJavaMethod() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String weatherAction = "ey I am in Toronto do you think i can go out without jacket";
        Double result = (Double)processor.processSingleAction(weatherAction);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }

    /**
     * {
     *     "methodName": "saveInformationToLocalFile",
     *     "parameters": [{
     *         "name": "args",
     *         "isArray": true,
     *         "className": "java.lang.String",
     *         "type": "String[]",
     *         "fieldValue": ["Vishal", "Toronto"],
     *         "prompt": "If you find more than 1 String add it as another object inside fields array  "
     *     }],
     *     "returnType": "Object"
     * }
     * @throws AIProcessingException
     * @throws IOException
     */
    @Test
     void testJavaMethodForFile() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String weatherAction = "My friends name is Vishal ,he lives in toronto.I want save this info locally";
        String result = (String)processor.processSingleAction(weatherAction);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }

    /*
    This is the JSON representation of the action for testActionWithList if it is not returinng then the problem
    is with AI
    {
    "methodName": "addOrganization",
    "parameters": [{
        "name": "org",
        "fields": [
            {
                "fieldName": "name",
                "fieldType": "String",
                "fieldValue": "MovieHits inc"
            },
            {
                "fieldName": "em",
                "className": "com.t4a.examples.pojo.Employee",
                "fieldType": "list",
                "prompt": "If you find more than 1 Employee add it as another object inside fields array ",
                "fieldValue": [{
                    "fieldName": "em",
                    "className": "com.t4a.examples.pojo.Employee",
                    "fields": [
                        {
                            "fieldName": "name",
                            "fieldType": "String",
                            "fieldValue": "Shahrukh Khan"
                        },
                        {
                            "fieldName": "department",
                            "fieldType": "String",
                            "fieldValue": ""
                        },
                        {
                            "fieldName": "salary",
                            "fieldType": "double",
                            "fieldValue": 100.0
                        },
                        {
                            "fieldName": "location",
                            "fieldType": "String",
                            "fieldValue": "Toronto, Montreal, Bombay"
                        },
                        {
                            "fieldName": "dateJoined",
                            "dateFormat": "ddMMyyyy",
                            "description": "convert to actual date",
                            "fieldType": "Date",
                            "fieldValue": "07092021"
                        },
                        {
                            "fieldName": "tasks",
                            "isArray": true,
                            "fieldType": "String[]",
                            "fieldValue": ["acting", "dancing"]
                        }
                    ],
                    "fieldType": "com.t4a.examples.pojo.Employee"
                },
                {
                    "fieldName": "em",
                    "className": "com.t4a.examples.pojo.Employee",
                    "fields": [
                        {
                            "fieldName": "name",
                            "fieldType": "String",
                            "fieldValue": "Hrithik Roshan"
                        },
                        {
                            "fieldName": "department",
                            "fieldType": "String",
                            "fieldValue": ""
                        },
                        {
                            "fieldName": "salary",
                            "fieldType": "double",
                            "fieldValue": 0.0
                        },
                        {
                            "fieldName": "location",
                            "fieldType": "String",
                            "fieldValue": "Chennai"
                        },
                        {
                            "fieldName": "dateJoined",
                            "dateFormat": "ddMMyyyy",
                            "description": "convert to actual date",
                            "fieldType": "Date",
                            "fieldValue": "15082021"
                        },
                        {
                            "fieldName": "tasks",
                            "isArray": true,
                            "fieldType": "String[]",
                            "fieldValue": ["jumping", "Gym"]
                        }
                    ],
                    "fieldType": "com.t4a.examples.pojo.Employee"
                }]
            },
            {
                "fieldName": "locations",
                "description": "there could be multiple String",
                "className": "java.lang.String",
                "fieldType": "list",
                "fieldValue": ["Toronto", "Montreal", "Bombay", "Chennai"]
            },
            {
                "fieldName": "customers",
                "isArray": true,
                "className": "com.t4a.examples.actions.Customer",
                "type": "Customer[]",
                "fieldValue": [],
                "prompt": "If you find more than 1 Customer add it as another object inside fields array ",
                "fieldType": "Customer[]"
            }
        ],
        "type": "com.t4a.examples.pojo.Organization"
    }],
    "returnType": "Organization"
}
     */
    @Test
     void testActionWithList() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String promptText = "Shahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay.Hrithik roshan is another employee of same company based in Chennai his taks are jumping and Gym he joined on Indian Independce Day";
        ListAction action = new ListAction();
        Organization org = (Organization) processor.processSingleAction(promptText,action,"addOrganization");
        Assertions.assertTrue(org.getEm().get(0).getName().contains("Shahrukh"));
        Assertions.assertTrue(org.getEm().get(1).getName().contains("Hrithik"));
    }

    @Test
     void testActionWithListAndArray() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();

               String promptText = "Shahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay.Hrithik roshan is another employee of MovieHits inc based in Chennai his taks are jumping and Gym he joined on Indian Independce Day.Vishal Mysore is customer of MovieHits inc and he styas in Paris the reason he is not happy is that he did nto like the movie date is labor day. Deepak Rao is another customer for MovieHits inc date is independence day";
        ListAction action = new ListAction();
        Organization org = (Organization) processor.processSingleAction(promptText,action,"addOrganization");
        Assertions.assertTrue(org.getEm().get(0).getName().contains("Shahrukh"));
        Assertions.assertTrue(org.getEm().get(1).getName().contains("Hrithik"));
    }

    @Test
     void testActionWithMap() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String promptText = "id of Cricket is 1 and then Polo is at 5, Footbal is at 9";
        MapAction action = new MapAction();
        Map<Integer,String> map = (Map<Integer,String>) processor.processSingleAction(promptText,action,"addSports");
        Assertions.assertEquals(map.keySet().size(),3);
        Assertions.assertEquals(map.get("1"),"Cricket");

    }

    @Test
     void testPojoWithMap() throws AIProcessingException, IOException {
        OpenAIPromptTransformer tra = new OpenAIPromptTransformer();
        String promptText = "id of Cricket is 1 and then Polo is at 5, Footbal is at 9";
        Map map = (Map) tra.transformIntoPojo(promptText, Map.class.getName(),"","");
        Assertions.assertEquals(map.keySet().size(),3);
        Assertions.assertEquals(map.get("1"),"Cricket");
    }

    @Test
     void testPojoWithMapInsideClass() throws AIProcessingException, IOException {
        OpenAIPromptTransformer tra = new OpenAIPromptTransformer();
        String promptText = "Create a dictionary with name Hindi Kosh, add words big=large thing , small=tiny thing in it";
        Dictionary dict = (Dictionary) tra.transformIntoPojo(promptText, Dictionary.class.getName(),"","");
        Assertions.assertEquals(dict.getWordMeanings().keySet().size(),2);
        Assertions.assertEquals(dict.getWordMeanings().get("small"),"tiny thing");
    }

    @Test
     void testPojoWithSeveralObjects() throws AIProcessingException, IOException {
        OpenAIPromptTransformer tra = new OpenAIPromptTransformer();
        String promptText = "I have dentist appointment on 3rd July, then i have Gym appointment on 7th August and I am meeting famous Bollywood actor Shahrukh Khan on 19 Sep. My friends Rahul, Dhawal, Aravind are coming with me. My employee Jhonny Napper is comign with me he joined on Indian Independce day.My customer name is Amitabh Bacchan he wants to learn acting form me he joined on labor day";
        MyDiary dict = (MyDiary) tra.transformIntoPojo(promptText, MyDiary.class.getName(),"","");
        log.info(dict.toString());
    }

    /**
     * {
     *     "methodName": "buildMyDiary",
     *     "parameters": [{
     *         "name": "diary",
     *         "fields": [
     *             {
     *                 "fieldName": "allTheDatesOfAppointment",
     *                 "dateFormat": "ddMMyyyy",
     *                 "isArray": true,
     *                 "className": "java.util.Date",
     *                 "type": "Date[]",
     *                 "fieldValue": ["03072021", "07082021", "19092021"],
     *                 "prompt": "If you find more than 1 Date add it as another object inside fields array ",
     *                 "fieldType": "Date[]"
     *             },
     *             {
     *                 "fieldName": "friendsNames",
     *                 "isArray": true,
     *                 "className": "java.lang.String",
     *                 "type": "String[]",
     *                 "fieldValue": ["Rahul", "Dhawal", "Aravind"],
     *                 "prompt": "If you find more than 1 String add it as another object inside fields array ",
     *                 "fieldType": "String[]"
     *             },
     *             {
     *                 "fieldName": "customer",
     *                 "fields": [
     *                     {
     *                         "fieldName": "firstName",
     *                         "fieldType": "String",
     *                         "fieldValue": "Amitabh"
     *                     },
     *                     {
     *                         "fieldName": "lastName",
     *                         "fieldType": "String",
     *                         "fieldValue": "Bacchan"
     *                     },
     *                     {
     *                         "fieldName": "reasonForCalling",
     *                         "fieldDescription": "convert this to Hindi",
     *                         "fieldType": "String",
     *                         "fieldValue": "मुझे एक्टिंग सीखनी है"
     *                     },
     *                     {
     *                         "fieldName": "dateJoined",
     *                         "dateFormat": "yyyy-MM-dd",
     *                         "fieldDescription": "if you dont find date provide todays date in fieldValue",
     *                         "fieldType": "Date",
     *                         "fieldValue": "2021-09-06"
     *                     }
     *                 ],
     *                 "fieldType": "com.t4a.examples.actions.Customer"
     *             },
     *             {
     *                 "fieldName": "employee",
     *                 "fields": [
     *                     {
     *                         "fieldName": "name",
     *                         "fieldType": "String",
     *                         "fieldValue": "Jhonny Napper"
     *                     },
     *                     {
     *                         "fieldName": "department",
     *                         "fieldType": "String",
     *                         "fieldValue": "Unknown"
     *                     },
     *                     {
     *                         "fieldName": "salary",
     *                         "fieldType": "double",
     *                         "fieldValue": 0.0
     *                     },
     *                     {
     *                         "fieldName": "location",
     *                         "fieldType": "String",
     *                         "fieldValue": "Unknown"
     *                     },
     *                     {
     *                         "fieldName": "dateJoined",
     *                         "dateFormat": "ddMMyyyy",
     *                         "fieldDescription": "convert to actual date",
     *                         "fieldType": "Date",
     *                         "fieldValue": "15082021"
     *                     },
     *                     {
     *                         "fieldName": "tasks",
     *                         "isArray": true,
     *                         "className": "java.lang.String",
     *                         "type": "String[]",
     *                         "fieldValue": ["Unknown"],
     *                         "prompt": "If you find more than 1 String add it as another object inside fields array ",
     *                         "fieldType": "String[]"
     *                     }
     *                 ],
     *                 "fieldType": "com.t4a.examples.pojo.Employee"
     *             }
     *         ],
     *         "type": "com.t4a.examples.pojo.MyDiary"
     *     }],
     *     "returnType": "MyDiary"
     * }
     * @throws AIProcessingException
     * @throws IOException
     */
    @Test
     void testActionWithSeveralObjects() throws AIProcessingException, IOException {
        OpenAiActionProcessor tra = new OpenAiActionProcessor();
        String promptText = "I have dentist appointment on 3rd July, then i have Gym appointment on 7th August and I am meeting famous Bollywood actor Shahrukh Khan on 19 Sep. My friends Rahul, Dhawal, Aravind are coming with me. My employee Jhonny Napper is comign with me he joined on Indian Independce day.My customer name is Amitabh Bacchan he wants to learn acting form me he joined on labor day";
        MyDiaryAction action = new MyDiaryAction();
        MyDiary dict = (MyDiary) tra.processSingleAction(promptText,action,"buildMyDiary");
        log.info(dict.toString());
    }
    @Test
     void testPojoWithMapAndArrayInsideClass() throws AIProcessingException, IOException {
        OpenAIPromptTransformer tra = new OpenAIPromptTransformer();
        String promptText = "Create a dictionary with name Hindi Kosh, add words big=large thing , small=tiny thing in it, publish this dictionary in toronto and bangalore";
        Dictionary dict = (Dictionary) tra.transformIntoPojo(promptText, Dictionary.class.getName(),"","");
        Assertions.assertEquals(dict.getWordMeanings().keySet().size(),2);
        Assertions.assertEquals(dict.getWordMeanings().get("small"),"tiny thing");
    }

    @Test
     void testPojoArray() throws AIProcessingException, IOException {
        OpenAIPromptTransformer tra = new OpenAIPromptTransformer();
        String promptText = "Create a dictionary with name Hindi Kosh, add words big=large thing , small=tiny thing in it, publish this dictionary in toronto and bangalore. I just need list of locations fomr here";
        List dict = (List) tra.transformIntoPojo(promptText, List.class.getName(),"","");
        Assertions.assertTrue(dict.size()==2);
        Assertions.assertTrue(dict.get(0).equals("Toronto"));
    }
    @Test
     void testActionWithArray() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String promptText = "Vishal Mysore is customer of MovieHits inc and he styas in Paris the reason he is not happy is that he did nto like the movie date is labor day. Deepak Rao is another customer for MovieHits inc date is independence day";
        ArrayAction action = new ArrayAction();
        Customer[] org = (Customer[]) processor.processSingleAction(promptText,action,"addCustomers");
        Assertions.assertTrue(org.length == 2);


    }

    @Test
     void testActionWithArrayOfObject() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String promptText = "Vishal Mysore is going to restaurant on 2nd August, he is going to gym on 3rd septembr and then he is Celebrating Diwali on 11 Nov";
        ArrayOfObjectAction action = new ArrayOfObjectAction();
        String[] org = (String[]) processor.processSingleAction(promptText,new GenericJavaMethodAction(action));
        Assertions.assertTrue(org.length == 3);


    }
    @Test
     void testPojoWithList() throws AIProcessingException, IOException {
        OpenAIPromptTransformer tra = new OpenAIPromptTransformer();
        String promptText = "Shahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay. Hrithik roshan is another employee of same company based in Chennai his taks are jumping and Gym he joined on Indian Independce Day";
        Organization org = (Organization) tra.transformIntoPojo(promptText, Organization.class.getName(),"","");
        Assertions.assertTrue(org.getEm().get(0).getName().contains("Shahrukh"));
        Assertions.assertTrue(org.getEm().get(1).getName().contains("Hrithik"));
    }



    @Test
     void testHighRiskAction() throws AIProcessingException, IOException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String ecomActionPrmt = "Hey This is Vishal, the ecommerce Server is very slow and users are not able to do online shopping";
        String result = (String)processor.processSingleAction(ecomActionPrmt);
        log.info(result);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("false".equalsIgnoreCase(success));
        result = (String)processor.processSingleAction(ecomActionPrmt,"restartTheECOMServer");
        log.info(result);
        Assertions.assertNotNull(result);
        success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("true".equalsIgnoreCase(success));
    }
}
