package com.t4a.examples;


import com.t4a.annotations.Action;
import com.t4a.annotations.Predict;
import com.t4a.examples.actions.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Predict
public class ArrayAction  {


    @Action(description = "Add all the customers")
    public Customer[] addCustomers(Customer[] allCustomers) {


        return allCustomers;
    }
}
