package com.t4a.test;

import com.t4a.api.GroupInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GroupInfoTest {

    @Test
    void testAllArgsConstructor() {
        GroupInfo g = new GroupInfo("myGroup", "my description");
        assertEquals("myGroup", g.getGroupName());
        assertEquals("my description", g.getGroupDescription());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        GroupInfo g = new GroupInfo();
        g.setGroupName("grp");
        g.setGroupDescription("desc");
        assertEquals("grp", g.getGroupName());
        assertEquals("desc", g.getGroupDescription());
    }

    @Test
    void testEquals_sameGroupName() {
        GroupInfo a = new GroupInfo("grp", "desc1");
        GroupInfo b = new GroupInfo("grp", "desc2");
        assertEquals(a, b); // equals only considers groupName
    }

    @Test
    void testEquals_differentGroupName() {
        GroupInfo a = new GroupInfo("grp1", "desc");
        GroupInfo b = new GroupInfo("grp2", "desc");
        assertNotEquals(a, b);
    }

    @Test
    void testEquals_null() {
        GroupInfo a = new GroupInfo("grp", "desc");
        assertNotEquals(null, a);
    }

    @Test
    void testEquals_differentClass() {
        GroupInfo a = new GroupInfo("grp", "desc");
        assertNotEquals("grp", a);
    }

    @Test
    void testEquals_sameInstance() {
        GroupInfo a = new GroupInfo("grp", "desc");
        assertEquals(a, a);
    }

    @Test
    void testHashCode_sameGroupName() {
        GroupInfo a = new GroupInfo("grp", "d1");
        GroupInfo b = new GroupInfo("grp", "d2");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToString() {
        GroupInfo g = new GroupInfo("grp", "desc");
        String s = g.toString();
        assertTrue(s.contains("grp"));
        assertTrue(s.contains("desc"));
    }
}
