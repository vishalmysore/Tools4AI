package com.t4a.examples;


import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.examples.actions.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Agent
public class ArrayAction  {


    @Action(description = "Add all the customers")
    public Customer[] addCustomers(Customer[] allCustomers) {


        return allCustomers;
    }
}
