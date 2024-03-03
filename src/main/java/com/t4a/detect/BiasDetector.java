package com.t4a.detect;

import com.t4a.bridge.ActionType;
import com.t4a.bridge.DetectorAction;
import com.t4a.bridge.GuardRailException;

public class BiasDetector implements DetectorAction {
    @Override
    public ActionType getActionType() {
        return ActionType.BIAS;
    }

    @Override
    public String getDescription() {
        return "Detect Bias in response";
    }

    @Override
    public boolean execute(DetectData dd) throws GuardRailException {
        return false;
    }
}
