package com.t4a.examples.actions;

import com.t4a.api.JavaMethodAction;
import com.t4a.annotations.Predict;

@Predict(actionName = "computerRepair", description = "Customer has problem create ticket for him", groupName = "customer support", groupDescription = "actions related to customer support")
public class ComplexAction implements JavaMethodAction {

    public String computerRepair(Customer customer) {
        return customer.toString();
    }
}