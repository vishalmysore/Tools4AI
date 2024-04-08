package com.t4a.examples.actions;

import com.t4a.annotations.Predict;
import com.t4a.api.JavaMethodAction;
import com.t4a.examples.pojo.Organization;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Predict(actionName = "addOrganization",description = "add new orgranization")
public class ListAction implements JavaMethodAction {


    public Organization addOrganization(Organization org) {

        System.out.println(org);
        return org;
    }
}
