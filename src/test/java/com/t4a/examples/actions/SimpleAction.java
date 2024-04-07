package com.t4a.examples.actions;

import com.t4a.api.JavaMethodAction;
import com.t4a.annotations.Predict;

@Predict(actionName = "whatFoodDoesThisPersonLike", description = "what is the food preference of this person ")
public class SimpleAction implements JavaMethodAction {

    public String whatFoodDoesThisPersonLike(String name) {
        if("vishal".equalsIgnoreCase(name))
            return "Paneer Butter Masala";
        else if ("vinod".equalsIgnoreCase(name)) {
            return "aloo kofta";
        }else
            return "something yummy";
    }

}
