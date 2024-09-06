package com.t4a.processor.scripts;

import org.openqa.selenium.WebDriver;

public interface SeleniumCallback {
    void beforeWebAction(WebDriver driver);
    void afterWebAction(WebDriver driver);
}
