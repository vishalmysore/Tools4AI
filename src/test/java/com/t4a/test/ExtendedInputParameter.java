package com.t4a.test;



public class ExtendedInputParameter {
    private String name;
    private Object value;

    public ExtendedInputParameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
