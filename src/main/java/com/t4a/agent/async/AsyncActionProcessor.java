package com.t4a.agent.async;

import com.t4a.api.AIAction;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Non-blocking wrapper around any {@link AIProcessor}.
 *
 * <p>Every {@code processSingleAction} call in Tools4AI is blocking, so a request that fans out
 * to several actions pays the sum of their latencies. This wrapper runs each call on an executor
 * and hands back a {@link CompletableFuture}, letting independent LLM calls overlap:
 *
 * <pre>{@code
 * try (AsyncActionProcessor async = new AsyncActionProcessor(new OpenAiActionProcessor(), 4)) {
 *
 *     CompletableFuture<Object> flight = async.processSingleActionAsync("Book a flight to Tokyo");
 *     CompletableFuture<Object> hotel  = async.processSingleActionAsync("Reserve a hotel in Tokyo");
 *
 *     CompletableFuture.allOf(flight, hotel).join();   // both ran concurrently
 *     System.out.println(flight.get() + " / " + hotel.get());
 * }
 * }</pre>
 *
 * <p>Or run a whole batch and collect the results in order:
 * <pre>{@code
 * List<Object> results = async.processAllAsync(List.of(
 *         "Book a flight to Tokyo",
 *         "Reserve a hotel in Tokyo",
 *         "Arrange airport pickup")).join();
 * }</pre>
 *
 * <p>Failures surface the usual way for futures: the returned future completes exceptionally with
 * a {@link CompletionException} wrapping the original {@link AIProcessingException}.
 *
 * <p><strong>Executor ownership.</strong> The constructors that take a thread count create and own
 * a fixed pool, which {@link #close()} shuts down. The constructor that accepts an existing
 * {@link ExecutorService} does not take ownership — {@code close()} leaves it running, so an
 * application-managed pool keeps working after the wrapper goes out of scope.
 */
@Slf4j
public class AsyncActionProcessor implements AutoCloseable {

    private final AIProcessor delegate;
    private final ExecutorService executor;
    private final boolean ownsExecutor;

    /** Wrap with an owned pool of 4 daemon threads. */
    public AsyncActionProcessor(AIProcessor delegate) {
        this(delegate, 4);
    }

    /** Wrap with an owned pool of {@code threads} daemon threads. */
    public AsyncActionProcessor(AIProcessor delegate, int threads) {
        if (delegate == null) throw new IllegalArgumentException("delegate must not be null");
        if (threads < 1) throw new IllegalArgumentException("threads must be >= 1");
        this.delegate = delegate;
        this.executor = Executors.newFixedThreadPool(threads, runnable -> {
            Thread thread = new Thread(runnable, "tools4ai-async");
            thread.setDaemon(true);
            return thread;
        });
        this.ownsExecutor = true;
    }

    /** Wrap using a caller-managed executor, which {@link #close()} will not shut down. */
    public AsyncActionProcessor(AIProcessor delegate, ExecutorService executor) {
        if (delegate == null) throw new IllegalArgumentException("delegate must not be null");
        if (executor == null) throw new IllegalArgumentException("executor must not be null");
        this.delegate = delegate;
        this.executor = executor;
        this.ownsExecutor = false;
    }

    /** The processor being wrapped, for callers that still need a blocking path. */
    public AIProcessor getDelegate() {
        return delegate;
    }

    @FunctionalInterface
    private interface Invocation<T> {
        T run() throws AIProcessingException;
    }

    private <T> CompletableFuture<T> submit(Invocation<T> invocation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return invocation.run();
            } catch (AIProcessingException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Object> processSingleActionAsync(String promptText) {
        return submit(() -> delegate.processSingleAction(promptText));
    }

    public CompletableFuture<Object> processSingleActionAsync(String promptText, AIAction action,
                                                              HumanInLoop humanVerification,
                                                              ExplainDecision explain) {
        return submit(() -> delegate.processSingleAction(promptText, action, humanVerification, explain));
    }

    public CompletableFuture<String> queryAsync(String promptText) {
        return submit(() -> delegate.query(promptText));
    }

    /**
     * Run every prompt concurrently and complete with the results in the order the prompts were
     * given. If any prompt fails the combined future completes exceptionally, but the remaining
     * prompts still run to completion.
     */
    public CompletableFuture<List<Object>> processAllAsync(List<String> prompts) {
        if (prompts == null || prompts.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        List<CompletableFuture<Object>> futures = prompts.stream()
                .map(this::processSingleActionAsync)
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    /** Shuts down the executor when this wrapper owns it; a caller-supplied executor is left alone. */
    @Override
    public void close() {
        if (!ownsExecutor) return;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Async executor did not terminate in 5s, forcing shutdown");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
    }
}
