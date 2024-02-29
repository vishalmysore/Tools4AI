package com.enterprise.db;

import com.t4a.bridge.AIAction;
import com.t4a.bridge.ActionType;

/**
 * This is an action class for MongoDB it takes a prompt and inject the values into MongoDatabase
 *
 */
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
