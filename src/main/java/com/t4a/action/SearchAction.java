package com.t4a.action;

import com.t4a.api.AIAction;
import com.t4a.api.ActionType;
import com.t4a.predict.Predict;
import com.t4a.predict.PredictionLoader;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.extern.java.Log;

/**
 * Uses Serper API to search the web for real time information. Can we used for Hallucination detection as well
 * You need to set serperKey in tools4ai.properties or as System property
 */
@Log
@Predict
public class SearchAction implements AIAction {
    @Override
    public ActionType getActionType() {
        return ActionType.JAVAMETHOD;
    }

    public String googleSearch(String searchString, boolean isNews)  {
        log.info(searchString+" : "+isNews);
        HttpResponse<String> response = Unirest.post("https://google.serper.dev/search")
                .header("X-API-KEY", PredictionLoader.getInstance().getSerperKey())
                .header("Content-Type", "application/json")
                .body("{\"q\":\""+searchString+"\"}")
                .asString();
        String resStr = response.getBody().toString();
        return resStr;
    }

    @Override
    public String getDescription() {
        return "search the web";
    }

    @Override
    public String getActionName() {
        return "googleSearch";
    }
}
