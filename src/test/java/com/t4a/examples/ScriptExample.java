package com.t4a.examples;

import com.t4a.processor.scripts.ScriptResult;
import com.t4a.processor.scripts.SeleniumScriptProcessor;
import com.t4a.processor.selenium.SeleniumGeminiProcessor;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.java.Log;
import org.openqa.selenium.chrome.ChromeDriver;

@Log
public class ScriptExample {
    public static void main(String[] args) {
        //ScriptProcessor script = new ScriptProcessor();
       // ScriptResult result= script.process("test.action");
       // log.info(script.summarize(result));

       // script = new ScriptProcessor(new OpenAiActionProcessor());
       // ScriptResult result= script.process("test.action");
        //log.info(script.summarize(result));
        WebDriverManager.chromedriver().setup();

        ChromeDriver driver = new ChromeDriver();
        SeleniumGeminiProcessor processor = new SeleniumGeminiProcessor(driver);
        SeleniumScriptProcessor scriptProcessor = new SeleniumScriptProcessor(processor);
        ScriptResult result= scriptProcessor.process("web.action");

    }
}
