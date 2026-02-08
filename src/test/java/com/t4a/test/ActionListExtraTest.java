package com.t4a.test;

import com.t4a.api.ActionGroup;
import com.t4a.api.ActionList;
import com.t4a.api.GroupInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ActionListExtraTest {
    @Test
    void testAddGroup() {
        ActionList actionList = new ActionList();
        ActionGroup group = new ActionGroup("G1", "D1");
        actionList.getGroups().add(group);

        assertEquals(1, actionList.getGroups().size());
        assertEquals(group, actionList.getGroups().get(0));
    }

    @Test
    void testRetainProperties() {
        ArrayList<ActionGroup> groups = new ArrayList<>();
        ArrayList<GroupInfo> groupInfo = new ArrayList<>();
        HashMap<GroupInfo, String> groupActions = new HashMap<>();

        ActionList actionList = new ActionList(groups, groupInfo, groupActions);

        assertSame(groups, actionList.getGroups());
        assertSame(groupInfo, actionList.getGroupInfo());
        assertSame(groupActions, actionList.getGroupActions());
    }
}
