package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.annotations.Prompt;

@Agent(groupName = "food" ,groupDescription = "all the tasks related to cooking")
public class CookingAction  {

    @Action(description = "This will be used for cooking dishes")
    public String cookThisForLunch(@Prompt(describe = "this should be comma separated") String ingredients) {
        return ingredients+" can be used to make spicy stuffed paratha";
    }

}
