package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.examples.pojo.Organization;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Agent(groupName = "Organization", groupDescription = "Organization actions")
public class ListAction  {

    @Action(description = "add new orgranization")
    public Organization addOrganization(Organization org) {

        System.out.println(org);
        return org;
    }
}
