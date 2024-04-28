package com.t4a.test;

import com.t4a.processor.selenium.DriverActions;
import com.t4a.processor.selenium.SeleniumAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeleniumActionTest {
    private SeleniumAction seleniumAction;
    private DriverActions driverActions;

    @BeforeEach
    public void setUp() {
        seleniumAction = new SeleniumAction();
        driverActions = new DriverActions();
    }

    @Test
    public void testWebPageAction() {
        DriverActions expectedDriverActions = driverActions;
        DriverActions actualDriverActions = seleniumAction.webPageAction(driverActions);
        assertEquals(expectedDriverActions, actualDriverActions, "The returned DriverActions object should be the same as the one passed in");
    }
}
