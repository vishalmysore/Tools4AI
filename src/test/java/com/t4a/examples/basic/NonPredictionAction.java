package com.t4a.examples.basic;

import com.t4a.annotations.Action;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NonPredictionAction  {


    @Action(description = "provide the food name here to check if it is good ")
    public String checkIfFoodisGoodForYou(String foodName) {
        log.debug(foodName);
        return foodName + " has too much sugar";
    }


}

