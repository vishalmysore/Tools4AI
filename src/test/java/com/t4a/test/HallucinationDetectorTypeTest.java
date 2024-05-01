package com.t4a.test;

import com.t4a.detect.HallucinationDetectorType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HallucinationDetectorTypeTest {

    @Test
    public void testEnumValues() {
        assertEquals(2, HallucinationDetectorType.values().length);
        assertEquals(HallucinationDetectorType.GOOGLE, HallucinationDetectorType.values()[0]);
        assertEquals(HallucinationDetectorType.SELF, HallucinationDetectorType.values()[1]);
    }
}
