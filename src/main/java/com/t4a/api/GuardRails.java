package com.t4a.api;

/**
 * Interface for checking guard rails
 */
public interface GuardRails {
    public boolean validateResponse(String response) throws GuardRailException;
    public boolean validateRequest(String prompt) throws GuardRailException;
}
