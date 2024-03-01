package com.enterprise.basic;

import java.util.Objects;


public class RestaurantPojo {
    String name ;
    int numberOfPeople;
    String restaurantName;
    boolean cancel;
    String reserveDate;

    public RestaurantPojo() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void setReserveDate(String reserveDate) {
        this.reserveDate = reserveDate;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public boolean isCancel() {
        return cancel;
    }

    public String getReserveDate() {
        return reserveDate;
    }

    @Override
    public String toString() {
        return "RestaurantPojo{" +
                "name='" + name + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", restaurantName='" + restaurantName + '\'' +
                ", cancel=" + cancel +
                ", reserveDate='" + reserveDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantPojo that = (RestaurantPojo) o;
        return numberOfPeople == that.numberOfPeople && cancel == that.cancel && Objects.equals(name, that.name) && Objects.equals(restaurantName, that.restaurantName) && Objects.equals(reserveDate, that.reserveDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, numberOfPeople, restaurantName, cancel, reserveDate);
    }
}
