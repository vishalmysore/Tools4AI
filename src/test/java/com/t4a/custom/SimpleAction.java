package com.t4a.custom;


import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import lombok.extern.java.Log;

@Log
@Agent(groupName = "SimpleAction", groupDescription = "This is a simple action which can be used to test the action processor")
public class SimpleAction {

    /**
     * User can provide queries such was "Vishal likes what food?" or "What food does Vinod like?" or Vishal is coming home to eat today what should i cook
     * THe name will get mapped to the parameter 'name' and the action will return what food that person likes.
     * @param name
     * @return
     */
    @Action(description = "Provide persons name and then find out what does that person like")
    public String whatFoodDoesThisPersonLike(String name, Double quantity, Long hoursToEat, Boolean isVegetarian,Integer timeToCook) {
        log.info("Inside whatFoodDoesThisPersonLike action with name: " + name);
        if("vishal".equalsIgnoreCase(name))
            return "Paneer Butter Masala";
        else if ("vinod".equalsIgnoreCase(name)) {
            return "aloo kofta";
        }else
            return "something yummy";
    }

}
