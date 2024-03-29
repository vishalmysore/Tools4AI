package com.t4a.examples.enterprise.fly;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Class to test various prompts and see if the booking works
 */
@Slf4j
public class FlightCustomer {
    public static void main(String[] args) throws IOException {

        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";

        String promptText = "My name is vishal i need to fly from toronto to bangalore on 25th of june, what a great day it is";
        String status = AIFlightAssistant.bookFlight(projectId, location, modelName, promptText);
        log.debug(promptText+ " : "+status);

        try {
            promptText = "My name is vishal i need to fly from toronto to delhi on 25th of December, I saw the movie Sholay again , what a great actiing by amitabh bacchan";
            status = AIFlightAssistant.bookFlight(projectId, location, modelName, promptText);
            log.debug(promptText + " : " + status);
        }catch (Exception e) {
            log.debug(promptText +": "+e.getMessage());
        }
        try {
            promptText = "My name is vishal row mysore can i  fly from toronto to delhi on 25th of December";
            status = AIFlightAssistant.bookFlight(projectId, location, modelName, promptText);
            log.debug(promptText + " : " + status);
        }catch (Exception e) {
            log.debug(promptText +": "+e.getMessage());
        }
    }
}
