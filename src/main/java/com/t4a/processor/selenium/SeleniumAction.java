package com.t4a.processor.selenium;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
@Setter
@Getter
@Agent(groupName = "Selenium", groupDescription = "Selenium actions")
public class SeleniumAction  {

    @Action(description = "Perform action on web page")
    public DriverActions webPageAction(DriverActions webDriverActions) {
        return webDriverActions;
    }
}
