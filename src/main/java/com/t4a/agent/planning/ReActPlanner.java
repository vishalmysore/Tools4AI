package com.t4a.agent.planning;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * ReAct (Reason + Act) planning loop for multi-step agentic tasks.
 *
 * <p>Each iteration of the loop:
 * <ol>
 *   <li><strong>Reason</strong> — asks the LLM what to do next given the goal and all prior
 *       observations.  The LLM replies with a structured block:
 *       <pre>
 *       THOUGHT: &lt;reasoning&gt;
 *       ACTION: &lt;prompt to send to the action processor, or DONE&gt;
 *       </pre>
 *   </li>
 *   <li><strong>Act</strong> — sends the ACTION text to the wrapped {@link AIProcessor} and
 *       captures the result as the observation for the next iteration.</li>
 *   <li><strong>Observe</strong> — appends the observation to the running context and loops.</li>
 * </ol>
 *
 * <p>The loop terminates when the LLM emits {@code ACTION: DONE} or when {@code maxIterations}
 * is reached.  A final synthesis prompt then produces the human-readable answer.
 *
 * <p>Example:
 * <pre>{@code
 * ReActPlanner planner = new ReActPlanner(new OpenAiActionProcessor());
 * ExecutionPlan plan = planner.plan(
 *     "Find the cheapest flight from Toronto to Bangalore next month and book it");
 * System.out.println(plan.getFinalAnswer());
 * plan.getSteps().forEach(System.out::println);
 * }</pre>
 */
@Slf4j
public class ReActPlanner {

    public static final int DEFAULT_MAX_ITERATIONS = 10;

    private static final String THOUGHT_PREFIX = "THOUGHT:";
    private static final String ACTION_PREFIX  = "ACTION:";
    private static final String DONE_SIGNAL    = "DONE";

    private final AIProcessor processor;
    private final int maxIterations;

    public ReActPlanner(AIProcessor processor) {
        this(processor, DEFAULT_MAX_ITERATIONS);
    }

    public ReActPlanner(AIProcessor processor, int maxIterations) {
        if (processor == null)    throw new IllegalArgumentException("processor must not be null");
        if (maxIterations <= 0)   throw new IllegalArgumentException("maxIterations must be > 0");
        this.processor     = processor;
        this.maxIterations = maxIterations;
    }

    /**
     * Execute the ReAct loop for the given {@code goal}.
     *
     * @param goal natural-language description of what needs to be accomplished
     * @return a fully populated {@link ExecutionPlan} with every thought/action/observation trace
     *         and a final synthesised answer
     */
    public ExecutionPlan plan(String goal) throws AIProcessingException {
        ExecutionPlan plan = new ExecutionPlan(goal);
        StringBuilder context = new StringBuilder(buildSystemPrompt(goal));

        log.info("ReActPlanner starting for goal: {}", goal);

        for (int i = 1; i <= maxIterations; i++) {
            PlanStep step = new PlanStep(i);
            log.debug("ReAct iteration {}/{}", i, maxIterations);

            // ---- REASON ----
            String reasonResponse;
            try {
                reasonResponse = processor.query(context.toString());
            } catch (AIProcessingException e) {
                step.setFailed(true);
                step.setFailureReason("Reason call failed: " + e.getMessage());
                plan.addStep(step);
                log.error("Reason step {} failed: {}", i, e.getMessage());
                break;
            }

            String thought      = extractSection(reasonResponse, THOUGHT_PREFIX);
            String actionPrompt = extractSection(reasonResponse, ACTION_PREFIX);
            step.setThought(thought);
            step.setActionPrompt(actionPrompt);
            log.info("Step {} THOUGHT: {}", i, thought);
            log.info("Step {} ACTION:  {}", i, actionPrompt);

            // ---- CHECK FOR DONE ----
            if (actionPrompt == null || actionPrompt.trim().equalsIgnoreCase(DONE_SIGNAL)) {
                step.setDone(true);
                plan.addStep(step);
                log.info("ReAct loop signalled DONE at step {}", i);
                break;
            }

            // ---- ACT ----
            Object observation;
            try {
                observation = processor.processSingleAction(actionPrompt);
            } catch (AIProcessingException e) {
                observation = "ERROR: " + e.getMessage();
                step.setFailed(true);
                step.setFailureReason(e.getMessage());
                log.warn("Action step {} failed: {}", i, e.getMessage());
            }
            step.setObservation(observation);
            plan.addStep(step);
            log.info("Step {} OBSERVATION: {}", i, observation);

            // ---- APPEND TO CONTEXT ----
            context.append("\nStep ").append(i).append(":\n")
                   .append("THOUGHT: ").append(thought).append('\n')
                   .append("ACTION: ").append(actionPrompt).append('\n')
                   .append("OBSERVATION: ").append(observation).append('\n');
        }

        // ---- FINAL SYNTHESIS ----
        String finalAnswer = synthesise(goal, plan, context.toString());
        plan.setFinalAnswer(finalAnswer);
        log.info("ReActPlanner completed. Steps={}, finalAnswer='{}'",
                plan.getTotalSteps(), finalAnswer);
        return plan;
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private String buildSystemPrompt(String goal) {
        return "You are a ReAct agent. Your goal is: " + goal + "\n\n" +
               "At each step respond with exactly two labelled lines:\n" +
               "THOUGHT: <your reasoning about what to do next>\n" +
               "ACTION: <the task description to execute, or the word DONE if the goal is achieved>\n\n" +
               "Be concise. Do not add extra text outside these two lines.\n";
    }

    private String synthesise(String goal, ExecutionPlan plan, String context) {
        if (plan.getSteps().isEmpty()) return "No steps were executed.";
        String synthPrompt = context +
                "\nNow provide a final, concise answer to the original goal: " + goal;
        try {
            return processor.query(synthPrompt);
        } catch (AIProcessingException e) {
            log.warn("Final synthesis failed: {}", e.getMessage());
            // Fall back: concatenate the last observation
            if (!plan.getSteps().isEmpty()) {
                Object lastObs = plan.getSteps().get(plan.getSteps().size() - 1).getObservation();
                return lastObs != null ? lastObs.toString() : "No answer produced.";
            }
            return "No answer produced.";
        }
    }

    /**
     * Extract the text after a labelled prefix (e.g. {@code "THOUGHT:"}) from a multi-line string.
     * Returns {@code null} if the prefix is not found.
     */
    static String extractSection(String text, String prefix) {
        if (text == null) return null;
        for (String line : text.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (trimmed.toUpperCase().startsWith(prefix.toUpperCase())) {
                return trimmed.substring(prefix.length()).trim();
            }
        }
        return null;
    }
}
