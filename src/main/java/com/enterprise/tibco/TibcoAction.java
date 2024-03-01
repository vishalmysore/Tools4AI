package com.enterprise.tibco;

import com.t4a.bridge.AIAction;
import com.t4a.bridge.ActionType;
import com.t4a.predict.Predict;

@Predict
public class TibcoAction implements AIAction {

    public Object sendMessageToQueue() {
        return null;

    }
    @Override
    public String getActionName() {
        return "sendMessageToQueue";
    }

    @Override
    public ActionType getActionType() {
        return ActionType.JAVAMETHOD;
    }

    @Override
    public String getDescription() {
        return "Inject message to Tibco";
    }
}
