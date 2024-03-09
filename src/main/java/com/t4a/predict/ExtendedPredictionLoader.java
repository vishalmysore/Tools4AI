package com.t4a.predict;

import com.t4a.action.ExtendedPredictedAction;

import java.util.Map;

public interface ExtendedPredictionLoader {
    public Map<String, ExtendedPredictedAction>  getExtendedActions() throws LoaderException;
}
