package com.t4a.test;

import com.t4a.processor.selenium.DriverActions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DriverActionsTest {
    private DriverActions driverActions;

    @BeforeEach
    public void setUp() {
        driverActions = new DriverActions();
    }

    @Test
    public void testTypeOfActionToTakeOnWebDriver() {
        String expectedAction = "navigate";
        driverActions.setTypeOfActionToTakeOnWebDriver(expectedAction);
        String actualAction = driverActions.getTypeOfActionToTakeOnWebDriver();
        assertEquals(expectedAction, actualAction, "The action should be navigate");
    }
}