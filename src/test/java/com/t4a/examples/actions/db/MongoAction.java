package com.t4a.examples.actions.db;

import com.t4a.api.JavaMethodAction;
import com.t4a.predict.Predict;

/**
 * This is an action class for MongoDB it takes a prompt and inject the values into MongoDatabase
 *
 */
@Predict(actionName = "insertCustomerComplaint",description = "insert messsage to Mongo")
public class MongoAction implements JavaMethodAction {

    public Object insertCustomerComplaint(String customerName, String complaint) {

        return null;
    }

}
