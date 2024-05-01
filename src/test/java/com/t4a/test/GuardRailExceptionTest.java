package com.t4a.test;


import com.t4a.api.GuardRailException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GuardRailExceptionTest {

    @Test
    public void testGuardRailException() {
        assertThrows(GuardRailException.class, () -> {
            throw new GuardRailException("Test exception");
        });
    }
}
