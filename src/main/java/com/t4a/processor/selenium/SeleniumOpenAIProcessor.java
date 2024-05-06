package com.t4a.processor.selenium;

import com.t4a.JsonUtils;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import com.t4a.processor.OpenAiActionProcessor;
import com.t4a.transform.OpenAIPromptTransformer;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * The SeleniumOpenAIProcessor class extends the OpenAiActionProcessor and implements the SeleniumProcessor interface.
 * It provides methods for processing web actions using Selenium WebDriver and OpenAI's chat model.
 * It uses the JsonUtils for JSON processing and the OpenAIPromptTransformer for transforming prompts.
 */
@Slf4j
public class SeleniumOpenAIProcessor extends OpenAiActionProcessor implements SeleniumProcessor {
    @Getter
    @Setter
    private WebDriver driver;
    @Getter
    @Setter
    private JsonUtils utils ;
    @Getter
    @Setter
    private OpenAIPromptTransformer transformer ;
    public SeleniumOpenAIProcessor(WebDriver driver) {
        this.driver = driver;
        this.utils = new JsonUtils();
        this.transformer = new OpenAIPromptTransformer();
    }
    public SeleniumOpenAIProcessor() {
        this.utils = new JsonUtils();
        this.transformer = new OpenAIPromptTransformer();
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Setting headless mode
        options.addArguments("--disable-gpu");  // GPU hardware acceleration isn't useful in headless mode
        options.addArguments("--window-size=1920,1080");  // Set the window size
        driver = new ChromeDriver(options);
    }

    public boolean trueFalseQuery(String question) throws AIProcessingException {
        String htmlSource = driver.getPageSource();
        String str =  query(" this is your html { "+htmlSource+"} now answer this question in true or false only "+question);
        return Boolean.valueOf(str.trim());

    }

    @Override
    public AIProcessor getActionProcessor() {
        return this;
    }
}
