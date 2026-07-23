package com.t4a.agent.safety;

import com.t4a.api.AIAction;
import com.t4a.api.ActionRisk;
import com.t4a.api.ActionType;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.FeedbackLoop;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RiskGatedActionProcessorTest {

    static class ScriptedProcessor implements AIProcessor {
        int calls = 0;

        private Object attempt() {
            calls++;
            return "executed";
        }

        @Override public Object processSingleAction(String p) { return attempt(); }
        @Override public Object processSingleAction(String p, ActionCallback cb) { return attempt(); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e) { return attempt(); }
        @Override public Object processSingleAction(String p, HumanInLoop h, ExplainDecision e) { return attempt(); }
        @Override public Object processSingleAction(String p, AIAction a, HumanInLoop h, ExplainDecision e, ActionCallback cb) { return attempt(); }
        @Override public String query(String p) { return (String) attempt(); }
    }

    /** Approver that answers with a fixed verdict and counts how many times it was asked. */
    static class ScriptedApprover implements HumanInLoop {
        final boolean verdict;
        int asked = 0;
        String lastPrompt;
        String lastMethodName;

        ScriptedApprover(boolean verdict) {
            this.verdict = verdict;
        }

        private FeedbackLoop answer(String prompt, String methodName) {
            asked++;
            lastPrompt = prompt;
            lastMethodName = methodName;
            return () -> verdict;
        }

        @Override public FeedbackLoop allow(String promptText, String methodName, Map<String, Object> params) {
            return answer(promptText, methodName);
        }

        @Override public FeedbackLoop allow(String promptText, String methodName, String params) {
            return answer(promptText, methodName);
        }
    }

    static class RiskyAction implements AIAction {
        private final ActionRisk risk;

        RiskyAction(ActionRisk risk) {
            this.risk = risk;
        }

        @Override public String getActionName() { return "dropCustomerDatabase"; }
        @Override public ActionType getActionType() { return ActionType.JAVAMETHOD; }
        @Override public String getDescription() { return "drops the customer database"; }
        @Override public ActionRisk getActionRisk() { return risk; }
    }

    @Test
    void testApprovalsRequiredPerRiskLevel() {
        assertEquals(0, RiskGatedActionProcessor.approvalsRequired(ActionRisk.LOW));
        assertEquals(1, RiskGatedActionProcessor.approvalsRequired(ActionRisk.MEDIUM));
        assertEquals(2, RiskGatedActionProcessor.approvalsRequired(ActionRisk.HIGH));
        assertEquals(0, RiskGatedActionProcessor.approvalsRequired(null));
    }

    @Test
    void testLowRisk_runsWithoutAskingAnyone() throws AIProcessingException {
        ScriptedProcessor delegate = new ScriptedProcessor();
        ScriptedApprover approver = new ScriptedApprover(true);
        RiskGatedActionProcessor gated = new RiskGatedActionProcessor(delegate, approver);

        Object result = gated.processSingleAction("read the weather", new RiskyAction(ActionRisk.LOW), approver, null);

        assertEquals("executed", result);
        assertEquals(1, delegate.calls);
        assertEquals(0, approver.asked);
    }

    @Test
    void testMediumRisk_asksOnceAndProceedsWhenApproved() throws AIProcessingException {
        ScriptedProcessor delegate = new ScriptedProcessor();
        ScriptedApprover approver = new ScriptedApprover(true);
        RiskGatedActionProcessor gated = new RiskGatedActionProcessor(delegate, approver);

        gated.processSingleAction("restart the server", new RiskyAction(ActionRisk.MEDIUM), approver, null);

        assertEquals(1, approver.asked);
        assertEquals(1, delegate.calls);
        assertEquals("restart the server", approver.lastPrompt);
        assertEquals("dropCustomerDatabase", approver.lastMethodName);
    }

    @Test
    void testMediumRisk_blockedWhenDeclined() {
        ScriptedProcessor delegate = new ScriptedProcessor();
        ScriptedApprover approver = new ScriptedApprover(false);
        RiskGatedActionProcessor gated = new RiskGatedActionProcessor(delegate, approver);

        ActionBlockedException ex = assertThrows(ActionBlockedException.class,
                () -> gated.processSingleAction("restart the server", new RiskyAction(ActionRisk.MEDIUM), approver, null));

        assertEquals(ActionRisk.MEDIUM, ex.getRisk());
        assertEquals("dropCustomerDatabase", ex.getActionName());
        assertEquals(0, delegate.calls, "delegate must not run when the action is blocked");
    }

    @Test
    void testHighRisk_requiresTwoApprovals() throws AIProcessingException {
        ScriptedProcessor delegate = new ScriptedProcessor();
        ScriptedApprover primary = new ScriptedApprover(true);
        ScriptedApprover secondary = new ScriptedApprover(true);
        RiskGatedActionProcessor gated = new RiskGatedActionProcessor(delegate, primary, secondary);

        gated.processSingleAction("drop the db", new RiskyAction(ActionRisk.HIGH), primary, null);

        assertEquals(1, primary.asked);
        assertEquals(1, secondary.asked);
        assertEquals(1, delegate.calls);
    }

    @Test
    void testHighRisk_secondApproverVeto_blocks() {
        ScriptedProcessor delegate = new ScriptedProcessor();
        ScriptedApprover primary = new ScriptedApprover(true);
        ScriptedApprover secondary = new ScriptedApprover(false);
        RiskGatedActionProcessor gated = new RiskGatedActionProcessor(delegate, primary, secondary);

        ActionBlockedException ex = assertThrows(ActionBlockedException.class,
                () -> gated.processSingleAction("drop the db", new RiskyAction(ActionRisk.HIGH), primary, null));

        assertEquals(ActionRisk.HIGH, ex.getRisk());
        assertEquals(1, primary.asked);
        assertEquals(1, secondary.asked);
        assertEquals(0, delegate.calls);
    }

    @Test
    void testHighRisk_withoutSecondaryApprover_asksPrimaryTwice() throws AIProcessingException {
        ScriptedProcessor delegate = new ScriptedProcessor();
        ScriptedApprover primary = new ScriptedApprover(true);
        RiskGatedActionProcessor gated = new RiskGatedActionProcessor(delegate, primary);

        gated.processSingleAction("drop the db", new RiskyAction(ActionRisk.HIGH), primary, null);

        assertEquals(2, primary.asked);
        assertEquals(1, delegate.calls);
    }

    @Test
    void testPromptOnlyCall_defaultsToLowRiskAndPassesThrough() throws AIProcessingException {
        ScriptedProcessor delegate = new ScriptedProcessor();
        ScriptedApprover approver = new ScriptedApprover(false);
        RiskGatedActionProcessor gated = new RiskGatedActionProcessor(delegate, approver);

        assertEquals("executed", gated.processSingleAction("do something harmless"));
        assertEquals(0, approver.asked);
        assertEquals(1, delegate.calls);
    }

    @Test
    void testPromptOnlyCall_failClosedWhenUnknownRiskIsHigh() {
        ScriptedProcessor delegate = new ScriptedProcessor();
        ScriptedApprover approver = new ScriptedApprover(false);
        RiskGatedActionProcessor gated =
                new RiskGatedActionProcessor(delegate, approver, null, ActionRisk.HIGH);

        ActionBlockedException ex = assertThrows(ActionBlockedException.class,
                () -> gated.processSingleAction("do anything at all"));

        assertEquals("unknown", ex.getActionName());
        assertEquals(0, delegate.calls);
    }

    @Test
    void testNullDecisionFromApproverIsTreatedAsDenial() {
        ScriptedProcessor delegate = new ScriptedProcessor();
        HumanInLoop nullApprover = new HumanInLoop() {
            @Override public FeedbackLoop allow(String p, String m, Map<String, Object> params) { return null; }
            @Override public FeedbackLoop allow(String p, String m, String params) { return null; }
        };
        RiskGatedActionProcessor gated = new RiskGatedActionProcessor(delegate, nullApprover);

        assertThrows(ActionBlockedException.class,
                () -> gated.processSingleAction("restart", new RiskyAction(ActionRisk.MEDIUM), nullApprover, null));
        assertEquals(0, delegate.calls);
    }

    @Test
    void testQueryIsNotGated() throws AIProcessingException {
        ScriptedProcessor delegate = new ScriptedProcessor();
        ScriptedApprover approver = new ScriptedApprover(false);
        RiskGatedActionProcessor gated =
                new RiskGatedActionProcessor(delegate, approver, null, ActionRisk.HIGH);

        assertEquals("executed", gated.query("what is the weather"));
        assertEquals(0, approver.asked);
    }

    @Test
    void testConstructorRejectsNulls() {
        assertThrows(IllegalArgumentException.class,
                () -> new RiskGatedActionProcessor(null, new ScriptedApprover(true)));
        assertThrows(IllegalArgumentException.class,
                () -> new RiskGatedActionProcessor(new ScriptedProcessor(), null));
    }
}
