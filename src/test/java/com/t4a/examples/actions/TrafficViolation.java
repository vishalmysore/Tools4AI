package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;

@Agent
public class TrafficViolation  {
    @Action(description = "Traffic violation detected")
    public String trafficViolation(String typeOfViolation, String carColor) {
        System.out.println("car color "+carColor);
        return typeOfViolation+" has been detected there will penalty";
    }
}
