package com.t4a.examples;


import com.t4a.annotations.Predict;
import com.t4a.api.JavaMethodAction;
import com.t4a.examples.actions.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Predict(actionName = "addCustomers",description = "all the customers")
public class ArrayAction implements JavaMethodAction {


    public Customer[] addCustomers(Customer[] allCustomers) {


        return allCustomers;
    }
}
