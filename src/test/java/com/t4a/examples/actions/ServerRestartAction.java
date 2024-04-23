package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Predict;
import com.t4a.api.ActionRisk;

@Predict(groupName = "server support", groupDescription = "actions related to server support")
public class ServerRestartAction  {
    @Action(riskLevel = ActionRisk.HIGH, description = "Restart the ECOM server")
    public String restartTheECOMServer(String reasonForRestart, String requestedBy) {
        return " Server has been restarted by "+requestedBy+" due to following reason "+reasonForRestart;
    }
}
