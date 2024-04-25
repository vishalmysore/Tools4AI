package com.t4a.detect;

import com.t4a.api.ActionType;
import com.t4a.api.DetectorAction;
import com.t4a.api.GuardRailException;

/**
 * Detect Bias in response using Zero-shot classification
 * To detect bias, look for unequal treatment in outputs, analyze the data source,
 * and challenge the AI's assumptions with follow-up questions.
 */
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
