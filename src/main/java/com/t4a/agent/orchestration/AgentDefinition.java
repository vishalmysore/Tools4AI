package com.t4a.agent.orchestration;

import com.t4a.processor.AIProcessor;
import lombok.Getter;

/**
 * Describes a named agent that can be registered with an {@link AgentOrchestrator}.
 *
 * <p>The {@code description} is shown to the routing LLM so it can decide which agent
 * should handle a given sub-task.  Write it like a sentence that completes:
 * <em>"Use this agent when the task involves …"</em>
 *
 * <pre>{@code
 * AgentDefinition flightAgent = new AgentDefinition(
 *     "flightAgent",
 *     "Use this agent when the task involves booking or checking flights",
 *     new OpenAiActionProcessor()
 * );
 * }</pre>
 */
@Getter
public class AgentDefinition {

    private final String name;
    private final String description;
    private final AIProcessor processor;

    public AgentDefinition(String name, String description, AIProcessor processor) {
        if (name == null || name.isBlank())        throw new IllegalArgumentException("Agent name must not be blank");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Agent description must not be blank");
        if (processor == null)                     throw new IllegalArgumentException("Processor must not be null");
        this.name        = name;
        this.description = description;
        this.processor   = processor;
    }

    @Override
    public String toString() {
        return "AgentDefinition{name='" + name + "', description='" + description + "'}";
    }
}
