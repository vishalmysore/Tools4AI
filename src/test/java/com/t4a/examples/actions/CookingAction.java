package com.t4a.examples.actions;

import com.t4a.annotations.Predict;
import com.t4a.annotations.Prompt;
import com.t4a.api.JavaMethodAction;

@Predict(actionName = "cookThisForLunch", description = "This will be used for cooking dishes", groupName = "cooking")
public class CookingAction implements JavaMethodAction {

    public String cookThisForLunch(@Prompt(describe = "this should be comma separated") String ingredients) {
        return ingredients+" can be used to make spicy stuffed paratha";
    }

}
