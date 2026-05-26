package com.t4a.agent.planning;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ReActPlannerTest {

    // ------------------------------------------------------------------
    // Minimal stub processor
    // ------------------------------------------------------------------

    static class ScriptedProcessor implements AIProcessor {
        private final String[] queryReplies;
        private final String   actionReply;
        private final AtomicInteger queryCallCount = new AtomicInteger(0);

        ScriptedProcessor(String actionReply, String... queryReplies) {
            this.actionReply  = actionReply;
            this.queryReplies = queryReplies;
        }

        @Override
        public String query(String p) throws AIProcessingException {
            int idx = queryCallCount.getAndIncrement();
            if (idx < queryReplies.length) return queryReplies[idx];
            return "THOUGHT: nothing more to do\nACTION: DONE";
        }

        @Override public Object processSingleAction(String p) { return actionReply; }
        @Override public Object processSingleAction(String p, ActionCallback cb) { return actionReply; }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e) { return actionReply; }
        @Override public Object processSingleAction(String p, HumanInLoop h, ExplainDecision e) { return actionReply; }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e, ActionCallback cb) { return actionReply; }
    }

    // ------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------

    @Test
    void testSingleStepThenDone() throws AIProcessingException {
        ScriptedProcessor proc = new ScriptedProcessor(
                "Weather is 25°C",
                "THOUGHT: I should check the weather\nACTION: What is the weather in Toronto?",
                "THOUGHT: I have the answer\nACTION: DONE",
                "The weather in Toronto is 25°C."  // synthesis call
        );

        ReActPlanner planner = new ReActPlanner(proc);
        ExecutionPlan plan = planner.plan("What is the weather in Toronto?");

        assertNotNull(plan.getFinalAnswer());
        assertTrue(plan.getTotalSteps() >= 1);
        assertTrue(plan.isCompleted());

        PlanStep first = plan.getSteps().get(0);
        assertEquals("I should check the weather", first.getThought());
        assertEquals("What is the weather in Toronto?", first.getActionPrompt());
        assertEquals("Weather is 25°C", first.getObservation());
    }

    @Test
    void testMaxIterationsRespected() throws AIProcessingException {
        // Every query reply is an action — never DONE
        ScriptedProcessor proc = new ScriptedProcessor(
                "some result",
                "THOUGHT: step\nACTION: do something",
                "THOUGHT: step\nACTION: do something",
                "THOUGHT: step\nACTION: do something",
                "THOUGHT: step\nACTION: do something",
                "THOUGHT: step\nACTION: do something",
                "final answer" // synthesis
        );

        ReActPlanner planner = new ReActPlanner(proc, 3);
        ExecutionPlan plan = planner.plan("some open-ended goal");

        // Should stop at 3 iterations (or fewer if a DONE slips in from default stub)
        assertTrue(plan.getTotalSteps() <= 3);
    }

    @Test
    void testExtractSection_found() {
        String text = "THOUGHT: this is my reasoning\nACTION: do the thing";
        assertEquals("this is my reasoning", ReActPlanner.extractSection(text, "THOUGHT:"));
        assertEquals("do the thing",         ReActPlanner.extractSection(text, "ACTION:"));
    }

    @Test
    void testExtractSection_notFound() {
        assertNull(ReActPlanner.extractSection("no labels here", "THOUGHT:"));
    }

    @Test
    void testExtractSection_nullInput() {
        assertNull(ReActPlanner.extractSection(null, "THOUGHT:"));
    }

    @Test
    void testNullProcessorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new ReActPlanner(null));
    }

    @Test
    void testZeroMaxIterationsThrows() {
        ScriptedProcessor proc = new ScriptedProcessor("r");
        assertThrows(IllegalArgumentException.class, () -> new ReActPlanner(proc, 0));
    }

    @Test
    void testPlanStepGettersSetters() {
        PlanStep step = new PlanStep(1);
        step.setThought("my thought");
        step.setActionPrompt("my action");
        step.setObservation("my obs");
        step.setDone(true);

        assertEquals(1,          step.getStepNumber());
        assertEquals("my thought",  step.getThought());
        assertEquals("my action",   step.getActionPrompt());
        assertEquals("my obs",      step.getObservation());
        assertTrue(step.isDone());
        assertFalse(step.isFailed());
    }

    @Test
    void testExecutionPlanFields() {
        ExecutionPlan plan = new ExecutionPlan("test goal");
        assertEquals("test goal", plan.getGoal());
        assertFalse(plan.isCompleted());
        assertEquals(0, plan.getTotalSteps());

        plan.addStep(new PlanStep(1));
        plan.setFinalAnswer("the answer");

        assertEquals(1, plan.getTotalSteps());
        assertTrue(plan.isCompleted());
        assertEquals("the answer", plan.getFinalAnswer());
    }
}
