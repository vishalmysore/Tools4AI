package com.t4a.test;

import com.t4a.api.ActionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActionTypeTest {

    @Test
    void testAllValues() {
        ActionType[] types = ActionType.values();
        assertEquals(10, types.length);
    }

    @Test
    void testValueOf() {
        assertEquals(ActionType.JAVACLASS, ActionType.valueOf("JAVACLASS"));
        assertEquals(ActionType.JAVAMETHOD, ActionType.valueOf("JAVAMETHOD"));
        assertEquals(ActionType.GENERICJAVAMETHOD, ActionType.valueOf("GENERICJAVAMETHOD"));
        assertEquals(ActionType.HTTP, ActionType.valueOf("HTTP"));
        assertEquals(ActionType.HALLUCINATION, ActionType.valueOf("HALLUCINATION"));
        assertEquals(ActionType.BIAS, ActionType.valueOf("BIAS"));
        assertEquals(ActionType.FACT, ActionType.valueOf("FACT"));
        assertEquals(ActionType.FILE, ActionType.valueOf("FILE"));
        assertEquals(ActionType.SHELL, ActionType.valueOf("SHELL"));
        assertEquals(ActionType.EXTEND, ActionType.valueOf("EXTEND"));
    }

    @Test
    void testToString() {
        assertEquals("JAVACLASS", ActionType.JAVACLASS.toString());
        assertEquals("HTTP", ActionType.HTTP.toString());
        assertEquals("SHELL", ActionType.SHELL.toString());
    }
}
