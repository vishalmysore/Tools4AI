package com.external;

import com.t4a.bridge.AIAction;
import com.t4a.bridge.ActionType;

public class BlankAction implements AIAction {
    public String askAdditionalQuestion(String additionalQuestion){
        return "provide answer for this query : "+additionalQuestion;
    }

    @Override
    public String getActionName() {
        return "askAdditionalQuestion";
    }

    @Override
    public ActionType getActionType() {
        return ActionType.JAVAMETHOD;
    }

    @Override
    public String getDescription() {
        return "ask remaining question";
    }
}
