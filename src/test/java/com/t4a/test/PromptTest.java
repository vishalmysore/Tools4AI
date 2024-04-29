package com.t4a.test;
import com.t4a.processor.chain.Prompt;
import com.t4a.processor.chain.SubPrompt;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PromptTest {

    @Test
    public void testPrompt() {
        // Arrange
        SubPrompt subPrompt1 = new SubPrompt();
        subPrompt1.setId("1");
        subPrompt1.setSubprompt("SubPrompt 1");
        subPrompt1.setDepend_on("Depend 1");

        SubPrompt subPrompt2 = new SubPrompt();
        subPrompt2.setId("2");
        subPrompt2.setSubprompt("SubPrompt 2");
        subPrompt2.setDepend_on("Depend 2");

        List<SubPrompt> subPrompts = Arrays.asList(subPrompt1, subPrompt2);

        // Act
        Prompt prompt = new Prompt();
        prompt.setPrmpt(subPrompts);

        // Assert
        assertEquals(subPrompts, prompt.getPrmpt());
    }
}