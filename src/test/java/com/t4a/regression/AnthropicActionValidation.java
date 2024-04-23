package com.t4a.regression;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.t4a.examples.ArrayAction;
import com.t4a.examples.actions.*;
import com.t4a.examples.basic.DateDeserializer;
import com.t4a.examples.basic.RestaurantPojo;
import com.t4a.examples.pojo.Dictionary;
import com.t4a.examples.pojo.MyDiary;
import com.t4a.examples.pojo.Organization;
import com.t4a.transform.AnthropicTransformer;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AnthropicActionProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class AnthropicActionValidation {
    @Test
    void testBasicAction() throws AIProcessingException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String postABook = "post a book harry poster with id 189 the publish date is 2024-03-22 and the description is about harry who likes poster its around 500 pages  ";
        String result = (String)processor.processSingleAction(postABook);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }

    @Test
    public void testRestaurantPojo() throws AIProcessingException {
        String promptText = "can you book a dinner reseration in name of Vishal and his family of 4 at Maharaj restaurant in Toronto, on Indian Independence day and make sure its cancellable";
        AnthropicTransformer tools = new AnthropicTransformer();
        RestaurantPojo pojo = (RestaurantPojo) tools.transformIntoPojo(promptText, "com.t4a.examples.basic.RestaurantPojo", "RestaurantClass", "Create Pojo from the prompt");
        Assertions.assertTrue(pojo != null);
        Assertions.assertTrue(pojo.getName().contains("Vishal"));
        Assertions.assertEquals(pojo.getNumberOfPeople(), 4);
        Assertions.assertTrue(pojo.getRestaurantDetails().getName().contains("Maharaj"));
        Assertions.assertEquals(pojo.getRestaurantDetails().getLocation(), "Toronto");

    }

    @Test
    public void testCustomerPojoOpenAI() throws AIProcessingException, IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer("dd MMMM yyyy"));
        Gson gson = gsonBuilder.create();
        AnthropicTransformer tools2 = new AnthropicTransformer(gson);

        Customer pojo = (Customer) tools2.transformIntoPojo("I went to the part yesterday and met someone it was so good to meet an old friend. A customer is complaining that his computer is not working, his name is Vinod Gupta,  and he stays in Toronto he joined on 12 May 2008", Customer.class.getName(),"Customer", "get Customer details");
        Assertions.assertTrue(pojo != null);
        String reasonMatches = TestHelperOpenAI.getInstance().sendMessage("reply in true or false only - is this "+pojo.getReasonForCalling()+" same as computer not working");
        Assertions.assertTrue("True".equalsIgnoreCase(reasonMatches));
        Assertions.assertEquals(pojo.getFirstName(),"Vinod");
        Assertions.assertEquals(pojo.getLastName(),"Gupta");
    }

    @Test
    public void testComplexActionOpenAI() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String prm = "Sachin Tendulkar is a cricket player and he has played 400 matches, his max score is 1000, he wants to go to " +
                "Maharaja restaurant in toronto with 4 of his friends on Indian Independence Day, can you notify him and the restarurant";
        PlayerWithRestaurant playerAc = new PlayerWithRestaurant();
        String result = (String)processor.processSingleAction(prm,playerAc,"notifyPlayerAndRestaurant") ;
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(playerAc.getRestaurantPojo());
        Assertions.assertNotNull(playerAc.getPlayer());
        Assertions.assertEquals(playerAc.getPlayer().getFirstName(),"Sachin");
        Assertions.assertEquals(playerAc.getPlayer().getLastName(),"Tendulkar");
    }

    @Test
    public void testHttpActionOpenAI() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String postABook = "post a book harry poster with id 189 the publish date is 2024-03-22 and the description is about harry who likes poster its around 500 pages  ";
        String result = (String)processor.processSingleAction(postABook);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));

    }

    @Test
    public void testShellActionOpenAI() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String shellAction = "An Employee joined the organization, his name is Vishal and his location is Toronto, save this information ";
        String result = (String)processor.processSingleAction(shellAction);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }

    @Test
    public void testJavaMethod() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String weatherAction = "ey I am in Toronto do you think i can go out without jacket";
        String result = (String)processor.processSingleAction(weatherAction);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }
    @Test
    public void testJavaMethodForFile() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String weatherAction = "My friends name is Vishal ,he lives in toronto.I want save this info locally";
        String result = (String)processor.processSingleAction(weatherAction);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }
    @Test
    public void testActionWithList() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String promptText = "Shahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay.Hrithik roshan is another employee of same company based in Chennai his taks are jumping and Gym he joined on Indian Independce Day";
        ListAction action = new ListAction();
        Organization org = (Organization) processor.processSingleAction(promptText,action,"addOrganization");
        Assertions.assertTrue(org.getEm().get(0).getName().contains("Shahrukh"));
        Assertions.assertTrue(org.getEm().get(1).getName().contains("Hrithik"));
    }

    @Test
    public void testActionWithListAndArray() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String promptText = "Shahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay.Hrithik roshan is another employee of MovieHits inc based in Chennai his taks are jumping and Gym he joined on Indian Independce Day.Vishal Mysore is customer of MovieHits inc and he styas in Paris the reason he is not happy is that he did nto like the movie date is labor day. Deepak Rao is another customer for MovieHits inc date is independence day";
        ListAction action = new ListAction();
        Organization org = (Organization) processor.processSingleAction(promptText,action,"addOrganization");
        Assertions.assertTrue(org.getEm().get(0).getName().contains("Shahrukh"));
        Assertions.assertTrue(org.getEm().get(1).getName().contains("Hrithik"));
    }

    @Test
    public void testActionWithMap() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String promptText = "id of Cricket is 1 and then Polo is at 5, Footbal is at 9";
        MapAction action = new MapAction();
        Map<Integer,String> map = (Map<Integer,String>) processor.processSingleAction(promptText,action,"addSports");
        Assertions.assertEquals(map.keySet().size(),3);
        Assertions.assertEquals(map.get("1"),"Cricket");

    }

    @Test
    public void testPojoWithMap() throws AIProcessingException, IOException {
        AnthropicTransformer tra = new AnthropicTransformer();
        String promptText = "id of Cricket is 1 and then Polo is at 5, Footbal is at 9";
        Map map = (Map) tra.transformIntoPojo(promptText, Map.class.getName(),"","");
        Assertions.assertEquals(map.keySet().size(),3);
        Assertions.assertEquals(map.get("1"),"Cricket");
    }

    @Test
    public void testPojoWithMapInsideClass() throws AIProcessingException, IOException {
        AnthropicTransformer tra = new AnthropicTransformer();
        String promptText = "Create a dictionary with name Hindi Kosh, add words big=large thing , small=tiny thing in it";
        Dictionary dict = (Dictionary) tra.transformIntoPojo(promptText, Dictionary.class.getName(),"","");
        Assertions.assertEquals(dict.getWordMeanings().keySet().size(),2);
        Assertions.assertEquals(dict.getWordMeanings().get("small"),"tiny thing");
    }

    @Test
    public void testPojoWithSeveralObjects() throws AIProcessingException, IOException {
        AnthropicTransformer tra = new AnthropicTransformer();
        String promptText = "I have dentist appointment on 3rd July, then i have Gym appointment on 7th August and I am meeting famous Bollywood actor Shahrukh Khan on 19 Sep. My friends Rahul, Dhawal, Aravind are coming with me. My employee Jhonny Napper is comign with me he joined on Indian Independce day.My customer name is Amitabh Bacchan he wants to learn acting form me he joined on labor day";
        MyDiary dict = (MyDiary) tra.transformIntoPojo(promptText, MyDiary.class.getName(),"","");
        log.info(dict.toString());
    }

    @Test
    public void testActionWithSeveralObjects() throws AIProcessingException, IOException {
        AnthropicActionProcessor tra = new AnthropicActionProcessor();
        String promptText = "I have dentist appointment on 3rd July, then i have Gym appointment on 7th August and I am meeting famous Bollywood actor Shahrukh Khan on 19 Sep. My friends Rahul, Dhawal, Aravind are coming with me. My employee Jhonny Napper is comign with me he joined on Indian Independce day.My customer name is Amitabh Bacchan he wants to learn acting form me he joined on labor day";
        MyDiaryAction action = new MyDiaryAction();
        MyDiary dict = (MyDiary) tra.processSingleAction(promptText,action,"buildMyDiary");
        log.info(dict.toString());
    }
    @Test
    public void testPojoWithMapAndArrayInsideClass() throws AIProcessingException, IOException {
        AnthropicTransformer tra = new AnthropicTransformer();
        String promptText = "Create a dictionary with name Hindi Kosh, add words big=large thing , small=tiny thing in it, publish this dictionary in toronto and bangalore";
        Dictionary dict = (Dictionary) tra.transformIntoPojo(promptText, Dictionary.class.getName(),"","");
        Assertions.assertEquals(dict.getWordMeanings().keySet().size(),2);
        Assertions.assertEquals(dict.getWordMeanings().get("small"),"tiny thing");
    }

    @Test
    public void testPojoArray() throws AIProcessingException, IOException {
        AnthropicTransformer tra = new AnthropicTransformer();
        String promptText = "Create a dictionary with name Hindi Kosh, add words big=large thing , small=tiny thing in it, publish this dictionary in toronto and bangalore. I just need list of locations fomr here";
        List dict = (List) tra.transformIntoPojo(promptText, List.class.getName(),"","");
        Assertions.assertTrue(dict.size()==2);
        Assertions.assertTrue(dict.get(0).equals("Toronto"));
    }
    @Test
    public void testActionWithArray() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String promptText = "Vishal Mysore is customer of MovieHits inc and he styas in Paris the reason he is not happy is that he did nto like the movie date is labor day. Deepak Rao is another customer for MovieHits inc date is independence day";
        ArrayAction action = new ArrayAction();
        Customer[] org = (Customer[]) processor.processSingleAction(promptText,action,"addCustomers");
        Assertions.assertTrue(org.length == 2);


    }

    @Test
    public void testActionWithArrayOfObject() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
        String promptText = "Vishal Mysore is going to restaurant on 2nd August, he is going to gym on 3rd septembr and then he is Celebrating Diwali on 11 Nov";
        ArrayOfObjectAction action = new ArrayOfObjectAction();
        String[] org = (String[]) processor.processSingleAction(promptText,action,"allTheDates");
        Assertions.assertTrue(org.length == 3);


    }
    @Test
    public void testPojoWithList() throws AIProcessingException, IOException {
        AnthropicTransformer tra = new AnthropicTransformer();
        String promptText = "Shahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay. Hrithik roshan is another employee of same company based in Chennai his taks are jumping and Gym he joined on Indian Independce Day";
        Organization org = (Organization) tra.transformIntoPojo(promptText, Organization.class.getName(),"","");
        Assertions.assertTrue(org.getEm().get(0).getName().contains("Shahrukh"));
        Assertions.assertTrue(org.getEm().get(1).getName().contains("Hrithik"));
    }



    @Test
    public void testHighRiskAction() throws AIProcessingException, IOException {
        AnthropicActionProcessor processor = new AnthropicActionProcessor();
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
