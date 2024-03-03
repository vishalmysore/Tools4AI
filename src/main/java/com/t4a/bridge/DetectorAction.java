package com.t4a.bridge;

import com.t4a.detect.DetectValues;
import com.t4a.detect.DetectValueRes;

/**
 * Base class for AI Hallucination and Bias detection
 */
public interface DetectorAction extends AIAction{
    @Override
    default String getActionName() {
        return "execute";
    }

    public DetectValueRes execute(DetectValues dd) throws GuardRailException;
}
