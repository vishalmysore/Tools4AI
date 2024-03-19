package com.t4a.examples.actions.tibco;

import com.t4a.api.JavaMethodAction;
import com.t4a.predict.Predict;

@Predict(actionName = "sendMessageToQueue",description = "Inject message to Tibco")
public class TibcoAction implements JavaMethodAction {

    public Object sendMessageToQueue() {
        return null;

    }

}
