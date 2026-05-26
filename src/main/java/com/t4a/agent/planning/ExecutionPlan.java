package com.t4a.agent.planning;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The full trace of a {@link ReActPlanner} run — every thought/action/observation triple
 * plus the final synthesised answer.
 */
@Getter
public class ExecutionPlan {

    private final String goal;
    private final List<PlanStep> steps = new ArrayList<>();
    private String finalAnswer;
    private boolean completed;
    private int totalSteps;

    public ExecutionPlan(String goal) {
        this.goal = goal;
    }

    void addStep(PlanStep step) {
        steps.add(step);
        totalSteps = steps.size();
    }

    void setFinalAnswer(String answer) {
        this.finalAnswer = answer;
        this.completed = true;
    }

    /** Unmodifiable view of all steps. */
    public List<PlanStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    @Override
    public String toString() {
        return "ExecutionPlan{goal='" + goal + "', steps=" + totalSteps
                + ", completed=" + completed + ", finalAnswer='" + finalAnswer + "'}";
    }
}
