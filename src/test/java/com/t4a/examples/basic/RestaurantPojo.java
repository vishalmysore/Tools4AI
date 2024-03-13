package com.t4a.examples.basic;

public class RestaurantPojo {
    String name;
    int numberOfPeople;

    RestaurantDetails restaurantDetails;
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
                ", restaurantDetails=" + restaurantDetails +
                ", cancel=" + cancel +
                ", reserveDate='" + reserveDate + '\'' +
                '}';
    }
}