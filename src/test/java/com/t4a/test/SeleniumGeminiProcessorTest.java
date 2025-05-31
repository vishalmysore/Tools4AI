package com.t4a.test;

import com.t4a.JsonUtils;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.selenium.DriverActions;
import com.t4a.processor.selenium.SeleniumGeminiProcessor;
import com.t4a.transform.GeminiV2PromptTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
public class SeleniumGeminiProcessorTest {

   // @Test
    void testWebAction() throws AIProcessingException {
        WebDriver mockAction = Mockito.mock(WebDriver.class);
        JsonUtils mockUtils = new JsonUtils();


        GeminiV2PromptTransformer mockTransformer = Mockito.mock(GeminiV2PromptTransformer.class);
        SeleniumGeminiProcessor processor = new SeleniumGeminiProcessor(mockAction);
        processor.setUtils(mockUtils);
        DriverActions actions = new DriverActions();
        actions.setTypeOfActionToTakeOnWebDriver("get");
        String json = "{\"fieldName\":\"urlToClick\",\"fieldType\":\"String\",\"fieldValue\":\"google.com\"}";
        Mockito.when(mockTransformer.transformIntoPojo(anyString(), eq(DriverActions.class))).thenReturn(actions);
        Mockito.when(mockTransformer.transformIntoJson(anyString(), anyString())).thenReturn(json);
        processor.setTransformer(mockTransformer);

        try {
            processor.processWebAction("prompt");
            Mockito.verify(mockAction).get("google.com");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    void testWebActionClick() throws AIProcessingException {
        WebDriver mockAction = Mockito.mock(WebDriver.class);
        WebElement mockElement = Mockito.mock(WebElement.class);
        String elementName = "myelement";
        Mockito.when(mockAction.findElement(By.linkText(elementName))).thenReturn(mockElement);

        JsonUtils mockUtils = new JsonUtils();


        GeminiV2PromptTransformer mockTransformer = Mockito.mock(GeminiV2PromptTransformer.class);
        SeleniumGeminiProcessor processor = new SeleniumGeminiProcessor(mockAction);
        processor.setUtils(mockUtils);
        DriverActions actions = new DriverActions();
        actions.setTypeOfActionToTakeOnWebDriver("CLICK");
        String json = "{\"fieldName\":\"textOfElementToClick\",\"fieldType\":\"String\",\"fieldValue\":\""+elementName+"\"}";
        Mockito.when(mockTransformer.transformIntoPojo(anyString(), eq(DriverActions.class))).thenReturn(actions);
        Mockito.when(mockTransformer.transformIntoJson(anyString(), anyString())).thenReturn(json);
        processor.setTransformer(mockTransformer);

        try {


            processor.processWebAction("prompt");
            ArgumentCaptor<By> argument = ArgumentCaptor.forClass(By.class);
            Mockito.verify(mockAction).findElement(argument.capture());
            By capturedArgument = argument.getValue();
            Assertions.assertEquals(By.linkText(elementName), capturedArgument);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
