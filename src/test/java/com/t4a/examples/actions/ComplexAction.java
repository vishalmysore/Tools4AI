package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Predict;

@Predict(groupName = "customer support", groupDescription = "actions related to customer support")
public class ComplexAction  {

    @Action(description = "Customer has problem create ticket for him")
    public String computerRepair(Customer customer) {
        return customer.toString();
    }
}