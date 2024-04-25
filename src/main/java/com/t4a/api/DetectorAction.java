package com.t4a.api;

import com.t4a.detect.DetectValues;
import com.t4a.detect.DetectValueRes;
import com.t4a.processor.AIProcessingException;

/**
 * Base class for AI Hallucination and Bias detection
 */
public interface DetectorAction extends AIAction{
    @Override
    default String getActionName() {
        return "execute";
    }

    public DetectValueRes execute(DetectValues dd) throws GuardRailException, AIProcessingException;
}
