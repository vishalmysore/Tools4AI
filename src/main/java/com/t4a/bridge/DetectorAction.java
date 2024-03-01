package com.t4a.bridge;

/**
 * Base class for AI Hallucination and Bias detection
 */
public interface DetectorAction extends AIAction{
    @Override
    default String getActionName() {
        return "execute";
    }

    boolean execute(String message)throws GuardRailException;
}
