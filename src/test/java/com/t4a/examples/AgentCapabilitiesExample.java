package com.t4a.examples;

import com.t4a.agent.feedback.ToolResultFeedbackProcessor;
import com.t4a.agent.memory.AgentMemory;
import com.t4a.agent.memory.InMemoryAgentMemory;
import com.t4a.agent.memory.PersistentFileAgentMemory;
import com.t4a.agent.orchestration.AgentDefinition;
import com.t4a.agent.orchestration.AgentOrchestrator;
import com.t4a.agent.orchestration.OrchestrationResult;
import com.t4a.agent.planning.ExecutionPlan;
import com.t4a.agent.planning.PlanStep;
import com.t4a.agent.planning.ReActPlanner;
import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.OpenAiActionProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * End-to-end example showcasing the four new agentic capabilities added to Tools4AI:
 *
 * <ol>
 *   <li><b>Memory</b> — short-term {@link InMemoryAgentMemory} and long-term
 *       {@link PersistentFileAgentMemory} give the agent context across turns.</li>
 *   <li><b>Multi-agent orchestration</b> — {@link AgentOrchestrator} routes a complex goal
 *       to the right specialist agents and synthesises a unified answer.</li>
 *   <li><b>ReAct planning loop</b> — {@link ReActPlanner} iterates Reason→Act→Observe cycles
 *       until the goal is achieved or the max-iteration limit is hit.</li>
 *   <li><b>Tool result feedback</b> — {@link ToolResultFeedbackProcessor} wraps any processor
 *       and feeds the raw Java return value back to the LLM for a natural-language answer.</li>
 * </ol>
 *
 * <p><b>Prerequisites:</b> set {@code openAiKey} in {@code tools4ai.properties}
 * (or pass {@code -DopenAiKey=sk-...} at runtime).  All four demos use the same
 * {@link OpenAiActionProcessor}; swap in {@code GeminiV2ActionProcessor} or
 * {@code AnthropicActionProcessor} if you prefer a different provider.
 *
 * <p>Run any individual demo via its static method, or call {@link #main} to run all four
 * in sequence.
 */
@Slf4j
public class AgentCapabilitiesExample {

    // =========================================================================
    // Sample @Agent classes used by the demos
    // =========================================================================

    @Agent(groupName = "food", groupDescription = "food and cooking tasks")
    public static class FoodAction {
        @Action(description = "what is the food preference of this person")
        public String whatFoodDoesThisPersonLike(String name) {
            if ("vishal".equalsIgnoreCase(name)) return "Paneer Butter Masala";
            if ("vinod".equalsIgnoreCase(name))  return "Aloo Kofta";
            return "something yummy";
        }
    }

    @Agent(groupName = "travel", groupDescription = "flight and hotel booking tasks")
    public static class TravelAction {
        @Action(description = "book a flight between two cities on a given date")
        public String bookFlight(String fromCity, String toCity, String date) {
            return "Flight " + fromCity + "→" + toCity + " on " + date + " confirmed (PNR: XY42)";
        }
    }

    @Agent(groupName = "hotel", groupDescription = "hotel reservation tasks")
    public static class HotelAction {
        @Action(description = "reserve a hotel room in a city for a number of nights")
        public String reserveHotel(String city, int nights) {
            return "Hotel in " + city + " for " + nights + " nights booked (Ref: HTL99)";
        }
    }

    // =========================================================================
    // Demo 1 — Memory
    // =========================================================================

    /**
     * Shows how to maintain conversation context across multiple turns so the agent
     * does not start from a blank slate on every call.
     *
     * <p>Short-term memory (in-process, bounded window) is useful for a single session.
     * Long-term memory (JSON file) survives JVM restarts and is loaded back on the next run.
     */
    public static void demoMemory() throws AIProcessingException {
        log.info("=== Demo 1: Memory ===");
        OpenAiActionProcessor processor = new OpenAiActionProcessor();

        // --- Short-term: in-memory sliding window (last 20 turns) ---
        AgentMemory shortTerm = new InMemoryAgentMemory(20);

        String prompt1 = "My friend Vishal is coming for dinner — what should I cook?";
        Object result1 = processor.processSingleAction(
                shortTerm.getHistoryAsContext() + prompt1,
                new FoodAction(), "whatFoodDoesThisPersonLike");
        shortTerm.addTurn(prompt1, result1);
        log.info("Turn 1 result: {}", result1);

        // Second turn: the agent has context from turn 1
        String prompt2 = "What ingredients do I need for that dish?";
        Object result2 = processor.query(shortTerm.getHistoryAsContext() + prompt2);
        shortTerm.addTurn(prompt2, result2);
        log.info("Turn 2 result (with context): {}", result2);

        log.info("Memory size after 2 turns: {}", shortTerm.size());

        // --- Long-term: file-backed, persists across restarts ---
        AgentMemory longTerm = new PersistentFileAgentMemory("/tmp/tools4ai-session.json");
        longTerm.addTurn(prompt1, result1);
        longTerm.addTurn(prompt2, result2);
        log.info("Persistent memory saved — {} turns on disk", longTerm.size());

        // Simulate a restart by loading it fresh
        AgentMemory reloaded = new PersistentFileAgentMemory("/tmp/tools4ai-session.json");
        log.info("Reloaded {} turns from disk", reloaded.size());
        log.info("Context loaded from file:\n{}", reloaded.getHistoryAsContext());
    }

    // =========================================================================
    // Demo 2 — Multi-agent orchestration
    // =========================================================================

    /**
     * Shows how to route a complex, multi-domain goal to specialised agents and collect
     * a single synthesised answer.
     *
     * <p>The orchestrator asks the LLM which registered agents should handle which part of
     * the prompt, delegates each sub-task, then asks the LLM to merge the results.
     */
    public static void demoOrchestration() throws AIProcessingException {
        log.info("=== Demo 2: Multi-agent orchestration ===");

        OpenAiActionProcessor router        = new OpenAiActionProcessor();
        OpenAiActionProcessor flightProc    = new OpenAiActionProcessor();
        OpenAiActionProcessor hotelProc     = new OpenAiActionProcessor();

        AgentOrchestrator orchestrator = new AgentOrchestrator(router)
                .register(new AgentDefinition(
                        "flightAgent",
                        "Use this agent when the task involves booking or checking flights",
                        flightProc))
                .register(new AgentDefinition(
                        "hotelAgent",
                        "Use this agent when the task involves hotel or accommodation reservations",
                        hotelProc));

        String goal = "Book a flight from Toronto to Bangalore on August 15 " +
                      "and a hotel near MG Road for 3 nights starting the same day";

        OrchestrationResult result = orchestrator.execute(goal);

        log.info("Orchestration successful: {}", result.isSuccessful());
        result.getResults().forEach((agent, res) ->
                log.info("  {} → {}", agent, res));
        log.info("Synthesised answer: {}", result.getSummary());
    }

    // =========================================================================
    // Demo 3 — ReAct planning loop
    // =========================================================================

    /**
     * Shows how the ReAct (Reason + Act + Observe) loop tackles a multi-step goal by
     * iteratively reasoning about what to do, executing one action, observing the result,
     * and deciding the next step — until it signals {@code DONE}.
     */
    public static void demoReActPlanning() throws AIProcessingException {
        log.info("=== Demo 3: ReAct planning loop ===");

        OpenAiActionProcessor processor = new OpenAiActionProcessor();

        // Default max 10 iterations; set to 5 here to keep the demo short
        ReActPlanner planner = new ReActPlanner(processor, 5);

        String goal = "Find out what Vishal likes to eat, then suggest a restaurant " +
                      "in Toronto that serves that cuisine and book a table for 2";

        ExecutionPlan plan = planner.plan(goal);

        log.info("Plan completed: {} steps", plan.getTotalSteps());
        for (PlanStep step : plan.getSteps()) {
            log.info("  Step {} | THOUGHT: {} | ACTION: {} | OBSERVATION: {}",
                    step.getStepNumber(), step.getThought(),
                    step.getActionPrompt(), step.getObservation());
        }
        log.info("Final answer: {}", plan.getFinalAnswer());
    }

    // =========================================================================
    // Demo 4 — Tool result feedback
    // =========================================================================

    /**
     * Shows how {@link ToolResultFeedbackProcessor} turns a bare Java return value into a
     * natural-language sentence by feeding it back to the LLM after the action executes.
     *
     * <p>Without the decorator, {@code processSingleAction} returns exactly what the
     * {@code @Action} method returns — e.g. the raw string {@code "Paneer Butter Masala"}.
     * With the decorator, the LLM uses both the original prompt and that raw value to
     * compose a proper answer like <em>"Your friend Vishal loves Paneer Butter Masala —
     * you could prepare that for dinner tonight!"</em>
     */
    public static void demoToolResultFeedback() throws AIProcessingException {
        log.info("=== Demo 4: Tool result feedback ===");

        OpenAiActionProcessor raw     = new OpenAiActionProcessor();
        ToolResultFeedbackProcessor enriched = new ToolResultFeedbackProcessor(raw);

        String prompt = "My friend Vishal is coming over — what should I cook for him?";

        // Without feedback: returns the bare method return value
        Object bareResult = raw.processSingleAction(prompt, new FoodAction(),
                "whatFoodDoesThisPersonLike");
        log.info("Without feedback : {}", bareResult);

        // With feedback: the LLM synthesises a natural-language sentence
        Object enrichedResult = enriched.processSingleAction(prompt, new FoodAction(),
                "whatFoodDoesThisPersonLike");
        log.info("With feedback    : {}", enrichedResult);
    }

    // =========================================================================
    // Main — run all four demos
    // =========================================================================

    public static void main(String[] args) throws AIProcessingException {
        demoMemory();
        demoOrchestration();
        demoReActPlanning();
        demoToolResultFeedback();
    }
}
