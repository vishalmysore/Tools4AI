package com.t4a.test;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public  class TestClass {
    private String name;
    private int age;
    private double salary;
    private boolean isActive;
    private List<String> hobbies;
    private Map<String, Integer> scores;
    private Map<String, Integer> testMap;
    // getters and setters
}

