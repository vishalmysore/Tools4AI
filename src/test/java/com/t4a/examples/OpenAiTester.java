package com.t4a.examples;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class OpenAiTester {
    public static void main(String[] args) {

        ChatLanguageModel model = OpenAiChatModel.withApiKey("demo");

        String joke = model.generate("here is your prompt - Can I go out without jacket in toronto today - what action should you take from the list of action - getTemperature,getName,postActivity - reply back with one action only");

        System.out.println(joke);
        joke = model.generate("here is your prompt - Can I go out without jacket in toronto today - here is you action- getTemperature(cityName,province,country) - what parameter should you pass to this function. give comma separated values only and nothing else");
        System.out.println(joke);

        joke = model.generate("here is your prompt - my friend Vinod is visiting me from Balaghat , I want to take him out to for food , not sure what he will eat since I am south Indian based in Bangalore - what action should you take from the list of action - getTemperature,getName,postActivity,findRestaurant,reserveRestaurant,cookFood - reply back with one action only");

        System.out.println(joke);

        joke = model.generate("here is your prompt -my friend Vinod is visiting me from Balaghat , I want to take him out to for food , not sure what he will eat since I am south Indian based in Bangalore  - here is you action- findRestaurant(numOfPeople,city,country) - what parameter should you pass to this function. give comma separated name=values only and nothing else");
        System.out.println(joke);
    }
}
