package com.t4a.api;

/**
 * If guard rails are broken
 */
public class GuardRailException extends Exception{
    public GuardRailException(String message) {
        super(message);
    }
}
