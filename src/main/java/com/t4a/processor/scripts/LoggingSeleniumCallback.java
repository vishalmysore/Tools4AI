package com.t4a.processor.scripts;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

@Slf4j
public class LoggingSeleniumCallback implements SeleniumCallback{
    @Override
    public boolean beforeWebAction(String lineToBeProcessed,WebDriver driver) {
        log.info("Before Web Action: " + driver.getCurrentUrl());
        return true; // Return true to continue processing the line
    }

    @Override
    public void afterWebAction(String lineProcssed,WebDriver driver) {
        log.info("After Web Action: " + driver.getCurrentUrl());
    }

    @Override
    public String handleError(String line, String errorMessage, WebDriver driver, int retryCount) {
        log.info("Error during web action: " + errorMessage);
        return null;
    }


}
