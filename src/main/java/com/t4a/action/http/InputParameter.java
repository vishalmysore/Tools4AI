package com.t4a.action.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class InputParameter {
    private String name;
    private String type;
    private String description;
    private String defaultValue;

    public InputParameter(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public boolean hasDefaultValue() {
        return defaultValue!= null;
    }

    @Override
    public String toString() {
        return "InputParameter{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}