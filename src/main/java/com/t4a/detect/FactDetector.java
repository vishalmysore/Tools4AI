package com.t4a.detect;

import com.t4a.bridge.ActionType;
import com.t4a.bridge.DetectorAction;
import com.t4a.bridge.GuardRailException;

public class FactDetector implements DetectorAction {
    @Override
    public ActionType getActionType() {
        return ActionType.FACT;
    }

    @Override
    public String getDescription() {
        return "Fact Check in response";
    }

    @Override
    public DetectValueRes execute(DetectValues dd)  throws GuardRailException {
        return null;
    }
}
