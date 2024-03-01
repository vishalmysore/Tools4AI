package com.t4a.detect;

import com.t4a.bridge.ActionType;
import com.t4a.bridge.DetectorAction;
import com.t4a.bridge.GuardRailException;

public class HallucinationDetector implements DetectorAction {
    @Override
    public ActionType getActionType() {
        return ActionType.HALLUCINATION;
    }

    @Override
    public String getDescription() {
        return "Detect Hallucination in response";
    }

    @Override
    public boolean execute(String message) throws GuardRailException {
        return false;
    }
}
