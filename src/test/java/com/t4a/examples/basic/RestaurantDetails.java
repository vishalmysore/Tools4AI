package com.t4a.examples.basic;

import com.t4a.annotations.Prompt;

public class RestaurantDetails {
    String name;
    @Prompt(describe = "convert location to hindi")
    String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "RestaurantDetails{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
