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
    @Prompt(describe = "What method should i invoke on org.openqa.selenium.WebDriver " +
            "{navigate, get," +
             "click, " +
            "takescreenshot, sendKeys,clear,submit,getText," +
            "isDisplayed,isEnabled," +
            "isSelected,getAttribute,switchTo,selectByVisibleText," +
            "selectByValue,selectByIndex" +
            "}" +
            "")
    String typeOfActionToTakeOnWebDriver;

}
