package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.annotations.Prompt;

import java.util.Date;

@Agent(groupName = "customer support", groupDescription = "actions related to customer support")
public class CustomerWithQueryAction  {

    @Action(description = "Customer has problem create ticket for him")
    public String computerRepairWithDetails(Customer customer, @Prompt(dateFormat = "yyyy-MM-dd") Date dateOfComp , @Prompt(describe = "this is customer complaint") String query) {
        return customer.toString();
    }
}