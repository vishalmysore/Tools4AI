package com.enterprise.fly;

import java.util.Date;
import java.util.List;

class FlightBookingRequest {
    private final Date onwardDate;
    private final Date returnDate;
    private final List<String> passengerNames;
    private final String fromDestination;
    private final String toDestination;

    public FlightBookingRequest(Date onwardDate, Date returnDate, List<String> passengerNames, String fromDestination, String toDestination) {
        this.onwardDate = onwardDate;
        this.returnDate = returnDate;
        this.passengerNames = passengerNames;
        this.fromDestination = fromDestination;
        this.toDestination = toDestination;
    }

    public Date getOnwardDate() {
        return onwardDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public List<String> getPassengerNames() {
        return passengerNames;
    }

    public String getFromDestination() {
        return fromDestination;
    }

    public String getToDestination() {
        return toDestination;
    }
}