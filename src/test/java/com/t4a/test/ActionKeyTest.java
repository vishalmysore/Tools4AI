package com.t4a.test;

import com.t4a.api.ActionKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ActionKeyTest {
    @Test
     void testEqualsAndHashCode() {
        // Create two identical AIAction objects
        MockAction action1 = new MockAction();


        MockAction action2 = new MockAction();


        // Create two ActionKey objects using the identical AIAction objects
        ActionKey key1 = new ActionKey(action1);
        ActionKey key2 = new ActionKey(action2);


        assertNotEquals(key1, key2);


        assertNotEquals(key1.hashCode(), key2.hashCode());


        ActionKey key3 = new ActionKey(action1);


        assertNotEquals(key1, key3);

        // The hash codes of the first ActionKey object and the third ActionKey object should noy be the same
        assertNotEquals(key1.hashCode(), key3.hashCode());
    }
}
