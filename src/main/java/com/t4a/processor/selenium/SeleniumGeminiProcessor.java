package com.t4a.processor.selenium;

import com.t4a.JsonUtils;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import com.t4a.processor.GeminiImageActionProcessor;
import com.t4a.processor.GeminiV2ActionProcessor;
import com.t4a.transform.GeminiV2PromptTransformer;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * The SeleniumGeminiProcessor class extends the GeminiV2ActionProcessor and implements the SeleniumProcessor interface.
 * It provides methods for processing web actions using Selenium WebDriver and Gemini's chat model.
 * It uses the Gson library for JSON processing and the PredictionLoader singleton to access the Gemini chat model.
 */
@Slf4j

@AllArgsConstructor

public class SeleniumGeminiProcessor extends GeminiV2ActionProcessor implements SeleniumProcessor{
    @Getter
    @Setter
    private WebDriver driver;
    @Getter
    @Setter
    private JsonUtils utils ;
    @Getter
    @Setter
    private GeminiV2PromptTransformer transformer ;
    public SeleniumGeminiProcessor(WebDriver driver) {
        this.driver = driver;
        this.utils = new JsonUtils();
        this.transformer = new GeminiV2PromptTransformer();
    }

   public SeleniumGeminiProcessor() {
        this.utils = new JsonUtils();
        this.transformer = new GeminiV2PromptTransformer();
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Setting headless mode
        options.addArguments("--disable-gpu");  // GPU hardware acceleration isn't useful in headless mode
        options.addArguments("--window-size=1920,1080");  // Set the window size
        driver = new ChromeDriver(options);
   }


    public boolean trueFalseQuery(String question) throws AIProcessingException {
         TakesScreenshot ts = (TakesScreenshot) driver;
         byte[] screenshotBytes = ts.getScreenshotAs(OutputType.BYTES);
         GeminiImageActionProcessor imageActionProcessor = new GeminiImageActionProcessor();

         return Boolean.valueOf(imageActionProcessor.imageToText(screenshotBytes,question+", answer in True or False").trim());

    }
    @Override
    public AIProcessor getActionProcessor() {
        return this;
    }
}
