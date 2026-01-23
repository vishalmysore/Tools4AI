package com.t4a.detect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum ActionState {
    SUBMITTED("submitted"),
    WORKING("working"),
    INPUT_REQUIRED("input-required"),
    COMPLETED("completed"),
    CANCELED("canceled"),
    FAILED("failed"),
    UNKNOWN("unknown");

    private final String value;

    ActionState(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public static ActionState forValue(String value) {
        for (ActionState state : values()) {
            if (state.value.equals(value)) {
                return state;
            }
        }
        return UNKNOWN;
    }
}
