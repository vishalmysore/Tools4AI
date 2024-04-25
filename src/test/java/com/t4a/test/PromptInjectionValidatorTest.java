package com.t4a.test;

import com.t4a.detect.PromptInjectionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptInjectionValidatorTest {

    private PromptInjectionValidator promptInjectionValidator;

    @BeforeEach
    public void setup() {
        promptInjectionValidator = new PromptInjectionValidator();
    }

    @Test
    public void testIsValidPrompt() {
        assertTrue(promptInjectionValidator.isValidPrompt("This is a safe prompt."));
        assertFalse(promptInjectionValidator.isValidPrompt("This prompt contains a threat: delete"));
        assertFalse(promptInjectionValidator.isValidPrompt("This prompt contains a disallowed character: #"));
    }
}
