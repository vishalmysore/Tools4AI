package com.t4a.bridge;

/**
 * Uses Gemini to check the user prompts
 */
public class GeminiGuardRails implements GuardRails{
    String validateOffensive = "Does this sentence contain anything offensive - ";
    String validatePII;


    public GeminiGuardRails() {
    }

    @Override
    public boolean validateResponse(String response) throws GuardRailException {
        return false;
    }

    @Override
    public boolean validateRequest(String prompt) throws GuardRailException {
        return false;
    }
}
