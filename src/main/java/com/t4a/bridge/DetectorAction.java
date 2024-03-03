package com.t4a.bridge;

import com.t4a.detect.DetectData;

/**
 * Base class for AI Hallucination and Bias detection
 */
public interface DetectorAction extends AIAction{
    @Override
    default String getActionName() {
        return "execute";
    }

    public boolean execute(DetectData dd) throws GuardRailException;
}
