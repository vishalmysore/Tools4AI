package com.t4a.action.shell;

import com.t4a.api.AIAction;
import com.t4a.api.ActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * base class to execute shell commands
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShellAction implements AIAction {

    private String description;
    private String shellPath;



    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getActionName() {
        return "executeShell";
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SHELL;
    }
}
