package com.t4a.test;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiV2ActionProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
public class GeminiV2ActionTest {
    @Test
    public void testHttpAction() throws AIProcessingException, IOException {
        GeminiV2ActionProcessor processor = new GeminiV2ActionProcessor();
        String postABook = "post a book harry poster with id 189 the publish date is 2024-03-22 and the description is about harry who likes poster its around 500 pages  ";
        String result = (String)processor.processSingleAction(postABook);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));

    }

    @Test
    public void testShellAction() throws AIProcessingException, IOException {
        GeminiV2ActionProcessor processor = new GeminiV2ActionProcessor();
        String shellAction = "An Employee joined the organization, his name is Vishal and his location is Toronto, save this information ";
        String result = (String)processor.processSingleAction(shellAction);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }

    @Test
    public void testJavaMethod() throws AIProcessingException, IOException {
        GeminiV2ActionProcessor processor = new GeminiV2ActionProcessor();
        String weatherAction = "hey I am in Toronto do you think i can go out without jacket";
        String result = (String)processor.processSingleAction(weatherAction);
        Assertions.assertNotNull(result);
        String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" - was it a success? - Reply in true or false only");
        log.debug(success);
        Assertions.assertTrue("True".equalsIgnoreCase(success));
    }
}
