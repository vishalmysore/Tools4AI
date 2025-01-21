package com.t4a.examples.actions.db;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;

/**
 * This is an action class for MongoDB it takes a prompt and inject the values into MongoDatabase
 *
 */
@Agent(groupName = "MongoDB", groupDescription = "MongoDB related actions")
public class MongoAction  {

    @Action(description = "Inserts a customer complaint into the database")
    public Object insertCustomerComplaint(String customerName, String complaint) {

        return null;
    }

}
