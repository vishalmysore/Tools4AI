package com.t4a.agent.orchestration;

import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AgentOrchestratorTest {

    /** Stub processor — returns a canned action result and a canned query answer. */
    static class StubProcessor implements AIProcessor {
        private final String actionResult;
        private final String queryResult;
        int actionCallCount = 0;

        StubProcessor(String actionResult, String queryResult) {
            this.actionResult = actionResult;
            this.queryResult  = queryResult;
        }

        @Override public String query(String p) { return queryResult; }

        @Override
        public Object processSingleAction(String p) { actionCallCount++; return actionResult; }

        @Override public Object processSingleAction(String p, ActionCallback cb) { return actionResult; }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e) { return actionResult; }
        @Override public Object processSingleAction(String p, HumanInLoop h, ExplainDecision e) { return actionResult; }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e, ActionCallback cb) { return actionResult; }
    }

    /**
     * Router that returns a fixed routing string in the format the orchestrator parses.
     * e.g. "flightAgent|Book a flight from Toronto to Bangalore"
     */
    static class RoutingStubProcessor extends StubProcessor {
        private final String routingResponse;
        RoutingStubProcessor(String routingResponse, String synthesis) {
            super("unused", synthesis);
            this.routingResponse = routingResponse;
        }
        @Override public String query(String p) {
            // first call is routing, subsequent calls are synthesis
            if (p.contains("orchestrator")) return routingResponse;
            return super.queryResult;
        }
    }

    private StubProcessor flightProcessor;
    private StubProcessor hotelProcessor;

    @BeforeEach
    void setUp() {
        flightProcessor = new StubProcessor("Flight BLR-001 confirmed", "flight query");
        hotelProcessor  = new StubProcessor("Hotel Leela booked",       "hotel query");
    }

    @Test
    void testRegisterAndGetNames() {
        AgentOrchestrator orch = new AgentOrchestrator(new StubProcessor("r", "q"));
        orch.register(new AgentDefinition("alpha", "handles alpha tasks", flightProcessor));
        orch.register(new AgentDefinition("beta",  "handles beta tasks",  hotelProcessor));

        List<String> names = orch.getRegisteredAgentNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("alpha"));
        assertTrue(names.contains("beta"));
    }

    @Test
    void testExecute_routesToCorrectAgent() throws AIProcessingException {
        // Router says: send to flightAgent with this sub-task
        RoutingStubProcessor router = new RoutingStubProcessor(
                "flightAgent|Book a flight to Bangalore",
                "All done: flight confirmed");

        AgentOrchestrator orch = new AgentOrchestrator(router);
        orch.register(new AgentDefinition("flightAgent", "handles flights", flightProcessor));
        orch.register(new AgentDefinition("hotelAgent",  "handles hotels",  hotelProcessor));

        OrchestrationResult result = orch.execute("Plan my trip to Bangalore");

        assertTrue(result.getResults().containsKey("flightAgent"));
        assertEquals("Flight BLR-001 confirmed", result.getResults().get("flightAgent"));
        // hotelAgent was not in the routing response
        assertFalse(result.getResults().containsKey("hotelAgent"));
        assertNotNull(result.getSummary());
        assertTrue(result.isSuccessful());
    }

    @Test
    void testExecute_noAgents_throws() {
        AgentOrchestrator orch = new AgentOrchestrator(new StubProcessor("r", "q"));
        assertThrows(IllegalStateException.class, () -> orch.execute("some goal"));
    }

    @Test
    void testExecute_withoutSynthesis_nullSummary() throws AIProcessingException {
        RoutingStubProcessor router = new RoutingStubProcessor(
                "flightAgent|book flight", "synthesised answer");

        AgentOrchestrator orch = new AgentOrchestrator(router)
                .withoutSynthesis();
        orch.register(new AgentDefinition("flightAgent", "handles flights", flightProcessor));

        OrchestrationResult result = orch.execute("Book a flight");
        assertNull(result.getSummary());
    }

    @Test
    void testOrchestrationResult_goal() {
        OrchestrationResult r = new OrchestrationResult("my goal");
        assertEquals("my goal", r.getGoal());
        assertTrue(r.isSuccessful());
        assertTrue(r.getResults().isEmpty());
    }

    @Test
    void testOrchestrationResult_markFailed() {
        OrchestrationResult r = new OrchestrationResult("goal");
        r.markFailed("agent1", new RuntimeException("oops"));
        assertFalse(r.isSuccessful());
        assertTrue(r.getResults().get("agent1").toString().contains("oops"));
    }

    @Test
    void testAgentDefinition_blankNameThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new AgentDefinition("", "desc", flightProcessor));
    }

    @Test
    void testAgentDefinition_nullProcessorThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new AgentDefinition("name", "desc", null));
    }

    @Test
    void testNullRouterThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new AgentOrchestrator(null));
    }
}
