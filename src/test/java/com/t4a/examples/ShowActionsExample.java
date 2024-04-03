package com.t4a.examples;

import com.google.gson.Gson;
import com.t4a.api.GroupInfo;
import com.t4a.predict.PredictionLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ShowActionsExample {

    public ShowActionsExample() throws Exception {

    }
    public static void main(String[] args) throws Exception {

        ShowActionsExample sample = new ShowActionsExample();
        sample.showActionList();

        Map<GroupInfo,String> ga = PredictionLoader.getInstance().getActionGroupList().getGroupActions();
        ga.forEach((key, value) -> {
            System.out.println("Key: " + key + ", Value: " + value);
        });
        Gson gsr = new Gson();
        String groupInfo = gsr.toJson(PredictionLoader.getInstance().getActionGroupList().getGroupInfo());
        System.out.println(groupInfo);
    }

    private void showActionList() {
        log.debug(PredictionLoader.getInstance().getActionNameList().toString());
    }
}
