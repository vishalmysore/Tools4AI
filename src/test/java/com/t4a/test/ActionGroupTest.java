package com.t4a.test;

import com.t4a.api.AIAction;
import com.t4a.api.ActionGroup;
import com.t4a.api.ActionKey;
import com.t4a.api.GroupInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionGroupTest {

    private ActionGroup actionGroup;

    @BeforeEach
    public void setUp() {
        actionGroup = new ActionGroup("TestGroup", "Test Description");
    }

    @Test
    void testAddAction() {
        AIAction action = new MockAction();
        actionGroup.addAction(action);

        assertEquals(1, actionGroup.getActions().size());
        assertEquals(action.getActionName(), actionGroup.getActions().get(0).getActionName());
    }

    @Test
    void testEquals() {
        ActionGroup anotherActionGroup = new ActionGroup("TestGroup", "Test Description");
        assertTrue(actionGroup.equals(anotherActionGroup));
    }

    @Test
    void testGetActions() {
        AIAction action1 = new MockAction();
        AIAction action2 = new MockAction();
        AIAction action3 = new MockAction();

        actionGroup.addAction(action1);
        actionGroup.addAction(action2);
        actionGroup.addAction(action3);

        List<ActionKey> actions = actionGroup.getActions();
        assertEquals(3, actions.size());
        
        // Verify action names are present in the list
        assertTrue(actions.stream().anyMatch(key -> key.getActionName().equals(action1.getActionName())));
        assertTrue(actions.stream().anyMatch(key -> key.getActionName().equals(action2.getActionName())));
        assertTrue(actions.stream().anyMatch(key -> key.getActionName().equals(action3.getActionName())));
    }

    @Test
    void testGroupInfo() {
        GroupInfo info = actionGroup.getGroupInfo();
        assertEquals("TestGroup", info.getGroupName());
        assertEquals("Test Description", info.getGroupDescription());
    }

    @Test
    void testHashCode() {
        ActionGroup sameGroup = new ActionGroup("TestGroup", "Test Description");
        ActionGroup differentGroup = new ActionGroup("DifferentGroup", "Different Description");

        assertEquals(actionGroup.hashCode(), sameGroup.hashCode());
        assertNotEquals(actionGroup.hashCode(), differentGroup.hashCode());
    }

    @Test
    void testEqualsWithDifferentScenarios() {
        // Test with null
        assertFalse(actionGroup.equals(null));

        // Test with different class
        assertFalse(actionGroup.equals(new Object()));

        // Test with same values
        ActionGroup sameGroup = new ActionGroup("TestGroup", "Test Description");
        assertTrue(actionGroup.equals(sameGroup));

        // Test with different group name
        ActionGroup differentName = new ActionGroup("DifferentGroup", "Test Description");
        assertFalse(actionGroup.equals(differentName));

        // Test with different description
        ActionGroup differentDesc = new ActionGroup("TestGroup", "Different Description");
        assertTrue(actionGroup.equals(differentDesc)); // Description doesn't affect equality

        // Test with different actions
        ActionGroup groupWithActions = new ActionGroup("TestGroup", "Test Description");
        groupWithActions.addAction(new MockAction());
        assertTrue(actionGroup.equals(groupWithActions)); // Actions don't affect equality, only groupInfo is compared
    }

    @Test
    void testSingleArgumentConstructor() {
        ActionGroup group = new ActionGroup("TestGroup");
        assertEquals("TestGroup", group.getGroupInfo().getGroupName());
        assertNotNull(group.getGroupInfo().getGroupDescription());
        assertTrue(group.getActions().isEmpty());
    }

    @Test
    void testNoArgumentConstructor() {
        ActionGroup group = new ActionGroup();
        assertNotNull(group.getActions());
        assertNotNull(group.getGroupInfo());
        assertTrue(group.getActions().isEmpty());
    }

    @Test
    void testAddActionKey() {
        ActionKey key = new ActionKey(new MockAction());
        actionGroup.addAction(key);
        
        assertEquals(1, actionGroup.getActions().size());
        assertEquals(key.getActionName(), actionGroup.getActions().get(0).getActionName());
    }

    @Test
    void testEqualsWithActions() {
        ActionGroup group1 = new ActionGroup("TestGroup", "Description");
        ActionGroup group2 = new ActionGroup("TestGroup", "Description");
        
        // Should be equal initially
        assertEquals(group1, group2);
        
        // Add same actions in same order
        AIAction action1 = new MockAction();
        AIAction action2 = new MockAction();
        
        group1.addAction(action1);
        group1.addAction(action2);
        
        group2.addAction(action1);
        group2.addAction(action2);
        
        assertEquals(group1, group2);
        assertEquals(group1.hashCode(), group2.hashCode());
    }
      @Test
    void testEqualsWithDifferentActions() {
        ActionGroup group1 = new ActionGroup("TestGroup1", "Description");
        ActionGroup group2 = new ActionGroup("TestGroup2", "Description");
        
        group1.addAction(new MockAction());
        group2.addAction(new MockAction());
        
        // Should not be equal due to different group names
        assertNotEquals(group1, group2);
        assertNotEquals(group1.hashCode(), group2.hashCode());
    }@Test
    void testAddActionWithNullAction() {
        assertDoesNotThrow(() -> actionGroup.addAction((AIAction)null));
        assertEquals(1, actionGroup.getActions().size());
        assertNull(actionGroup.getActions().get(0).getActionName());
    }
    
    @Test
    void testAddActionWithNullKey() {
        assertDoesNotThrow(() -> actionGroup.addAction((ActionKey)null));
        assertEquals(1, actionGroup.getActions().size());
        assertNull(actionGroup.getActions().get(0));
    }

    @Test
    void testActionListModification() {
        AIAction action = new MockAction();
        actionGroup.addAction(action);
        
        List<ActionKey> actions = actionGroup.getActions();
        actions.add(new ActionKey(new MockAction()));
        
        // The internal list should be modified
        assertEquals(2, actionGroup.getActions().size());
    }

    @Test
    void testNoArgsConstructor() {
        ActionGroup group = new ActionGroup();
        assertNotNull(group.getActions());
        assertNotNull(group.getGroupInfo());
        assertTrue(group.getActions().isEmpty());
        assertNull(group.getGroupInfo().getGroupName());
        assertNull(group.getGroupInfo().getGroupDescription());
    }
}