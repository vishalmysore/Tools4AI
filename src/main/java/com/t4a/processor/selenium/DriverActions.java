package com.t4a.processor.selenium;

import com.t4a.annotations.Prompt;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class DriverActions {
    @Prompt(describe = "This is the action on selenium webdriver one out of these {get,findElement,findElements,click}")
    String typeOfActionToTakeOnWebDriver;

}
