# Tools4AI Tutorial: Build Java AI Agents Without Rewriting Your Application

> **Tools4AI** (v1.2.1 · Maven Central) is a pure-Java agentic AI framework that lets you
> turn *any existing Java method* into an AI-callable action by adding two annotations.
> No Spring context required. No manual function schemas. No glue code.

---

## Table of Contents

1. [How Tools4AI Is Different from Other AI Frameworks](#1-how-tools4ai-is-different)
2. [Prerequisites & Setup](#2-prerequisites--setup)
3. [Example 1 — Route a Natural-Language Prompt to a Java Method](#3-example-1--route-a-natural-language-prompt-to-a-java-method)
4. [Example 2 — Extract a Complex POJO from Free Text](#4-example-2--extract-a-complex-pojo-from-free-text)
5. [Example 3 — Multi-Step Agent with Memory](#5-example-3--multi-step-agent-with-memory)
6. [Example 4 — Multi-Agent Orchestration](#6-example-4--multi-agent-orchestration)
7. [Example 5 — ReAct Planning Loop](#7-example-5--react-planning-loop)
8. [Choosing an LLM Provider](#8-choosing-an-llm-provider)
9. [Next Steps](#9-next-steps)

---

## 1. How Tools4AI Is Different

### The core problem it solves

Most enterprise Java applications already have *years* of business logic encoded in Java
classes and methods.  Adding AI to those apps today usually means one of two paths:

* **Rewrite** — migrate to a new AI-first framework (expensive, risky).
* **Wrap** — build a translation layer that manually maps LLM outputs to existing methods
  (tedious, fragile, and hard to maintain as the codebase evolves).

**Tools4AI takes a third path: annotate what you already have.**

```java
// BEFORE — plain Java service, no AI awareness
public class CustomerService {
    public String raiseComplaint(String customerId, String description) { ... }
}

// AFTER — AI-callable with two annotations, zero other changes
@Agent(groupName = "support", groupDescription = "customer support tasks")
public class CustomerService {
    @Action(description = "raise a support complaint for a customer")
    public String raiseComplaint(String customerId, String description) { ... }
}
```

That's it.  The framework scans the classpath, discovers every `@Action` method, builds a
function-calling schema automatically, and at runtime asks the LLM which method to invoke and
what parameters to extract from the user's natural-language prompt.

### Tools4AI vs Spring AI — key differences

| What you care about | Tools4AI | Spring AI |
|---|---|---|
| **Who it's for** | Developers retrofitting AI onto existing Java apps | Developers building new AI-native Spring Boot apps |
| **Spring required?** | ❌ No — works with plain Java | ✅ Yes — Spring context required |
| **How actions are registered** | Classpath scan of `@Agent`/`@Action` annotations | Manual `@Tool` / function-callback registration |
| **Parameter extraction** | Fully automatic — LLM fills POJOs, Lists, Maps | You define schemas by hand |
| **Non-Java actions** | Shell scripts, HTTP REST, Swagger/OpenAPI — no code needed | Not supported natively |
| **Safety layer** | Built-in `GuardRails`, `HumanInLoop`, hallucination & bias detectors | Not built in |
| **Agent memory** | `InMemoryAgentMemory` (session) + `PersistentFileAgentMemory` (cross-restart) | Not built in |
| **Multi-agent orchestration** | `AgentOrchestrator` with LLM-driven routing | Not built in |
| **RAG / vector stores** | ❌ Not in scope | ✅ Core feature |
| **Weight** | Single lightweight JAR | Full Spring ecosystem |

> **One-line summary:** Spring AI is a *platform* for building AI-first apps from scratch.
> Tools4AI is a *retrofit layer* that makes any existing Java system AI-controllable with
> minimal code change.

---

## 2. Prerequisites & Setup

### Maven dependency

```xml
<dependency>
    <groupId>io.github.vishalmysore</groupId>
    <artifactId>tools4ai</artifactId>
    <version>1.2.1</version>
</dependency>
```

### tools4ai.properties

Create `src/main/resources/tools4ai.properties` and add your LLM key:

```properties
# OpenAI
openAiKey=sk-...

# OR Google Gemini (Vertex AI)
# googleProjectId=my-gcp-project
# googleLocation=us-central1

# OR Anthropic Claude
# anthropicApiKey=sk-ant-...
```

The framework picks the provider automatically based on which key is present.

---

## 3. Example 1 — Route a Natural-Language Prompt to a Java Method

**Use-case:** A user types a free-text message.  Without any routing code, Tools4AI figures out
which Java method to call and what arguments to pass.

### Step 1 — annotate your action class

```java
import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;

@Agent(groupName = "food", groupDescription = "all tasks related to cooking and food")
public class CookingAction {

    @Action(description = "suggest a recipe given a list of ingredients")
    public String suggestRecipe(String ingredients) {
        // Your real business logic here
        return "You can make a delicious dish with: " + ingredients;
    }

    @Action(description = "what food does a specific person like to eat")
    public String whatFoodDoesThisPersonLike(String name) {
        if ("vishal".equalsIgnoreCase(name)) return "Paneer Butter Masala";
        if ("vinod".equalsIgnoreCase(name))  return "Aloo Kofta";
        return "something yummy";
    }
}
```

### Step 2 — call the processor

```java
import com.t4a.processor.OpenAiActionProcessor;

OpenAiActionProcessor processor = new OpenAiActionProcessor();

// The framework automatically picks whatFoodDoesThisPersonLike("Vishal")
Object result = processor.processSingleAction(
        "My friend Vishal is coming for dinner — what should I cook?");

System.out.println(result); // → "Paneer Butter Masala"
```

**What just happened?**

1. The processor sent the prompt + all registered `@Action` descriptions to the LLM.
2. The LLM replied with: "call `whatFoodDoesThisPersonLike` with `name = Vishal`".
3. Tools4AI invoked the method via reflection and returned the Java result.

No routing code.  No schema definition.  No string parsing.

---

## 4. Example 2 — Extract a Complex POJO from Free Text

**Use-case:** Turn an unstructured paragraph into a typed Java object — useful for intake
forms, email parsing, chatbot data collection, and customer support triage.

### Define your POJO

```java
import com.t4a.annotations.Prompt;
import lombok.*;
import java.util.Date;

@Getter @Setter @NoArgsConstructor
public class Customer {

    private String firstName;
    private String lastName;

    @Prompt(describe = "convert the reason to a concise one-line summary")
    private String reasonForCalling;

    @Prompt(ignore = true)          // never populate this field from the prompt
    private String internalCaseId;

    @Prompt(dateFormat = "yyyy-MM-dd",
            describe = "if no date is mentioned, use today's date")
    private Date dateJoined;
}
```

### Use PromptTransformer

```java
import com.t4a.transform.OpenAIPromptTransformer;

OpenAIPromptTransformer transformer = new OpenAIPromptTransformer();

String prompt = "A customer named Vinod Gupta is calling from Toronto. "
              + "He joined on 12 May 2008 and his computer stopped working.";

Customer customer = (Customer) transformer.transformIntoPojo(
        prompt,
        Customer.class.getName(),
        "Customer",
        "Extract customer details");

System.out.println(customer.getFirstName());       // → Vinod
System.out.println(customer.getLastName());        // → Gupta
System.out.println(customer.getReasonForCalling());// → Computer not working (summarised)
System.out.println(customer.getInternalCaseId());  // → null  (ignored by @Prompt)
```

**Why this matters for enterprise Java:**
You get a fully typed, validated Java object from messy natural-language input — ready to
pass into your existing service layer without changing any downstream code.

---

## 5. Example 3 — Multi-Step Agent with Memory

**Use-case:** A support chatbot that remembers what was said earlier in the conversation so
the user doesn't have to repeat themselves on every message.

```java
import com.t4a.agent.memory.AgentMemory;
import com.t4a.agent.memory.InMemoryAgentMemory;
import com.t4a.agent.memory.PersistentFileAgentMemory;
import com.t4a.processor.OpenAiActionProcessor;

OpenAiActionProcessor processor = new OpenAiActionProcessor();

// ── Short-term memory (in-process, survives the session) ─────────────────────
AgentMemory memory = new InMemoryAgentMemory(20); // keep last 20 turns

// Turn 1
String prompt1 = "My friend Vishal is coming for dinner — what should I cook?";
Object result1 = processor.processSingleAction(
        memory.getHistoryAsContext() + prompt1,
        new CookingAction(), "whatFoodDoesThisPersonLike");
memory.addTurn(prompt1, result1);
// result1 → "Paneer Butter Masala"

// Turn 2 — agent knows the dish from turn 1 without being told again
String prompt2 = "What ingredients do I need for that dish?";
Object result2 = processor.query(memory.getHistoryAsContext() + prompt2);
memory.addTurn(prompt2, result2);
// result2 → "For Paneer Butter Masala you need paneer, tomatoes, cream, butter..."

System.out.println("Turns stored: " + memory.size()); // → 2

// ── Long-term memory (JSON file, survives JVM restarts) ──────────────────────
AgentMemory longTerm = new PersistentFileAgentMemory("/var/myapp/agent-session.json");
longTerm.addTurn(prompt1, result1);
longTerm.addTurn(prompt2, result2);

// Next day — reload
AgentMemory reloaded = new PersistentFileAgentMemory("/var/myapp/agent-session.json");
System.out.println("Turns reloaded: " + reloaded.size());     // → 2
System.out.println(reloaded.getHistoryAsContext());           // prints both turns
```

**When to use each:**

| Memory type | Best for |
|---|---|
| `InMemoryAgentMemory` | Single-session chatbots, request-scoped agents, unit tests |
| `PersistentFileAgentMemory` | Long-running assistants, CLI tools, agents that restart between tasks |

---

## 6. Example 4 — Multi-Agent Orchestration

**Use-case:** A travel assistant that needs to book a flight *and* a hotel.  Instead of one
giant catch-all agent, you give each specialist agent a focused job, and the orchestrator
routes sub-tasks automatically.

```java
import com.t4a.agent.orchestration.*;
import com.t4a.processor.OpenAiActionProcessor;

// Each agent gets its own processor and a one-sentence capability description
AgentOrchestrator orchestrator = new AgentOrchestrator(new OpenAiActionProcessor())
    .register(new AgentDefinition(
        "flightAgent",
        "Use this agent when the task involves booking or querying flights",
        new OpenAiActionProcessor()))
    .register(new AgentDefinition(
        "hotelAgent",
        "Use this agent when the task involves hotel or accommodation reservations",
        new OpenAiActionProcessor()));

String goal = "Book a flight from Toronto to Bangalore on August 15 "
            + "and reserve a hotel near MG Road for 3 nights.";

OrchestrationResult result = orchestrator.execute(goal);

// Per-agent results
result.getResults().forEach((agent, res) ->
        System.out.printf("  %-15s → %s%n", agent, res));

// LLM-synthesised unified answer
System.out.println("\nSummary: " + result.getSummary());

// → "Your flight from Toronto to Bangalore on August 15 is confirmed (PNR: XY42)
//    and your hotel near MG Road is reserved for 3 nights (Ref: HTL99)."
```

**How the routing works:**

1. The orchestrator sends the goal + agent descriptions to the router LLM.
2. The LLM replies with `agentName|sub-task` lines (one per agent to invoke).
3. Each agent receives its sub-task and runs independently.
4. Results are collected, then the router LLM synthesises a single human-readable answer.

You can disable synthesis if you only need the raw per-agent results:

```java
orchestrator.withoutSynthesis();
```

---

## 7. Example 5 — ReAct Planning Loop

**Use-case:** A goal that requires multiple steps decided *at runtime* — the agent reasons
about what to do next, acts, observes the result, and repeats until done.

This follows the **ReAct** (Reason + Act) pattern, a well-established technique for
autonomous agent loops.

```java
import com.t4a.agent.planning.*;
import com.t4a.processor.OpenAiActionProcessor;

ReActPlanner planner = new ReActPlanner(
        new OpenAiActionProcessor(),
        5);  // max 5 iterations — prevents runaway loops

String goal = "Find out what Vishal likes to eat, then suggest a restaurant "
            + "in Toronto that serves that cuisine and book a table for two.";

ExecutionPlan plan = planner.plan(goal);

// Inspect every Reason→Act→Observe step
for (PlanStep step : plan.getSteps()) {
    System.out.printf("Step %d%n", step.getStepNumber());
    System.out.printf("  THOUGHT      : %s%n", step.getThought());
    System.out.printf("  ACTION PROMPT: %s%n", step.getActionPrompt());
    System.out.printf("  OBSERVATION  : %s%n", step.getObservation());
}

// Final synthesised answer
System.out.println("\nFinal answer: " + plan.getFinalAnswer());
```

**Sample output:**

```
Step 1
  THOUGHT      : I need to find out Vishal's food preference first.
  ACTION PROMPT: What food does Vishal like?
  OBSERVATION  : Paneer Butter Masala

Step 2
  THOUGHT      : Now I should find a Toronto restaurant serving North Indian cuisine.
  ACTION PROMPT: Find a restaurant in Toronto serving Paneer Butter Masala.
  OBSERVATION  : Kiran Palace, 155 King St W, Toronto

Final answer: Vishal loves Paneer Butter Masala. I recommend Kiran Palace at
155 King St W, Toronto. I have reserved a table for two — enjoy your dinner!
```

**When to use ReAct vs direct `processSingleAction`:**

| Scenario | Recommended approach |
|---|---|
| Single, well-defined task | `processSingleAction` |
| Multi-step task with known steps | `AgentOrchestrator` |
| Open-ended goal where steps emerge at runtime | `ReActPlanner` |

---

## 8. Choosing an LLM Provider

Tools4AI supports multiple providers — swap the processor class, keep everything else the same.

```java
// OpenAI (GPT-4o, GPT-4-turbo, …)
new OpenAiActionProcessor()

// Google Gemini via Vertex AI
new GeminiV2ActionProcessor()

// Anthropic Claude
new AnthropicActionProcessor()
```

All processors implement the same `AIProcessor` interface, so you can inject any of them and
your action classes never change.

### Spring Boot integration

If you are using Spring Boot, Spring-aware processor wrappers are available:

```java
@Bean
public AIProcessor aiProcessor() {
    return new SpringOpenAIProcessor();  // picks up Spring's application context
}
```

---

## 9. Next Steps

| Resource | Link |
|---|---|
| Rapid-start template | https://github.com/vishalmysore/agenticjava |
| Full API Javadocs | Published on each release via Maven Central |
| Architecture & Spring AI comparison | [ARCHITECTURE.md](../ARCHITECTURE.md) |
| HTTP REST & Shell actions (no Java needed) | See `http_actions.json` / `shell_actions.yaml` in the repo |
| Swagger/OpenAPI auto-discovery | `SwaggerPredictionLoader` — point at any OpenAPI spec |
| Safety & guardrails | `GuardRails`, `HumanInLoop`, `ZeroShotHallucinationDetector` |
| A2A / MCP / UCP protocols | See protocol integration docs in the repo |

---

### Keywords

*Java AI agent framework · agentic AI Java · LLM Java integration · OpenAI Java SDK alternative ·
Spring AI alternative · AI action routing Java · natural language to Java method · enterprise AI
automation · Java AI annotation · ReAct agent Java · multi-agent orchestration Java ·
Tools4AI tutorial · AI ADK Java*
