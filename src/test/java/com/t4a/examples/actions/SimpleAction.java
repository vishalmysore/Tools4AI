package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;

@Agent
public class SimpleAction  {

    @Action( description = "what is the food preference of this person")
    public String whatFoodDoesThisPersonLike(String name) {
        if("vishal".equalsIgnoreCase(name))
            return "Paneer Butter Masala";
        else if ("vinod".equalsIgnoreCase(name)) {
            return "aloo kofta";
        }else
            return "something yummy";
    }

}
