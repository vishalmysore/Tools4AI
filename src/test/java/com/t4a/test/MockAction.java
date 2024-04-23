package com.t4a.test;

import com.t4a.api.ActionRisk;
import com.t4a.api.ActionType;
import com.t4a.api.JavaMethodAction;

public class MockAction implements JavaMethodAction {

    public Person p;
    public String name;
    @Override
    public String getActionName() {
        return "mockAction";
    }

    public String mockAction(String mockName,Person mockPerson) {
      p = mockPerson;
        name = mockName;
       return mockName;
    }

    @Override
    public String getDescription() {
        return "mockActionDescription";
    }




}
