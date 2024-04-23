package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Predict;
import lombok.Getter;
import lombok.Setter;

@Predict
@Getter
@Setter
public class ArrayOfObjectAction  {
    @Action(description = "All the customers")
    public String[] allTheDates(String[] allTheDates) {


        return allTheDates;
    }
}
