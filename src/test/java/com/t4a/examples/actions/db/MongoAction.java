package com.t4a.examples.actions.db;

import com.t4a.api.AIAction;
import com.t4a.api.ActionType;
import com.t4a.predict.Predict;

/**
 * This is an action class for MongoDB it takes a prompt and inject the values into MongoDatabase
 *
 */
@Predict
public class MongoAction implements AIAction {

    public Object insertCustomerComplaint(String customerName, String complaint) {

        return null;
    }
    @Override
    public String getActionName() {
        return "insertCustomerComplaint";
    }
    @Override
    public ActionType getActionType() {
        return ActionType.JAVAMETHOD;
    }

    @Override
    public String getDescription() {
        return "insert messsage to Mongo";
    }
}
