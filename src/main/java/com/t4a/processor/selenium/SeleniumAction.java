package com.t4a.processor.selenium;

import com.t4a.annotations.Predict;
import com.t4a.api.JavaMethodAction;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
@Setter
@Getter
@Predict(actionName = "webPageAction",description = "All actions related to webpage testing", groupDescription = "All actions related to webpage testing",groupName = "web page actions group")
public class SeleniumAction implements JavaMethodAction {

    public DriverActions webPageAction(DriverActions webDriverActions) {
        return webDriverActions;
    }
}
