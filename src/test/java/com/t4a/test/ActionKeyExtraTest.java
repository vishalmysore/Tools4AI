package com.t4a.test;

import com.t4a.api.ActionKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActionKeyExtraTest {
    @Test
    void testActionKeyMethods() {
        MockAction action = new MockAction();
        ActionKey key = new ActionKey(action);

        assertEquals(action.getActionName(), key.getActionName());
        assertEquals(action.getDescription(), key.getActionDescription());
        assertNotEquals(0, key.getUniqueKey());
    }

    @Test
    void testEquality() {
        MockAction action1 = new MockAction();
        ActionKey key1 = new ActionKey(action1);
        ActionKey key2 = new ActionKey(action1);

        // ActionKey equality is based on uniqueKey which is random
        assertNotEquals(key1, key2);
    }
}
