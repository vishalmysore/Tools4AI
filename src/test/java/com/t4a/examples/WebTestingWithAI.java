package com.t4a.examples;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.selenium.SeleniumProcessor;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

public class WebTestingWithAI {
    public static void main(String[] args) throws IOException, AIProcessingException {
        // Set the path of the ChromeDriver executable
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Setting headless mode
        options.addArguments("--disable-gpu");  // GPU hardware acceleration isn't useful in headless mode
        options.addArguments("--window-size=1920,1080");  // Set the window size
        WebDriver driver = new ChromeDriver(options);


        SeleniumProcessor processor = new SeleniumProcessor(driver);
        processor.processWebAction("go to website https://the-internet.herokuapp.com");
        boolean buttonPresent =  processor.trueFalseQuery("do you see Add/Remove Elements?");
        if(buttonPresent) {
            processor.processWebAction("click on Add/Remove Elements");
            // perform other function in simple english

        } //else {
           // processor.processSingleAction("Create Jira by taking screenshot");
       // }

        processor.processWebAction("go to website https://the-internet.herokuapp.com");
        boolean isCheckboxPresent =  processor.trueFalseQuery("do you see Checkboxes?");
        if(isCheckboxPresent) {
            processor.processWebAction("click on Checkboxes");
            processor.processWebAction("select checkbox 1");

        }

        // Perform further actions or validations as needed

        // Close the browser window
        driver.quit();
    }
}
