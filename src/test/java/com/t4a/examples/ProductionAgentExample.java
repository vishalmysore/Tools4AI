package com.t4a.examples;

import com.t4a.agent.async.AsyncActionProcessor;
import com.t4a.agent.audit.AuditTrail;
import com.t4a.agent.audit.AuditedActionProcessor;
import com.t4a.agent.audit.InMemoryAuditTrail;
import com.t4a.agent.metrics.InMemoryActionMetrics;
import com.t4a.agent.metrics.MeteredActionProcessor;
import com.t4a.agent.resilience.RateLimitedActionProcessor;
import com.t4a.agent.resilience.RetryActionProcessor;
import com.t4a.agent.safety.ActionBlockedException;
import com.t4a.agent.safety.RiskGatedActionProcessor;
import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.api.ActionRisk;
import com.t4a.detect.FeedbackLoop;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import com.t4a.processor.OpenAiActionProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * End-to-end example of the operational capabilities that turn a working agent into one you
 * can run in production: risk gating, rate limiting, metrics, and concurrency.
 *
 * <p>{@link AgentCapabilitiesExample} covers what an agent can <em>do</em> — memory, planning,
 * orchestration, feedback. This example covers what an agent needs to do it <em>safely and
 * observably</em>:
 *
 * <ol>
 *   <li><b>Risk gating</b> — {@link RiskGatedActionProcessor} enforces the {@link ActionRisk}
 *       contract: MEDIUM needs one human approval, HIGH needs two.</li>
 *   <li><b>Rate limiting</b> — {@link RateLimitedActionProcessor} shapes traffic to stay inside
 *       the provider's quota instead of reacting to 429s after the fact.</li>
 *   <li><b>Metrics</b> — {@link MeteredActionProcessor} records latency and error rate per
 *       operation into any {@link com.t4a.agent.metrics.ActionMetrics} sink.</li>
 *   <li><b>Async execution</b> — {@link AsyncActionProcessor} overlaps independent calls instead
 *       of paying the sum of their latencies.</li>
 *   <li><b>The full stack</b> — all of the above composed with retry and audit into one
 *       processor.</li>
 * </ol>
 *
 * <p><b>Prerequisites:</b> set {@code openAiKey} in {@code tools4ai.properties}
 * (or pass {@code -DopenAiKey=sk-...} at runtime). Swap in {@code GeminiV2ActionProcessor} or
 * {@code AnthropicActionProcessor} to use a different provider — every decorator here wraps the
 * {@link AIProcessor} interface, not a specific implementation.
 */
@Slf4j
public class ProductionAgentExample {

    // =========================================================================
    // Sample @Agent class — one harmless action, one destructive one
    // =========================================================================

    @Agent(groupName = "ops", groupDescription = "server operations")
    public static class OpsAction {

        @Action(description = "report the current status of a server")
        public String getServerStatus(String serverName) {
            return serverName + " is healthy, 42% CPU";
        }

        @Action(description = "restart a production server, this is disruptive")
        public String restartServer(String serverName) {
            return serverName + " restarted";
        }
    }

    /**
     * Stand-in approver. A real implementation would raise a ticket, post to Slack, or block on
     * an approval UI, and return the human's verdict — here everything destructive is refused.
     */
    static class ConsoleApprover implements HumanInLoop {

        private final String name;

        ConsoleApprover(String name) {
            this.name = name;
        }

        private FeedbackLoop decide(String promptText, String methodName) {
            boolean destructive = methodName.toLowerCase().contains("restart")
                    || methodName.toLowerCase().contains("delete");
            log.info("[{}] approval requested for '{}' ({}) → {}",
                    name, methodName, promptText, destructive ? "DENIED" : "APPROVED");
            return () -> !destructive;
        }

        @Override public FeedbackLoop allow(String promptText, String methodName, Map<String, Object> params) {
            return decide(promptText, methodName);
        }

        @Override public FeedbackLoop allow(String promptText, String methodName, String params) {
            return decide(promptText, methodName);
        }
    }

    // =========================================================================
    // Demo 1 — Risk gating
    // =========================================================================

    /**
     * Shows the {@link ActionRisk} contract being enforced rather than merely declared.
     *
     * <p>Before this decorator existed, an action marked HIGH risk still executed unless the
     * calling code remembered to check {@code getActionRisk()} itself. Routing through
     * {@link RiskGatedActionProcessor} makes the approval mandatory, and a refusal arrives as
     * {@link ActionBlockedException} rather than as a silently completed action.
     */
    public static void demoRiskGating() throws AIProcessingException {
        log.info("=== Demo 1: Risk gating ===");

        HumanInLoop onCallEngineer = new ConsoleApprover("on-call engineer");
        HumanInLoop securityOfficer = new ConsoleApprover("security officer");

        AIProcessor gated = new RiskGatedActionProcessor(
                new OpenAiActionProcessor(), onCallEngineer, securityOfficer);

        // A LOW risk action (the default) runs without asking anyone
        Object status = gated.processSingleAction("How is web-01 doing?", new OpsAction(), "getServerStatus");
        log.info("Status check ran unattended: {}", status);

        // Mark the destructive action MEDIUM or HIGH in your own AIAction implementation and the
        // gate demands approvals before the delegate is ever called.
        try {
            gated.processSingleAction("Restart web-01 right now", new OpsAction(), "restartServer");
        } catch (ActionBlockedException e) {
            log.info("Blocked as expected — risk={} action={} reason={}",
                    e.getRisk(), e.getActionName(), e.getMessage());
        }
    }

    // =========================================================================
    // Demo 2 — Rate limiting
    // =========================================================================

    /**
     * Shows traffic shaping: five calls against a two-per-second budget take about two seconds
     * instead of tripping the provider's quota and returning 429s.
     *
     * <p>Set {@code maxWaitMillis} to 0 to shed load instead of queueing — the call then fails
     * immediately with {@link com.t4a.agent.resilience.RateLimitExceededException}.
     */
    public static void demoRateLimiting() throws AIProcessingException {
        log.info("=== Demo 2: Rate limiting ===");

        AIProcessor throttled = new RateLimitedActionProcessor(
                new OpenAiActionProcessor(),
                2.0,      // 2 calls per second sustained
                2,        // allow a burst of 2 after an idle period
                5000);    // wait up to 5s for a permit before failing

        long start = System.currentTimeMillis();
        for (int i = 1; i <= 5; i++) {
            throttled.processSingleAction("How is web-0" + i + " doing?", new OpsAction(), "getServerStatus");
            log.info("Call {} completed at +{}ms", i, System.currentTimeMillis() - start);
        }
    }

    // =========================================================================
    // Demo 3 — Metrics
    // =========================================================================

    /**
     * Shows latency and error-rate collection. The same {@link InMemoryActionMetrics} instance
     * can be shared across providers — pass a {@code namePrefix} so each one's numbers stay
     * separate and comparable.
     */
    public static void demoMetrics() throws AIProcessingException {
        log.info("=== Demo 3: Metrics ===");

        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        AIProcessor metered = new MeteredActionProcessor(new OpenAiActionProcessor(), metrics, "openai.");

        metered.processSingleAction("How is web-01 doing?", new OpsAction(), "getServerStatus");
        metered.processSingleAction("How is web-02 doing?", new OpsAction(), "getServerStatus");
        metered.query("Summarise the state of the fleet");

        log.info(metrics.report());
        log.info("Action error rate: {}", metrics.snapshot("openai.processSingleAction").getErrorRate());
    }

    // =========================================================================
    // Demo 4 — Async fan-out
    // =========================================================================

    /**
     * Shows three independent prompts running concurrently. Sequentially this costs the sum of
     * three LLM round trips; on a three-thread pool it costs roughly one.
     */
    public static void demoAsync() throws Exception {
        log.info("=== Demo 4: Async fan-out ===");

        try (AsyncActionProcessor async = new AsyncActionProcessor(new OpenAiActionProcessor(), 3)) {

            CompletableFuture<Object> web01 = async.processSingleActionAsync("How is web-01 doing?");
            CompletableFuture<Object> web02 = async.processSingleActionAsync("How is web-02 doing?");
            CompletableFuture<Object> web03 = async.processSingleActionAsync("How is web-03 doing?");

            CompletableFuture.allOf(web01, web02, web03).join();
            log.info("web-01: {}", web01.get());
            log.info("web-02: {}", web02.get());
            log.info("web-03: {}", web03.get());

            // Or hand over the whole batch and get the results back in prompt order
            List<Object> batch = async.processAllAsync(Arrays.asList(
                    "How is db-01 doing?",
                    "How is db-02 doing?")).join();
            log.info("Batch results: {}", batch);
        }
    }

    // =========================================================================
    // Demo 5 — The full production stack
    // =========================================================================

    /**
     * Shows every decorator composed into one processor. Order is what gives each layer its
     * meaning, reading from the outside in:
     *
     * <pre>
     *   audited(          ← records the final outcome, after retries and approvals
     *     metered(        ← measures total user-visible latency
     *       riskGated(    ← approvals happen once, not once per retry
     *         retrying(   ← retries transient provider failures
     *           rateLimited(processor)))))   ← every attempt takes its own permit
     * </pre>
     *
     * <p>Moving the metrics layer inside the retry decorator would measure each attempt
     * separately instead; moving the risk gate inside it would re-prompt the human on every
     * retry. Neither is wrong — but the difference is worth choosing deliberately.
     */
    public static void demoFullStack() throws AIProcessingException {
        log.info("=== Demo 5: Full production stack ===");

        InMemoryActionMetrics metrics = new InMemoryActionMetrics();
        AuditTrail auditTrail = new InMemoryAuditTrail(500);
        HumanInLoop approver = new ConsoleApprover("on-call engineer");

        AIProcessor agent =
                new AuditedActionProcessor(
                        new MeteredActionProcessor(
                                new RiskGatedActionProcessor(
                                        new RetryActionProcessor(
                                                new RateLimitedActionProcessor(new OpenAiActionProcessor(), 5.0),
                                                3, 500, null),
                                        approver),
                                metrics),
                        auditTrail);

        agent.processSingleAction("How is web-01 doing?", new OpsAction(), "getServerStatus");

        log.info(metrics.report());
        log.info("Audit trail: {}", ((InMemoryAuditTrail) auditTrail).getEntries());
    }

    // =========================================================================
    // Main — run every demo
    // =========================================================================

    public static void main(String[] args) throws Exception {
        demoRiskGating();
        demoRateLimiting();
        demoMetrics();
        demoAsync();
        demoFullStack();
    }
}
