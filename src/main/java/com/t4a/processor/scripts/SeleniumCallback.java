package com.t4a.processor.scripts;

import org.openqa.selenium.WebDriver;

public interface SeleniumCallback {
    boolean beforeWebAction(String lineToBeProcessed, WebDriver driver);
    void afterWebAction(String lineProcessed,WebDriver driver);

    void handleError(String line, String errorMessage, WebDriver driver);
}
