package com.t4a.processor.selenium;

import com.t4a.JsonUtils;
import com.t4a.processor.AIProcessingException;
import com.t4a.transform.PromptTransformer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SeleniumProcessor {
    Logger LOGGER = LoggerFactory.getLogger(SeleniumProcessor.class);
    public default void processWebAction(String prompt) throws AIProcessingException {

        DriverActions actions = (DriverActions)getTransformer().transformIntoPojo(prompt,DriverActions.class);
        String act = actions.getTypeOfActionToTakeOnWebDriver();
        LOGGER.debug("Processing the web action: {}", act);
        WebDriverAction action = WebDriverAction.valueOf(act.toUpperCase());
        if (WebDriverAction.GET.equals(action)) {
            String urlOfTheWebPage = getStringFromPrompt(prompt, "urlToClick");
            LOGGER.debug("Opening the web page: {}", urlOfTheWebPage);
            getDriver().get(urlOfTheWebPage);
        }
        if (WebDriverAction.CLICK.equals(action)) {
            String textOfElementToClick = getStringFromPrompt(prompt, "textOfElementToClick");
            LOGGER.debug("Clicking on the element with text: {}", textOfElementToClick);
            WebElement elementToClick = getDriver().findElement(By.linkText(textOfElementToClick));
            elementToClick.click();
        }
    }

    public default String getStringFromPrompt(String prompt,  String key) throws AIProcessingException {
        String urlOfTheWebPage = getTransformer().transformIntoJson(getUtils().createJson(key).toString(), prompt);

        urlOfTheWebPage = getUtils().getFieldValue(urlOfTheWebPage,key);
        return urlOfTheWebPage;
    }
    public boolean trueFalseQuery(String question) throws AIProcessingException ;

    public WebDriver getDriver();
    public JsonUtils getUtils();
    public PromptTransformer getTransformer();
}
