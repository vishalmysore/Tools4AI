# 🧭 Tools4AI — Architecture, Comparison & Roadmap

## Table of Contents
- [What is Tools4AI?](#what-is-tools4ai)
- [How It Works](#how-it-works)
- [Tools4AI vs Spring AI](#tools4ai-vs-spring-ai)
- [Improvement Roadmap](#improvement-roadmap)

---

## What is Tools4AI?

**Tools4AI** (v1.2.1, published on Maven Central) is a **pure Java agentic AI framework / ADK (Agent Development Kit)**.  
Its core idea is simple and powerful: **annotate existing Java methods with `@Agent`/`@Action` and the framework automatically maps natural-language prompts to those method calls at runtime** — no manual parsing, no glue code.

```java
@Agent
public class CookingAction {
    @Action(description = "what food does this person like")
    public String whatFoodDoesThisPersonLike(String name) {
        return "Paneer Butter Masala";
    }
}

// Somewhere else — zero plumbing:
new OpenAiActionProcessor().processSingleAction("I don't know what to cook for Vishal");
// → calls whatFoodDoesThisPersonLike("Vishal") automatically
```

---

## How It Works

| Capability | How |
|---|---|
| **Prompt → Java method call** | AI reads `@Action` descriptions and routes prompts to the right method, extracting typed parameters (primitives, POJOs, Lists, Maps, arrays) |
| **Prompt → POJO** | `PromptTransformer` converts free-text directly into complex Java object graphs |
| **Multi-LLM** | Supports Gemini (Vertex AI), OpenAI, Anthropic Claude, HuggingFace, LocalAI via LangChain4j |
| **Multi-action type** | Java methods, HTTP REST (via `http_actions.json`), Shell scripts (via `shell_actions.yaml`), Swagger/OpenAPI endpoints, extended custom loaders |
| **Safety & trust** | `GuardRails`, `HumanInLoop`, `ActionCallback` for progress, `ActionRisk` (LOW/MEDIUM/HIGH) for approval gates |
| **Response validation** | Hallucination detection (`ZeroShotHallucinationDetector`), bias detection (`BiasDetector`), fact checking (`FactDetector`) |
| **Spring integration** | `SpringGeminiProcessor`, `SpringAnthropicProcessor`, `SpringOpenAIProcessor` — plug straight into Spring beans |
| **Protocol support** | A2A, MCP, A2UI, UCP protocols |
| **Image actions** | `GeminiImageActionProcessor` — image-to-text, image-to-POJO, image comparison |
| **Script actions** | Scripted multi-step action chains via `.action` files |

---

## Tools4AI vs Spring AI

They sound similar but solve fundamentally different problems:

| Dimension | **Tools4AI** | **Spring AI** |
|---|---|---|
| **Core purpose** | **Agentic action routing** — maps prompts to pre-existing Java methods/REST/shell | **AI primitives** — chat, embeddings, RAG, vector stores for Spring apps |
| **Entry point** | Annotate *any* existing Java class/method — works without Spring | Built on Spring Boot — Spring context is required |
| **Action discovery** | Annotation-driven classpath scan + YAML/JSON config for REST/shell | Manual `@Tool` registration (since Spring AI 1.0), function callbacks |
| **Parameter mapping** | Fully automatic — extracts typed params from prompt and populates POJOs, Lists, Maps, arrays | Manual — you define function schemas explicitly |
| **Non-Java actions** | First-class shell scripts, Swagger/OpenAPI, HTTP REST — no code needed | Not supported natively |
| **LLM provider** | Gemini, OpenAI, Anthropic, HuggingFace, LocalAI | Same set + Azure OpenAI, Amazon Bedrock, Ollama, Mistral, etc. |
| **RAG / Vector stores** | Not present | Core feature (PGVector, Chroma, Pinecone, Redis, Weaviate…) |
| **Embeddings** | Not present | Core feature |
| **Image handling** | `GeminiImageActionProcessor` — image → POJO, compare | Multimodal in newer versions but not POJO mapping |
| **Safety layer** | `GuardRails`, `HumanInLoop`, hallucination/bias/fact detectors | Not built in |
| **Target user** | Enterprise Java dev wanting to AI-enable existing apps *without refactoring* | Spring Boot dev building new AI-native applications |
| **Singleton/classpath** | Single `PredictionLoader` scans the whole classpath | Spring ApplicationContext — standard IoC |
| **Weight** | Lightweight, single JAR | Full Spring ecosystem |

> **The one-line difference:** Spring AI is a *platform* for building AI-first apps.  
> Tools4AI is a *retrofit layer* that makes any existing Java system AI-controllable with minimal code change.

---

## Improvement Roadmap

The items below are forward-looking ideas. **No existing code is changed** — each represents a net-new addition or extension point.

### 1. 🏗️ Architecture

- **`PredictionLoader` is a singleton with global mutable state** — this is the root cause of all test isolation issues and would cause problems in multi-tenant apps. A proper DI-friendly `ActionRegistry` with scope control would fix this.
- **Classpath scanning is unbounded** — scanning the entire classpath by default is slow and fragile. Opt-in package scanning (`actionPackagesToScan`) is there but not the default.
- **No async/reactive support** — all `processSingleAction` calls are blocking. Modern agentic workloads need streaming and async execution.

### 2. 🤖 Agent Capabilities

- **No memory / conversation history** — each prompt is stateless. Real agents need short-term (turn) and long-term (session/persistent) memory.
- **No multi-agent orchestration** — `MultiBot` exists in examples but there is no first-class framework for agent-to-agent delegation with result aggregation.
- **No planning loop** — there is no ReAct (Reason+Act) or chain-of-thought loop. The AI picks one action and executes it. More complex tasks need iterative planning.
- **No tool result feedback** — after a Java method executes, the result is not fed back to the LLM for a follow-up reasoning step.

### 3. 🔒 Safety & Observability

- **`HumanInLoop` is an interface with no built-in UI** — there is no out-of-box approval UI/webhook, just the interface contract.
- **`ActionRisk` gates are not enforced by the framework** — MEDIUM/HIGH risk actions do not automatically pause for approval unless the caller explicitly checks.
- **No audit trail / action log** — no persistent record of what was executed, by whom, with what parameters.
- **`GuardRails` is Gemini-only** (`GeminiGuardRails`) — OpenAI/Anthropic actions have no guard-rail implementation.

### 4. 🧪 Testing & Quality

- **`PredictionLoader` singleton makes unit tests fragile** — 5 tests are permanently disabled because of singleton state bleed between tests. Proper scoping or a clean reset mechanism would fix them all.
- **No integration test harness** — the regression tests in `com.t4a.regression` all require live API keys. A contract-test / mock-LLM layer would enable true CI with no credentials.

### 5. 🔌 Extensibility

- **`ExtendedPredictionLoader` + `@ActivateLoader` are very powerful but underdocumented** — custom action loaders auto-discovered by annotation is a great pattern that needs more examples.
- **No built-in retry / fallback** — if the LLM picks the wrong action or parameter extraction fails, there is no retry strategy.
- **`SwaggerPredictionLoader` silently swallows parse errors** — many `catch (Exception e) { log.warn(...) }` blocks do not surface which endpoints failed to load.

### 6. 🌐 Ecosystem

- **No Spring Boot auto-configuration** (`spring.factories` / `@AutoConfiguration`) — Spring users have to wire it manually. A `tools4ai-spring-boot-starter` artifact would significantly drive adoption.
- **No MCP server implementation** — the README mentions MCP protocol support but there is no MCP server/client in the codebase yet.
- **No metrics** — no Micrometer integration for action execution latency, error rates, or LLM token usage per action.
