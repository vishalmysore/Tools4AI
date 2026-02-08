package com.t4a.test;

import com.t4a.predict.ConfigManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigManagerTest {
    @Test
    void testGetPropertyDefault() {
        ConfigManager config = new ConfigManager();
        // Since prompt.properties is likely missing in the test environment
        assertEquals("default", config.getProperty("non.existent.key", "default"));
    }

    @Test
    void testGetPropertyWithDefault() {
        ConfigManager config = new ConfigManager();
        assertEquals("anotherDefault", config.getProperty("someKey", "anotherDefault"));
    }
}
