package com.t4a.regression;

import com.t4a.examples.actions.ListAction;
import com.t4a.examples.actions.MapAction;
import com.t4a.examples.pojo.Dictionary;
import com.t4a.examples.pojo.Organization;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import com.t4a.processor.GeminiV2ActionProcessor;
import com.t4a.transform.GeminiV2PromptTransformer;
import com.t4a.transform.PromptTransformer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

@Slf4j
 class GeminiV2ActionValidation {
    @Test
     void testHttpAction() throws AIProcessingException, IOException {
        GeminiV2ActionProcessor processor = new GeminiV2ActionProcessor();
        String postABook = "post a book harry poster with id 189 the publish date is 2024-03-22 and the description is about harry who likes poster its around 500 pages  ";
        String result = (String)processor.processSingleAction(postABook);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));

    }
    @Test
     void testPredictionCustomer() throws AIProcessingException {
        GeminiV2ActionProcessor processor = new GeminiV2ActionProcessor();
        String result = (String)processor.processSingleAction(" Customer name is Vishal Mysore, his computer needs repair and he is in Toronto");
        Assertions.assertNotNull(result);
    }

    @Test
     void testShellAction() throws AIProcessingException, IOException {
        GeminiV2ActionProcessor processor = new GeminiV2ActionProcessor();
        String shellAction = "An Employee joined the organization, his name is Vishal and his location is Toronto, save this information ";
        String result = (String)processor.processSingleAction(shellAction);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }

    @Test
     void testJavaMethod() throws AIProcessingException, IOException {
        GeminiV2ActionProcessor processor = new GeminiV2ActionProcessor();
        String weatherAction = "hey I am in Toronto do you think i can go out without jacket";
        Double result = (Double) processor.processSingleAction(weatherAction);
        String answer = processor.query(weatherAction,"temprature -"+result);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+answer+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }

    @Test
     void testActionWithList() throws AIProcessingException, IOException {
        GeminiV2ActionProcessor processor = new GeminiV2ActionProcessor();
        String promptText = "Shahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay.Hrithik roshan is another employee of same company based in Chennai his taks are jumping and Gym he joined on Indian Independce Day";
        ListAction action = new ListAction();
        Organization org = (Organization) processor.processSingleAction(promptText,action,"addOrganization");
        Assertions.assertTrue(org.getEm().get(0).getName().contains("Shahrukh"));
        Assertions.assertTrue(org.getEm().get(1).getName().contains("Hrithik"));
        Assertions.assertEquals(2,org.getEm().size());


    }


    @Test
     void testActionWithListAndArray() throws AIProcessingException, IOException {
        AIProcessor processor = new GeminiV2ActionProcessor();
        String promptText = "Shahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay.Hrithik roshan is another employee of MovieHits inc based in Chennai his taks are jumping and Gym he joined on Indian Independce Day.Vishal Mysore is customer of MovieHits inc and he styas in Paris the reason he is not happy is that he did nto like the movie date is labor day. Deepak Rao is another customer for MovieHits inc date is independence day";
        ListAction action = new ListAction();
        Organization org = (Organization) processor.processSingleAction(promptText,action,"addOrganization");
        Assertions.assertTrue(org.getEm().get(0).getName().contains("Shahrukh"));
        Assertions.assertTrue(org.getEm().get(1).getName().contains("Hrithik"));
    }

    @Test
     void testActionWithMap() throws AIProcessingException, IOException {
        AIProcessor processor = new GeminiV2ActionProcessor();
        String promptText = "id of Cricket is 1 and then Polo is at 5, Footbal is at 9";
        MapAction action = new MapAction();
        Map<Integer,String> map = (Map<Integer,String>) processor.processSingleAction(promptText,action,"addSports");
        Assertions.assertEquals(map.keySet().size(),3);
        Assertions.assertEquals(map.get("1"),"Cricket");

    }

    @Test
     void testPojoWithMap() throws AIProcessingException, IOException {
        PromptTransformer tra = new GeminiV2PromptTransformer();
        String promptText = "id of Cricket is 1 and then Polo is at 5, Footbal is at 9";
        Map map = (Map) tra.transformIntoPojo(promptText, Map.class.getName(),"","");
        Assertions.assertEquals(map.keySet().size(),3);
        Assertions.assertEquals(map.get("1"),"Cricket");
    }

    @Test
     void testPojoWithMapInsideClass() throws AIProcessingException, IOException {
        PromptTransformer tra = new GeminiV2PromptTransformer();
        String promptText = "Create a dictionary with name Hindi Kosh, add words big=large thing , small=tiny thing in it";
        Dictionary dict = (Dictionary) tra.transformIntoPojo(promptText, Dictionary.class.getName(),"","");
        Assertions.assertEquals(dict.getWordMeanings().keySet().size(),2);
        Assertions.assertEquals(dict.getWordMeanings().get("small"),"tiny thing");
    }
}
