package com.t4a.bridge;

public interface GuardRails {
    public boolean validateResponse(String response) throws GuardRailException;
    public boolean validateRequest(String prompt) throws GuardRailException;
}
