package com.t4a.agent.orchestration;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * First-class multi-agent orchestration framework.
 *
 * <p>Register any number of {@link AgentDefinition}s, then call {@link #execute(String)} with a
 * high-level goal.  The orchestrator:
 * <ol>
 *   <li>Uses a <em>router</em> {@link AIProcessor} to decide which registered agents should
 *       handle which part of the goal (LLM-driven routing).</li>
 *   <li>Invokes each selected agent's {@code processSingleAction} with its sub-task.</li>
 *   <li>Optionally asks the router to synthesise a final answer from all agent results.</li>
 * </ol>
 *
 * <p>Example:
 * <pre>{@code
 * AgentOrchestrator orchestrator = new AgentOrchestrator(new OpenAiActionProcessor());
 * orchestrator.register(new AgentDefinition("flights", "handles flight booking", flightProcessor));
 * orchestrator.register(new AgentDefinition("hotels", "handles hotel reservations", hotelProcessor));
 *
 * OrchestrationResult result = orchestrator.execute(
 *     "Book a flight to Bangalore on Aug 15 and a hotel near MG Road for 3 nights");
 * System.out.println(result.getSummary());
 * }</pre>
 */
@Slf4j
public class AgentOrchestrator {

    private final AIProcessor router;
    private final Map<String, AgentDefinition> agents = new LinkedHashMap<>();
    private boolean synthesisEnabled = true;

    /**
     * @param router the {@link AIProcessor} used to route the goal to the right agents and
     *               optionally synthesise the final answer.
     */
    public AgentOrchestrator(AIProcessor router) {
        if (router == null) throw new IllegalArgumentException("router must not be null");
        this.router = router;
    }

    /** Register an agent.  Agents are tried in registration order if LLM routing is ambiguous. */
    public AgentOrchestrator register(AgentDefinition definition) {
        agents.put(definition.getName(), definition);
        log.info("Registered agent '{}'", definition.getName());
        return this;
    }

    /**
     * Disable the final synthesis step.  The {@link OrchestrationResult} will contain raw
     * per-agent results but {@link OrchestrationResult#getSummary()} will be {@code null}.
     */
    public AgentOrchestrator withoutSynthesis() {
        this.synthesisEnabled = false;
        return this;
    }

    /**
     * Execute the orchestration for the given {@code goal}.
     *
     * @param goal a natural-language description of the overall task
     * @return an {@link OrchestrationResult} containing per-agent results and an optional summary
     */
    public OrchestrationResult execute(String goal) throws AIProcessingException {
        if (agents.isEmpty()) throw new IllegalStateException("No agents registered");

        OrchestrationResult orchestrationResult = new OrchestrationResult(goal);
        Map<String, String> routing = route(goal);

        for (Map.Entry<String, String> entry : routing.entrySet()) {
            String agentName = entry.getKey();
            String subTask   = entry.getValue();
            AgentDefinition def = agents.get(agentName);
            if (def == null) {
                log.warn("Router returned unknown agent name '{}' — skipping", agentName);
                continue;
            }
            log.info("Delegating sub-task to agent '{}': {}", agentName, subTask);
            try {
                Object result = def.getProcessor().processSingleAction(subTask);
                orchestrationResult.addResult(agentName, result);
                log.info("Agent '{}' returned: {}", agentName, result);
            } catch (AIProcessingException e) {
                log.error("Agent '{}' failed: {}", agentName, e.getMessage());
                orchestrationResult.markFailed(agentName, e);
            }
        }

        if (synthesisEnabled && !orchestrationResult.getResults().isEmpty()) {
            String summary = synthesise(goal, orchestrationResult.getResults());
            orchestrationResult.setSummary(summary);
        }

        return orchestrationResult;
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Ask the router LLM which agents should handle the goal.
     * Returns a map of agentName → sub-task prompt.
     */
    private Map<String, String> route(String goal) {
        String agentList = buildAgentListDescription();
        String routingPrompt =
                "You are an orchestrator. Given the following goal and agent list, decide which agents " +
                "should be involved and what sub-task each should perform. " +
                "Reply ONLY as a plain list, one agent per line, in the format: " +
                "agentName|sub-task description\n\n" +
                "Goal: " + goal + "\n\n" +
                "Available agents:\n" + agentList;

        Map<String, String> routing = new LinkedHashMap<>();
        try {
            String response = router.query(routingPrompt);
            log.debug("Router response:\n{}", response);
            for (String line : response.split("\\r?\\n")) {
                line = line.trim();
                if (line.isEmpty()) continue;
                int sep = line.indexOf('|');
                if (sep < 0) continue;
                String name    = line.substring(0, sep).trim();
                String subTask = line.substring(sep + 1).trim();
                if (agents.containsKey(name)) {
                    routing.put(name, subTask);
                } else {
                    log.warn("Router mentioned unknown agent '{}' — ignoring", name);
                }
            }
        } catch (AIProcessingException e) {
            log.error("Routing failed ({}), falling back: all agents get the full goal", e.getMessage());
            for (String name : agents.keySet()) {
                routing.put(name, goal);
            }
        }

        // Fallback: if router returned nothing usable, send goal to every agent
        if (routing.isEmpty()) {
            log.warn("Router produced no valid routing — sending goal to all agents");
            for (String name : agents.keySet()) {
                routing.put(name, goal);
            }
        }
        return routing;
    }

    /** Ask the router to produce a human-readable synthesis of all agent results. */
    private String synthesise(String goal, Map<String, Object> results) {
        StringBuilder sb = new StringBuilder(
                "You are an orchestrator. Synthesise the following agent results into a single, " +
                "coherent answer for the user.\n\nOriginal goal: " + goal + "\n\nAgent results:\n");
        for (Map.Entry<String, Object> e : results.entrySet()) {
            sb.append("- ").append(e.getKey()).append(": ").append(e.getValue()).append('\n');
        }
        try {
            return router.query(sb.toString());
        } catch (AIProcessingException ex) {
            log.warn("Synthesis failed: {}", ex.getMessage());
            return results.toString();
        }
    }

    private String buildAgentListDescription() {
        StringBuilder sb = new StringBuilder();
        for (AgentDefinition def : agents.values()) {
            sb.append(def.getName()).append(": ").append(def.getDescription()).append('\n');
        }
        return sb.toString();
    }

    /** Returns registered agent names for inspection/testing. */
    public List<String> getRegisteredAgentNames() {
        return new ArrayList<>(agents.keySet());
    }
}
