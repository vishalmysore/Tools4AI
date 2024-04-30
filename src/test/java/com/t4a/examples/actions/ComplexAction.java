package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Predict;

@Predict(groupName = "customer support", groupDescription = "actions related to customer support")
public class ComplexAction  {

    public static int COUNTER = 0;
    public ComplexAction() {
        COUNTER++;
    }
    @Action(description = "Customer has problem create ticket for him")
    public String computerRepair(Customer customer) {
        return customer.toString();
    }
}