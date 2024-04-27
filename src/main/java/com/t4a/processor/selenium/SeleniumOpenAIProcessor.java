package com.t4a.processor.selenium;

import com.t4a.JsonUtils;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.OpenAiActionProcessor;
import com.t4a.transform.OpenAIPromptTransformer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
/**
 * The SeleniumOpenAIProcessor class extends the OpenAiActionProcessor and implements the SeleniumProcessor interface.
 * It provides methods for processing web actions using Selenium WebDriver and OpenAI's chat model.
 * It uses the JsonUtils for JSON processing and the OpenAIPromptTransformer for transforming prompts.
 */
@Slf4j
public class SeleniumOpenAIProcessor extends OpenAiActionProcessor implements SeleniumProcessor {
    @Getter
    private WebDriver driver;
    @Getter
    private JsonUtils utils ;
    @Getter
    private OpenAIPromptTransformer transformer ;
    public SeleniumOpenAIProcessor(WebDriver driver) {
        this.driver = driver;
        this.utils = new JsonUtils();
        this.transformer = new OpenAIPromptTransformer();
    }


    public boolean trueFalseQuery(String question) throws AIProcessingException {
        String htmlSource = driver.getPageSource();
        String str =  query(" this is your html { "+htmlSource+"} now answer this question in true or false only "+question);
        return Boolean.valueOf(str.trim());

    }
}
