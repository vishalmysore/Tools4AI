package com.t4a.action;

import com.t4a.api.AIAction;
import com.t4a.api.ActionType;

/**
 * This Action can be used when you want the AI to get back with answer and if you do not have any
 * specific action to be performed. For example
 * "Hey can you provide weather for tornto and get back with places to see?"
 *
 * In this case the first action is WeatherAction with method called getTemperature and the second action
 * could be blank action as you want it to answer the question.
 *
 */

public final class BlankAction implements AIAction {
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
