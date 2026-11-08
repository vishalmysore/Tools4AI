package quickstart;

import com.t4a.processor.OpenAiActionProcessor;

/**
 * Sends a prompt to a local Ollama model and lets Tools4AI run the matching Java method.
 *
 * <pre>
 * mvn -q compile exec:java
 * mvn -q compile exec:java -Dexec.args="Is lasagna on the menu today?"
 * mvn -q compile exec:java -DopenAiModelName=llama3.2
 * </pre>
 */
public class QuickStart {

    private static final String DEFAULT_MODEL = "qwen2.5:3b";

    // Held in a field so the level is not lost when the logger is garbage collected
    private static final java.util.logging.Logger TOOLS4AI_LOG = java.util.logging.Logger.getLogger("com.t4a");

    public static void main(String[] args) {
        // Hide notices about optional config files (shell_actions.yaml, http_actions.json) this example does not use
        TOOLS4AI_LOG.setLevel(java.util.logging.Level.SEVERE);
        // tools4ai.properties leaves the model blank so it can be chosen with -DopenAiModelName
        if (System.getProperty("openAiModelName") == null) {
            System.setProperty("openAiModelName", DEFAULT_MODEL);
        }
        String prompt = args.length > 0
                ? String.join(" ", args)
                : "Book a table for 4 people under the name Maria this Friday";

        System.out.println("Model  : " + System.getProperty("openAiModelName"));
        System.out.println("Prompt : " + prompt);
        try {
            Object result = new OpenAiActionProcessor().processSingleAction(prompt);
            System.out.println("Result : " + result);
        } catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            System.out.println("Failed : " + cause);
            if (cause instanceof java.net.ConnectException || String.valueOf(cause.getMessage()).contains("not found")) {
                System.out.println("Is Ollama running (ollama serve) and the model pulled (ollama pull "
                        + System.getProperty("openAiModelName") + ")?");
            }
        }
    }
}
