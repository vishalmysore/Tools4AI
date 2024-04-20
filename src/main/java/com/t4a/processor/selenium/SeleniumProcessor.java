package com.t4a.processor.selenium;

import com.t4a.JsonUtils;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiImageActionProcessor;
import com.t4a.processor.GeminiV2ActionProcessor;
import com.t4a.transform.GeminiV2PromptTransformer;
import lombok.extern.java.Log;
import org.openqa.selenium.*;

@Log
public class SeleniumProcessor extends GeminiV2ActionProcessor {
    private WebDriver driver;
    private JsonUtils utils ;
    private GeminiV2PromptTransformer transformer ;
    public SeleniumProcessor(WebDriver driver) {
        this.driver = driver;
        this.utils = new JsonUtils();
        this.transformer = new GeminiV2PromptTransformer();
    }
    public void processWebAction(String prompt) throws AIProcessingException {

        DriverActions actions = (DriverActions)processSingleAction(prompt,"webPageAction");
        String act = actions.getTypeOfActionToTakeOnWebDriver();
        WebDriverAction action = WebDriverAction.valueOf(act.toUpperCase());
        if (WebDriverAction.GET.equals(action)) {
            String urlOfTheWebPage = getStringFromPrompt(prompt, "urlToClick");
            driver.get(urlOfTheWebPage);
        }if (WebDriverAction.CLICK.equals(action)) {
            String textOfElementToClick = getStringFromPrompt(prompt, "textOfElementToClick");
            WebElement elementToClick = driver.findElement(By.linkText(textOfElementToClick));
            elementToClick.click();
        }
    }

    private String getStringFromPrompt(String prompt,  String key) throws AIProcessingException {
        String urlOfTheWebPage = transformer.transformIntoJson(utils.createJson(key).toString(), prompt);
        log.info(urlOfTheWebPage);
        urlOfTheWebPage = utils.getFieldValue(urlOfTheWebPage,key);
        return urlOfTheWebPage;
    }

    public boolean trueFalseQuery(String question) throws AIProcessingException {
         TakesScreenshot ts = (TakesScreenshot) driver;
         byte[] screenshotBytes = ts.getScreenshotAs(OutputType.BYTES);
         GeminiImageActionProcessor imageActionProcessor = new GeminiImageActionProcessor();
         return Boolean.valueOf(imageActionProcessor.imageToText(screenshotBytes,question+", answer in True or False").trim());

    }
}
