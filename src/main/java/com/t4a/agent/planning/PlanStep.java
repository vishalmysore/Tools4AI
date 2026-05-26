package com.t4a.agent.planning;

import lombok.Getter;
import lombok.Setter;

/**
 * One step in a ReAct execution plan.
 *
 * <p>Follows the Reason→Act→Observe cycle:
 * <ul>
 *   <li>{@code thought} — the LLM's reasoning about what to do next</li>
 *   <li>{@code actionPrompt} — the prompt sent to the action processor</li>
 *   <li>{@code observation} — the result returned by the action processor</li>
 * </ul>
 */
@Getter
@Setter
public class PlanStep {

    private final int stepNumber;
    private String thought;
    private String actionPrompt;
    private Object observation;
    private boolean done;
    private boolean failed;
    private String failureReason;

    public PlanStep(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    @Override
    public String toString() {
        return "PlanStep{step=" + stepNumber
                + ", thought='" + thought + '\''
                + ", actionPrompt='" + actionPrompt + '\''
                + ", observation=" + observation
                + ", done=" + done + '}';
    }
}
