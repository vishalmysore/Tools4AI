package com.t4a.processor;

import com.google.gson.Gson;

/**
 * Processor for locally hosted models (Ollama, LocalAI, LM Studio, vLLM ...). These servers expose the
 * OpenAI-compatible API, so this is the {@link OpenAiActionProcessor} pointed at the local server. Configure it in
 * {@code tools4ai.properties}:
 * <pre>
 * openAiKey=ollama                              # any non-empty value
 * openAiBaseURL=http://localhost:11434/v1
 * openAiModelName=llama3.1
 * </pre>
 *
 * @deprecated use {@link OpenAiActionProcessor} with {@code openAiBaseURL} set; this class only remains so existing
 * code that referenced it keeps working (it previously returned {@code null} from every method).
 */
@Deprecated
public class LocalAIActionProcessor extends OpenAiActionProcessor {

    public LocalAIActionProcessor() {
        super();
    }

    public LocalAIActionProcessor(Gson gson) {
        super(gson);
    }
}
