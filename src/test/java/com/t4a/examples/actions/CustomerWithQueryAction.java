package com.t4a.examples.actions;

import com.t4a.api.JavaMethodAction;
import com.t4a.predict.Predict;
import com.t4a.predict.Prompt;

import java.util.Date;

@Predict(actionName = "computerRepairWithDetails", description = "Customer has problem create ticket for him", groupName = "customer support", groupDescription = "actions related to customer support")
public class CustomerWithQueryAction implements JavaMethodAction {

    public String computerRepairWithDetails(Customer customer, @Prompt(dateFormat = "yyyy-MM-dd") Date dateOfComp , @Prompt(describe = "this is customer complaint") String query) {
        return customer.toString();
    }
}