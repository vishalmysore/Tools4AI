package com.t4a.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionList {
    private List<ActionGroup> groups = new ArrayList<ActionGroup>();
    private List<GroupInfo> groupInfo = new ArrayList();
    private Map<GroupInfo,String> groupActions = new HashMap<>();
    public void addAction(AIAction action) {
      ActionGroup group = new ActionGroup(action.getActionGroup(),action.getGroupDescription());
      if(groups.contains(group)) {
          group = groups.get(groups.indexOf(group));
      } else {
          groups.add(group);
          groupInfo.add(group.getGroupInfo());
      }
      group.addAction(action);
        String oldValue = groupActions.get(group.getGroupInfo());
        String value = action.getActionName();
        if(oldValue == null) {
            groupActions.put(group.getGroupInfo(),value);
        } else {
            value= oldValue+","+value;
            groupActions.put(group.getGroupInfo(),value);
        }
    }
}
