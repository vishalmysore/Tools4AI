package com.t4a.regression;

import com.t4a.agent.feedback.ToolResultFeedbackProcessor;
import com.t4a.agent.memory.AgentMemory;
import com.t4a.agent.memory.InMemoryAgentMemory;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Regression tests for the four agentic capabilities added to Tools4AI:
 * memory, multi-agent orchestration, ReAct planning loop, and tool result feedback.
 *
 * <p>Follows the pattern of {@link OpenAIActionValidation}: each test uses a real
 * {@link OpenAiActionProcessor} and validates results with {@link TestHelperOpenAI}
 * where natural-language assertions are needed.
 */
@Slf4j
class AgentCapabilitiesValidation {

    // -----------------------------------------------------------------------
    // Helper @Agent class shared across tests
    // -----------------------------------------------------------------------

    @Agent(groupName = "food", groupDescription = "food and cooking tasks")
    public static class FoodAction {
        @Action(description = "what is the food preference of this person")
        public String whatFoodDoesThisPersonLike(String name) {
            if ("vishal".equalsIgnoreCase(name)) return "Paneer Butter Masala";
            if ("vinod".equalsIgnoreCase(name))  return "Aloo Kofta";
            return "something yummy";
        }
    }

    @Agent(groupName = "travel", groupDescription = "flight booking tasks")
    public static class TravelAction {
        @Action(description = "book a flight between two cities on a given date")
        public String bookFlight(String fromCity, String toCity, String date) {
            return "Flight " + fromCity + " to " + toCity + " on " + date + " confirmed (PNR: XY42)";
        }
    }

    @Agent(groupName = "hotel", groupDescription = "hotel reservation tasks")
    public static class HotelAction {
        @Action(description = "reserve a hotel room in a city for a number of nights")
        public String reserveHotel(String city, int nights) {
            return "Hotel in " + city + " for " + nights + " nights booked (Ref: HTL99)";
        }
    }

    // -----------------------------------------------------------------------
    // Test 1 — Memory: context carries across turns
    // -----------------------------------------------------------------------

    @Test
    void testMemoryContextPropagation() throws AIProcessingException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        AgentMemory memory = new InMemoryAgentMemory(20);

        // Turn 1: ask about food preference for Vishal
        String prompt1 = "My friend Vishal is coming for dinner — what should I cook?";
        Object result1 = processor.processSingleAction(
                memory.getHistoryAsContext() + prompt1,
                new FoodAction(), "whatFoodDoesThisPersonLike");
        Assertions.assertNotNull(result1, "First turn result should not be null");
        memory.addTurn(prompt1, result1);

        log.info("Turn 1 result: {}", result1);

        // Turn 2: follow-up — agent should remember the dish from turn 1
        String prompt2 = "What are the main ingredients I need for that dish?";
        Object result2 = processor.query(memory.getHistoryAsContext() + prompt2);
        Assertions.assertNotNull(result2, "Second turn result should not be null");
        memory.addTurn(prompt2, result2);

        log.info("Turn 2 result: {}", result2);

        // Validate that the second response refers to Paneer (the dish Vishal likes)
        String validation = TestHelperOpenAI.getInstance().sendMessage(
                "Reply in true or false only — does this text mention paneer or butter masala or ingredients for paneer butter masala: \""
                        + result2 + "\"");
        log.info("Memory validation: {}", validation);
        Assertions.assertTrue("true".equalsIgnoreCase(validation.trim()),
                "Second-turn answer should reference the dish remembered from turn 1");

        Assertions.assertEquals(2, memory.size());
    }

    // -----------------------------------------------------------------------
    // Test 2 — Multi-agent orchestration: correct agent receives the sub-task
    // -----------------------------------------------------------------------

    @Test
    void testOrchestrationRoutesToCorrectAgent() throws AIProcessingException {
        OpenAiActionProcessor router     = new OpenAiActionProcessor();
        OpenAiActionProcessor flightProc = new OpenAiActionProcessor();
        OpenAiActionProcessor hotelProc  = new OpenAiActionProcessor();

        AgentOrchestrator orchestrator = new AgentOrchestrator(router)
                .register(new AgentDefinition(
                        "flightAgent",
                        "Use this agent when the task involves booking or checking flights",
                        flightProc))
                .register(new AgentDefinition(
                        "hotelAgent",
                        "Use this agent when the task involves hotel or accommodation reservations",
                        hotelProc));

        String goal = "Book a flight from Toronto to Bangalore on August 15 "
                + "and a hotel near MG Road for 3 nights starting the same day";

        OrchestrationResult result = orchestrator.execute(goal);

        log.info("Orchestration successful: {}", result.isSuccessful());
        result.getResults().forEach((agent, res) ->
                log.info("  {} -> {}", agent, res));
        log.info("Synthesised answer: {}", result.getSummary());

        Assertions.assertTrue(result.isSuccessful(), "Orchestration should succeed");
        Assertions.assertFalse(result.getResults().isEmpty(),
                "At least one agent should have been invoked");
        Assertions.assertNotNull(result.getSummary(), "Synthesised summary should not be null");

        // Validate that the summary mentions both the flight and the hotel
        String validation = TestHelperOpenAI.getInstance().sendMessage(
                "Reply in true or false only — does this text reference both a flight booking "
                + "and a hotel reservation: \"" + result.getSummary() + "\"");
        log.info("Orchestration validation: {}", validation);
        Assertions.assertTrue("true".equalsIgnoreCase(validation.trim()),
                "Summary should mention both flight and hotel");
    }

    // -----------------------------------------------------------------------
    // Test 3 — ReAct planning: plan has steps with thought/action/observation
    // -----------------------------------------------------------------------

    @Test
    void testReActPlannerProducesSteps() throws AIProcessingException {
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        ReActPlanner planner = new ReActPlanner(processor, 5);

        String goal = "Find out what Vishal likes to eat and suggest a restaurant in Toronto "
                + "that serves that cuisine";

        ExecutionPlan plan = planner.plan(goal);

        log.info("Plan completed: {} steps", plan.getTotalSteps());
        for (PlanStep step : plan.getSteps()) {
            log.info("  Step {} | THOUGHT: {} | ACTION: {} | OBSERVATION: {}",
                    step.getStepNumber(), step.getThought(),
                    step.getActionPrompt(), step.getObservation());
        }
        log.info("Final answer: {}", plan.getFinalAnswer());

        Assertions.assertTrue(plan.getTotalSteps() >= 1,
                "Plan should contain at least one step");
        Assertions.assertNotNull(plan.getFinalAnswer(),
                "Plan should have a final answer");

        // Every step should have a non-null thought
        for (PlanStep step : plan.getSteps()) {
            Assertions.assertNotNull(step.getThought(),
                    "Each step should have a thought at step " + step.getStepNumber());
        }

        // Validate the final answer is sensible
        String validation = TestHelperOpenAI.getInstance().sendMessage(
                "Reply in true or false only — does this text mention food or a restaurant or cuisine: \""
                        + plan.getFinalAnswer() + "\"");
        log.info("ReAct validation: {}", validation);
        Assertions.assertTrue("true".equalsIgnoreCase(validation.trim()),
                "Final answer should be related to food / restaurant");
    }

    // -----------------------------------------------------------------------
    // Test 4 — Tool result feedback: enriched answer is more descriptive
    // -----------------------------------------------------------------------

    @Test
    void testToolResultFeedbackEnrichesAnswer() throws AIProcessingException {
        OpenAiActionProcessor raw = new OpenAiActionProcessor();
        ToolResultFeedbackProcessor enriched = new ToolResultFeedbackProcessor(raw);

        String prompt = "My friend Vishal is coming over — what should I cook for him?";

        // Without feedback: returns bare Java return value
        Object bareResult = raw.processSingleAction(prompt, new FoodAction(),
                "whatFoodDoesThisPersonLike");
        log.info("Without feedback: {}", bareResult);
        Assertions.assertNotNull(bareResult, "Bare result should not be null");

        // With feedback: LLM composes a natural-language sentence
        Object enrichedResult = enriched.processSingleAction(prompt, new FoodAction(),
                "whatFoodDoesThisPersonLike");
        log.info("With feedback: {}", enrichedResult);
        Assertions.assertNotNull(enrichedResult, "Enriched result should not be null");

        // The enriched result should be longer / more descriptive than the raw value
        String enrichedStr = enrichedResult.toString();
        String bareStr     = bareResult.toString();
        Assertions.assertTrue(enrichedStr.length() >= bareStr.length(),
                "Enriched answer should be at least as long as the raw value");

        // Validate that the enriched answer is natural language about the dish
        String validation = TestHelperOpenAI.getInstance().sendMessage(
                "Reply in true or false only — is this a natural-language sentence that mentions cooking or food: \""
                        + enrichedStr + "\"");
        log.info("Feedback validation: {}", validation);
        Assertions.assertTrue("true".equalsIgnoreCase(validation.trim()),
                "Enriched result should be a natural-language sentence about food");
    }
}
