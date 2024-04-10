package com.t4a.examples.actions;

import com.t4a.annotations.Predict;
import com.t4a.api.JavaMethodAction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Predict(actionName = "allTheDates",description = "all the customers")
public class ArrayOfObjectAction implements JavaMethodAction {
    public String[] allTheDates(String[] allTheDates) {


        return allTheDates;
    }
}
