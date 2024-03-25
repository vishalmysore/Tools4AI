package com.t4a.examples.basic;

import com.t4a.api.JavaMethodAction;
import lombok.extern.java.Log;

@Log
public class NonPredictionAction implements JavaMethodAction {
    @Override
    public String getActionName() {
        return "checkIfFoodisGoodForYou";
    }

    @Override
    public String getDescription() {
        return "check if the food is good for you";
    }

    public String checkIfFoodisGoodForYou(String foodName) {
        log.info(foodName);
        return foodName + "is not good for you";
    }
}

