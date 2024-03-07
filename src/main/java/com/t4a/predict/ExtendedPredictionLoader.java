package com.t4a.predict;

import java.util.Map;

public interface ExtendedPredictionLoader {
    public Map<String,ExtendedPredictOptions>  getExtendedActions() throws LoaderException;
}
