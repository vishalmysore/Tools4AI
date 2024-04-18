package com.t4a.examples.actions;

import com.t4a.annotations.Predict;
import com.t4a.api.JavaMethodAction;


@Predict(actionName = "trafficViolation", description = "This action will be called in case of traffic violation", groupName = "traffic violation")
public class TrafficViolation implements JavaMethodAction {
    public String trafficViolation(String typeOfViolation, String carColor) {
        System.out.println("car color "+carColor);
        return typeOfViolation+" has been detected there will penalty";
    }
}
