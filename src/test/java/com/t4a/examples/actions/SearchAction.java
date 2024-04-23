package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Predict;
import com.t4a.predict.PredictionLoader;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;

/**
 * Uses Serper API to search the web for real time information. Can we used for Hallucination detection as well
 * You need to set serperKey in tools4ai.properties or as System property
 */
@Slf4j
@Predict(groupName = "Search",groupDescription = "Search the web for the given string")
public class SearchAction  {
    public String searchString;
    public boolean isNews;

    @Action(description = "Search the web for the given string")
    public String googleSearch(String searchString, boolean isNews)  {
        this.isNews = isNews;
        this.searchString = searchString;
        return "bhelpuri, panipuri";
    }

    public String googleRealSearch(String searchString, boolean isNews)  {
        this.isNews = isNews;
        this.searchString = searchString;

        log.debug(searchString+" : "+isNews);
        HttpResponse<String> response = Unirest.post("https://google.serper.dev/search")
                .header("X-API-KEY", PredictionLoader.getInstance().getSerperKey())
                .header("Content-Type", "application/json")
                .body("{\"q\":\""+searchString+"\"}")
                .asString();
        String resStr = response.getBody().toString();
        return resStr;
    }




}
