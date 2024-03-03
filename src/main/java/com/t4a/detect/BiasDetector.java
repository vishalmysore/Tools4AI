package com.t4a.detect;

import com.t4a.api.ActionType;
import com.t4a.api.DetectorAction;
import com.t4a.api.GuardRailException;

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
    public DetectValueRes execute(DetectValues dd) throws GuardRailException {
        return null;
    }
}
