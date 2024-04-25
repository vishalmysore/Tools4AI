package com.t4a.detect;

import java.util.regex.Pattern;

/**
 * Detecting prompt injection attacks requires vigilance.
 * Monitor AI outputs for nonsensical responses or unauthorized actions.
 * Validate user inputs to limit malicious characters or keywords. Regularly test your AI system
 * for vulnerabilities and educate users on safe interaction practices.
 * This layered approach can help mitigate the risks of prompt injection attacks on your AI
 */
public class PromptInjectionValidator {
    private String[] threats = {"harm", "attack", "steal", "delete","insert","update","drop","create",
            "alter","truncate","grant","revoke","execute","exec","system","shell","cmd",
            "powershell","bash","sh","csh","ksh","zsh","tcsh","rc","shrc","profile","bashrc",
            "zshrc","tcshrc","cshrc","kshrc","sh_history","bash_history","zsh_history",
            "tcsh_history","csh_history","ksh_history",".bash_history"};
    public  boolean isValidPrompt(String prompt) {
        // Check for disallowed characters or symbols
        Pattern disallowedPattern = Pattern.compile("[^a-zA-Z0-9\\s.!?,]");
        if (disallowedPattern.matcher(prompt).find()) {
            return false;
        }

        // Check for specific keywords or phrases (replace with your threat indicators)

        for (String threat : threats) {
            if (prompt.toLowerCase().contains(threat)) {
                return false;
            }
        }

        // Basic check for prompt length to avoid overflow issues
        if (prompt.length() > 1000) {
            return false;
        }

        // If all checks pass, tentatively assume valid prompt
        return true;
    }
}
