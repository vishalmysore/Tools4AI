package com.t4a.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.t4a.examples.basic.DateDeserializer;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.LoggingHumanDecision;
import com.t4a.processor.LogginggExplainDecision;
import com.t4a.processor.OpenAiActionProcessor;

import java.util.Date;

public class OpenAIActionTester  {
    public static void main(String[] args) throws AIProcessingException, JsonProcessingException {



        String prompt = "My friends name is Vishal ,I dont know what to cook for him today.";
      //  process(prompt) ;
        prompt = "Post a book with title Harry Poster and Problem rat, id of the book is 887 and discription is about harry ";
      //  process(prompt) ;
        prompt = "Customer name is Vishal Mysore, his computer needs repair and he is in Toronto";
        process(prompt) ;
    }

    public static void process(String prompt) throws AIProcessingException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer("dd MMMM yyyy"));
        Gson gson = gsonBuilder.create();
        OpenAiActionProcessor processor = new OpenAiActionProcessor(gson);
        String result = (String)processor.processSingleAction(prompt,new LoggingHumanDecision(),new LogginggExplainDecision());
        System.out.println(result);
    }
}
