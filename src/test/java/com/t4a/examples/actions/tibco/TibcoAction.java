package com.t4a.examples.actions.tibco;

import com.t4a.annotations.Action;
import com.t4a.annotations.Predict;

@Predict(groupName = "Tibco", groupDescription = "Tibco related actions")
public class TibcoAction  {

    @Action(description = "Send a message to a Tibco queue")
    public Object sendMessageToQueue() {
        return null;

    }

}
