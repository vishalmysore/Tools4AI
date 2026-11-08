# Ollama end-to-end check

A small standalone program that runs Tools4AI against a real local model through
[Ollama](https://ollama.com)'s OpenAI-compatible API. Unit tests mock the model. This program
does not: every scenario sends real prompts and checks what Tools4AI actually ran.

## What it checks

| # | Scenario | What a pass means |
|---|----------|-------------------|
| 01 | `claudeKey` set in `tools4ai.properties` | The Anthropic model is built from the file (no call is made) |
| 02 | Two agents expose the same action name | A warning names both classes |
| 03 | `query()` | A plain prompt round-trips to the model |
| 04 | Prompt routed to a Java action | The right `@Action` runs with the right parameters, and the `ActionCallback` field is injected |
| 05 | Overloaded method | The annotated overload runs, not the other one |
| 06 | HIGH-risk action predicted by the model | It is refused and never runs |
| 07 | Human-in-loop rejects the action | It is refused and never runs |
| 08 | Prompt routed to a shell script | The script receives the parameters the model extracted |
| 09 | Shell action called with no human-in-loop | It runs without errors |
| 10 | Model returns shell metacharacters (`&`, `>`, `%`) | The arguments are rejected and the script never runs |
| 11 | `LocalAIActionProcessor` | Answers through the OpenAI-compatible API |

Scenarios 08–10 use a `.cmd` script, so they run on Windows only and are skipped elsewhere.

## Run it

1. Start Ollama and pull a model that handles function arguments well. `phi4` works; tiny models
   such as `gemma3:270m` route correctly but leave parameters empty.

   ```bash
   ollama pull phi4
   ```

2. Install Tools4AI from this repository so the example uses the current code:

   ```bash
   mvn install -DskipTests -Dgpg.skip
   ```

3. Run the example from this folder:

   ```bash
   mvn compile exec:java
   ```

To use a different model, change `openAiModelName` in
[src/main/resources/tools4ai.properties](src/main/resources/tools4ai.properties). The program
prints a PASS/FAIL summary and exits with code 1 if any scenario fails.

On a laptop CPU, phi4 takes roughly 5–45 seconds per scenario.

## Configuration used

```properties
openAiKey=ollama                          # any non-empty value; Ollama ignores it
openAiBaseURL=http://localhost:11434/v1
openAiModelName=phi4:latest
maxTimeOutForOpenAI=5                     # minutes; local CPU inference is slow
action.packages.to.scan=e2e
```
