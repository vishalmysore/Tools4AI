package com.udo;

import java.util.function.Function;

public class BookingHelper {
    public static String bookFlight(FlightDetails bookingRequest) {
        // Define the book function inside this method
        Function<FlightDetails, String> book = (request) -> {
            // Here you can implement the booking logic
            // For simplicity, we'll just return a success message
            return "Flight booked successfully!\n" +
                    "Onward Date: " + request.getDate() + "\n" +
                   // "Return Date: " + request.getReturnDate() + "\n" +
                    "From: " + request.getFromLocation() + "\n" +
                    "To: " + request.getToLocation() + "\n" +
                    "Passenger Names: " + String.join(", ", request.getName());
        };

        // Call the book function with the booking request
        return book.apply(bookingRequest);
    }
}