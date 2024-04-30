package com.t4a.regression;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.t4a.examples.actions.Customer;
import com.t4a.examples.actions.PlayerWithRestaurant;
import com.t4a.examples.basic.DateDeserializer;
import com.t4a.examples.basic.RestaurantPojo;
import com.t4a.deperecated.GeminiPromptTransformer;
import com.t4a.processor.AIProcessingException;
import com.t4a.deperecated.GeminiActionProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

@Slf4j
 class GeminiActionsValidation {

    @Test
     void testRestaurantPojo() throws AIProcessingException {
        String promptText = "can you book a dinner reseration in name of Vishal and his family of 4 at Maharaj restaurant in Toronto, on Indian Independence day and make sure its cancellable";
        GeminiPromptTransformer tools = new GeminiPromptTransformer();
        RestaurantPojo pojo = (RestaurantPojo) tools.transformIntoPojo(promptText, "com.t4a.examples.basic.RestaurantPojo", "RestaurantClass", "Create Pojo from the prompt");
        Assertions.assertNotNull(pojo) ;
        Assertions.assertEquals(pojo.getName(), "Vishal");
        Assertions.assertEquals(pojo.getNumberOfPeople(), 4);
        Assertions.assertTrue(pojo.getRestaurantDetails().getName().contains("Maharaj"));
        Assertions.assertEquals(pojo.getRestaurantDetails().getLocation(), "Toronto");

    }

     void testPredictionCustomer() throws AIProcessingException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer("dd MMMM yyyy"));
        Gson gson = gsonBuilder.create();
        GeminiActionProcessor processor = new GeminiActionProcessor(gson);
        String result = (String)processor.processSingleAction(" Customer name is Vishal Mysore, his computer needs repair and he is in Toronto");
        log.debug(result);
    }


    @Test
     void testCustomerPojo() throws AIProcessingException, IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer("dd MMMM yyyy"));
        Gson gson = gsonBuilder.create();
        GeminiPromptTransformer tools2 = new GeminiPromptTransformer(gson);

        Customer pojo = (Customer) tools2.transformIntoPojo("I went to the part yesterday and met someone it was so good to meet an old friend. A customer is complaining that his computer is not working, his name is Vinod Gupta,  and he stays in Toronto he joined on 12 May 2008", Customer.class.getName(),"Customer", "get Customer details");
        Assertions.assertNotNull(pojo );
        String reasonMatches = TestAIHelper.getInstance().sendMessage("reply in true or false only - is this "+pojo.getReasonForCalling()+" same as computer not working");
        Assertions.assertTrue("True".equalsIgnoreCase(reasonMatches));
        Assertions.assertEquals(pojo.getFirstName(),"Vinod");
        Assertions.assertEquals(pojo.getLastName(),"Gupta");
    }

    @Test
     void testComplexAction() throws AIProcessingException, IOException {
        GeminiActionProcessor processor = new GeminiActionProcessor();
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
     void testHttpAction() throws AIProcessingException, IOException {
        GeminiActionProcessor processor = new GeminiActionProcessor();
        String postABook = "post a book harry poster with id 189 the publish date is 2024-03-22 and the description is about harry who likes poster its around 500 pages  ";
        String result = (String)processor.processSingleAction(postABook);
        Assertions.assertNotNull(result);
        String success = TestAIHelper.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));

    }


    @Test
     void testShellAction() throws AIProcessingException, IOException {
        GeminiActionProcessor processor = new GeminiActionProcessor();
        String shellAction = "An Employee joined the organization, his name is Vishal and his location is Toronto, save this information ";
        String result = (String)processor.processSingleAction(shellAction);
        Assertions.assertNotNull(result);
        String success = TestAIHelper.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }

    @Test
     void testJavaMethod() throws AIProcessingException, IOException {
        GeminiActionProcessor processor = new GeminiActionProcessor();
        String weatherAction = "ey I am in Toronto do you think i can go out without jacket";
        String result = (String)processor.processSingleAction(weatherAction);
        Assertions.assertNotNull(result);
        String success = TestAIHelper.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }
    @Test
     void testJavaMethodForFile() throws AIProcessingException, IOException {
        GeminiActionProcessor processor = new GeminiActionProcessor();
        String weatherAction = "My friends name is Vishal ,he lives in toronto.I want save this info locally";
        String result = (String)processor.processSingleAction(weatherAction);
        Assertions.assertNotNull(result);
        String success = TestAIHelper.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }

    @Test
     void testHighRiskAction() throws AIProcessingException, IOException {
        GeminiActionProcessor processor = new GeminiActionProcessor();
        String ecomActionPrmt = "Hey This is Vishal, the ecommerce Server is very slow and users are not able to do online shopping";
        String result = (String)processor.processSingleAction(ecomActionPrmt);
        log.info(result);
        Assertions.assertNotNull(result);
        String success = TestAIHelper.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("false".equalsIgnoreCase(success));

        result = (String)processor.processSingleAction(ecomActionPrmt,"restartTheECOMServer");
        log.info(result);
        Assertions.assertNotNull(result);
        success = TestAIHelper.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("true".equalsIgnoreCase(success));
    }
}
