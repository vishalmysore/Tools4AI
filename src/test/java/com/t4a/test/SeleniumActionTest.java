package com.t4a.test;

import com.t4a.processor.selenium.DriverActions;
import com.t4a.processor.selenium.SeleniumAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumActionTest {
    private SeleniumAction seleniumAction;
    private DriverActions driverActions;

    @BeforeEach
    public void setUp() {
        seleniumAction = new SeleniumAction();
        driverActions = new DriverActions();
    }

    @Test
    public void testWebPageActionWithValidDriverActions() {
        driverActions.setTypeOfActionToTakeOnWebDriver("click");
        DriverActions actualDriverActions = seleniumAction.webPageAction(driverActions);
        assertEquals(driverActions.getTypeOfActionToTakeOnWebDriver(), actualDriverActions.getTypeOfActionToTakeOnWebDriver());
    }

    @Test
    public void testWebPageActionWithNullDriverActions() {
        DriverActions actualDriverActions = seleniumAction.webPageAction(null);
        assertNull(actualDriverActions);
    }

    @Test
    public void testWebPageActionWithDifferentActions() {
        // Test GET action
        DriverActions getAction = new DriverActions();
        getAction.setTypeOfActionToTakeOnWebDriver("GET");
        DriverActions getResult = seleniumAction.webPageAction(getAction);
        assertEquals("GET", getResult.getTypeOfActionToTakeOnWebDriver());

        // Test CLICK action
        DriverActions clickAction = new DriverActions();
        clickAction.setTypeOfActionToTakeOnWebDriver("CLICK");
        DriverActions clickResult = seleniumAction.webPageAction(clickAction);
        assertEquals("CLICK", clickResult.getTypeOfActionToTakeOnWebDriver());

        // Test NAVIGATE action
        DriverActions navigateAction = new DriverActions();
        navigateAction.setTypeOfActionToTakeOnWebDriver("NAVIGATE");
        DriverActions navigateResult = seleniumAction.webPageAction(navigateAction);
        assertEquals("NAVIGATE", navigateResult.getTypeOfActionToTakeOnWebDriver());
    }

    @Test
    public void testWebPageActionRetainsActionType() {
        driverActions.setTypeOfActionToTakeOnWebDriver("CLICK");
        DriverActions result = seleniumAction.webPageAction(driverActions);
        assertEquals("CLICK", result.getTypeOfActionToTakeOnWebDriver());
    }

    @Test
    public void testWebPageActionIndependentObjects() {
        driverActions.setTypeOfActionToTakeOnWebDriver("GET");
        DriverActions result1 = seleniumAction.webPageAction(driverActions);
        
        driverActions.setTypeOfActionToTakeOnWebDriver("CLICK");
        DriverActions result2 = seleniumAction.webPageAction(driverActions);

        assertEquals("GET", result1.getTypeOfActionToTakeOnWebDriver());
        assertEquals("CLICK", result2.getTypeOfActionToTakeOnWebDriver());
    }

    @Test
    public void testDriverActionsEquality() {
        DriverActions actions1 = new DriverActions();
        actions1.setTypeOfActionToTakeOnWebDriver("CLICK");

        DriverActions actions2 = new DriverActions();
        actions2.setTypeOfActionToTakeOnWebDriver("CLICK");

        assertEquals(actions1.getTypeOfActionToTakeOnWebDriver(), actions2.getTypeOfActionToTakeOnWebDriver());
        assertEquals(actions1.toString(), actions2.toString());
    }

    @Test
    public void testWebPageActionWithEmptyAction() {
        driverActions.setTypeOfActionToTakeOnWebDriver("");
        DriverActions result = seleniumAction.webPageAction(driverActions);
        assertEquals("", result.getTypeOfActionToTakeOnWebDriver());
    }
}
