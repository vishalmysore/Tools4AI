package com.t4a.test;

import com.t4a.annotations.Action;
import com.t4a.api.JavaMethodAction;

public class MockActionWithAnnotation implements JavaMethodAction {

    public Person p;
    public String name;

    @Action(description = "mockActionDescription")
    public String mockAction(String mockName, Person mockPerson) {
        p = mockPerson;
        name = mockName;
        return mockName;
    }

    @Override
    public String getActionName() {
        return "mockAction";
    }

    @Override
    public String getDescription() {
        return "mockActionDescription";
    }

    @Override
    public String getPrompt() {
        return "";
    }

    @Override
    public String getSubprompt() {
        return "";
    }
}
