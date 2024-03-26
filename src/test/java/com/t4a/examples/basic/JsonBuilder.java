package com.t4a.examples.basic;

import com.t4a.predict.PromptTransformer;
import com.t4a.processor.AIProcessingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonBuilder {
    public static void main(String[] args) throws AIProcessingException {
        PromptTransformer builder = new PromptTransformer();
        String jsonString = "{\"name\":\"String\",\"age\":\"number\",\"address\":{\"street\":\"String\",\"city\":\"String\",\"zip\":\"int\"},\"contacts\":[{\"type\":\"string\",\"value\":\"String\"},{\"type\":\"string\",\"value\":\"string\"}]}";
        String prompt = "Can you make sure you add this info about my friend John Doe, aged 30, lives at 123 Main St in New York, zip code 10001. He can be reached via email at john@example.com or by phone at 555-1234.";
         jsonString = builder.transformIntoJson(jsonString,prompt,"MyFriend","get friend details");
         log.debug(jsonString);
    }
}
