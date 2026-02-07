package com.t4a.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionGroup {
    private List<ActionKey> actions = new ArrayList<>();
    private GroupInfo groupInfo = new GroupInfo();

    public ActionGroup(String groupName) {
        String cleanName = groupName == null
                ? null
                : groupName.replaceAll("\\r?\\n", "").trim();

        groupInfo.setGroupName(cleanName);
        groupInfo.setGroupDescription("");  // Initialize with empty string
    }

    public ActionGroup(String groupName, String groupDescription) {
        groupInfo.setGroupName(groupName);
        groupInfo.setGroupDescription(groupDescription);
    }    public void addAction(ActionKey key) {
        actions.add(key);
    }
    
    public void addAction(AIAction action) {
        actions.add(new ActionKey(action));
    }@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionGroup that = (ActionGroup) o;
        return Objects.equals(groupInfo, that.groupInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupInfo);
    }
}
