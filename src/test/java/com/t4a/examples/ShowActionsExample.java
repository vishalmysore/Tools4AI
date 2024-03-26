package com.t4a.examples;

import com.t4a.predict.PredictionLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShowActionsExample {

    public ShowActionsExample() throws Exception {

    }
    public static void main(String[] args) throws Exception {

        ShowActionsExample sample = new ShowActionsExample();
        sample.showActionList();

    }

    private void showActionList() {
        log.debug(PredictionLoader.getInstance().getActionNameList().toString());
    }
}
