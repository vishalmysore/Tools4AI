package com.t4a.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApiComponentsTest {

    @Test
    public void testActionGroup() {
        ActionGroup group = new ActionGroup("testGroup", "testDesc");
        assertEquals("testGroup", group.getGroupInfo().getGroupName());
        assertEquals("testDesc", group.getGroupInfo().getGroupDescription());

        ActionGroup group2 = new ActionGroup("testGroup");
        assertEquals("testGroup", group2.getGroupInfo().getGroupName());
        assertEquals("", group2.getGroupInfo().getGroupDescription());

        ActionGroup group3 = new ActionGroup();
        group3.setGroupInfo(new GroupInfo("g1", "d1"));
        assertEquals("g1", group3.getGroupInfo().getGroupName());

        AIAction mockAction = mock(AIAction.class);
        when(mockAction.getActionName()).thenReturn("action1");
        group.addAction(mockAction);
        assertEquals(1, group.getActions().size());

        ActionGroup groupDuplicate = new ActionGroup("testGroup", "testDesc");
        assertEquals(group, groupDuplicate);
    }

    @Test
    public void testActionKey() {
        AIAction mockAction = mock(AIAction.class);
        when(mockAction.getActionName()).thenReturn("name1");
        when(mockAction.getDescription()).thenReturn("desc1");

        ActionKey key1 = new ActionKey(mockAction);
        assertEquals("name1", key1.getActionName());
        assertEquals("desc1", key1.getActionDescription());
        assertTrue(key1.getUniqueKey() > 0);

        ActionKey key2 = new ActionKey();
        key2.setActionName("name3");
        assertEquals("name3", key2.getActionName());

        ActionKey key3 = new ActionKey();
        key3.setUniqueKey(key1.getUniqueKey());
        assertEquals(key1, key3);
        assertEquals(key1.hashCode(), key3.hashCode());
    }

    @Test
    public void testGroupInfo() {
        GroupInfo info1 = new GroupInfo("g1", "d1");
        assertEquals("g1", info1.getGroupName());
        assertEquals("d1", info1.getGroupDescription());

        GroupInfo info2 = new GroupInfo();
        info2.setGroupName("g1");
        info2.setGroupDescription("d1");

        assertEquals(info1, info2);
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    public void testDetectorAction() {
        DetectorAction action = mock(DetectorAction.class, CALLS_REAL_METHODS);
        assertEquals("execute", action.getActionName());
    }
}
