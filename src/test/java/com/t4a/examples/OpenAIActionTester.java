package com.t4a.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.t4a.examples.actions.SearchAction;
import com.t4a.examples.actions.SimpleAction;
import com.t4a.examples.actions.file.FileWriteAction;
import com.t4a.examples.basic.DateDeserializer;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.OpenAiActionProcessor;

import java.util.Date;

public class OpenAIActionTester  {
    public static void main(String[] args) throws AIProcessingException, JsonProcessingException {



        String prompt = "My friends name is Vishal ,I dont know what to cook for him today.";
        process(prompt, new SimpleAction(),"whatFoodDoesThisPersonLike") ;
        prompt = "Post a book with title Harry Poster and Problem rat, id of the book is 887 and discription is about harry ";
        process(prompt) ;
        prompt = "save these indian recipe names to a file eggcurry, paneer butter";
        process(prompt, new FileWriteAction(),"saveInformationToLocalFile") ;
        prompt = "Customer name is Vishal Mysore, his computer needs repair and he is in Toronto he complained on labor day";
        prompt ="insert vishal in the mongo database";
       // process(prompt) ;
        prompt = "sachin tendular is a cricket player and he has played 400 matches, his max score is 1000, he wants to go to " +
                "Maharaja restaurant in toronto with 4 of his friends on Indian Independence Day, can you notify him and the restarurant";
        process(prompt) ;
        prompt = "My employee name is Vishal he is toronto save this";
        process(prompt) ;
        prompt = "can you provide me list of core V1 component status for the kubernetes cluster";
        process(prompt) ;
        prompt = "search google for Indian Recipes";
        process(prompt,new SearchAction(),"googleSearch") ;
    }
    public static void process(String prompt) throws AIProcessingException {
    process(prompt,null,null);
    }
    public static void process(String prompt, Object action,String actionName) throws AIProcessingException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer("dd MMMM yyyy"));
        Gson gson = gsonBuilder.create();
        OpenAiActionProcessor processor = new OpenAiActionProcessor(gson);
        if(action != null) {
        String result = (String)processor.processSingleAction(prompt,action,actionName);
        System.out.println(result);}
        else {
            String result = (String)processor.processSingleAction(prompt);
            System.out.println(result);
        }
    }
}
