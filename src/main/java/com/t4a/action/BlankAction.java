package com.t4a.action;

import com.t4a.api.AIAction;
import com.t4a.api.ActionType;


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
