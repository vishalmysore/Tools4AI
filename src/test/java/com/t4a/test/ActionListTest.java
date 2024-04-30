package com.t4a.test;


import com.t4a.api.ActionGroup;
import com.t4a.api.ActionList;
import com.t4a.api.GroupInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ActionListTest {

    @Test
    public void testDefaultConstructor() {
        ActionList actionList = new ActionList();

        assertNotNull(actionList.getGroups());
        assertNotNull(actionList.getGroupInfo());
        assertNotNull(actionList.getGroupActions());
    }

    @Test
    public void testAllArgsConstructor() {
        ArrayList<ActionGroup> groups = new ArrayList<>();
        ArrayList<GroupInfo> groupInfo = new ArrayList<>();
        HashMap<GroupInfo, String> groupActions = new HashMap<>();

        ActionList actionList = new ActionList(groups, groupInfo, groupActions);

        assertEquals(groups, actionList.getGroups());
        assertEquals(groupInfo, actionList.getGroupInfo());
        assertEquals(groupActions, actionList.getGroupActions());
    }
}
