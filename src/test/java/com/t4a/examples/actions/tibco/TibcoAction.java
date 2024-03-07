package com.t4a.examples.actions.tibco;

import com.t4a.api.AIAction;
import com.t4a.api.ActionType;
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
