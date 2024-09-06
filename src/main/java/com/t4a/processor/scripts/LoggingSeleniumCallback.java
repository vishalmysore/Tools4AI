package com.t4a.processor.scripts;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

@Slf4j
public class LoggingSeleniumCallback implements SeleniumCallback{
    @Override
    public void beforeWebAction(WebDriver driver) {
        log.info("Before Web Action: " + driver.getCurrentUrl());
    }

    @Override
    public void afterWebAction(WebDriver driver) {
        log.info("After Web Action: " + driver.getCurrentUrl());
    }
}
