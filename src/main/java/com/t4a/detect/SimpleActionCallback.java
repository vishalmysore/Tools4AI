package com.t4a.detect;

import lombok.extern.java.Log;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The ActionCallback (e.g., SimpleActionCallback) in Tools4AI is used to provide real-time feedback during long-running AI actions, allowing the system to report progress, handle errors, or update status without blocking the main process. It's particularly useful in multi-threaded or asynchronous environments.
 *
 * Practical Use Cases
 * Database Queries: Track query execution stages (e.g., "Connecting to DB", "Fetching records", "Processing results"). Example: In DatabaseExample, it logs status updates every second during a customer lookup, simulating a slow query.
 *
 * File Processing: Report progress on large file operations (e.g., "Reading file", "Parsing data", "Writing output"). Useful for ETL tasks or data imports where users need visibility into long-running jobs.
 *
 * API/Web Service Calls: Notify on request lifecycle (e.g., "Sending request", "Waiting for response", "Handling errors"). Ideal for integrations with external services that may take time.
 *
 * Batch Processing or Simulations: Update on iterations (e.g., "Processing batch 1/10", "Simulating scenario X"). Common in AI training loops or Monte Carlo simulations.
 *
 * User Interface Updates: In GUI apps, send status to update progress bars or logs (e.g., via sendtStatus). For example, a desktop tool could show "AI is analyzing..." with incremental updates.
 *
 * Error Handling and Recovery: Use ActionState (e.g., WORKING, COMPLETED, FAILED) to trigger retries or alerts. If an action fails midway, the callback can log errors and notify for manual intervention.
 *
 * In code, the callback is passed to AIProcessor.processSingleAction(), and the framework invokes sendtStatus from background threads. This keeps the main thread responsive while providing transparency. For custom actions, implement ActionCallback to tailor feedback to your use case.
 */

public abstract class SimpleActionCallback implements ActionCallback {

    private AtomicReference<Object> context = null;

    @Override
    public void setContext(AtomicReference<Object> context) {
        this.context = context;
    }

    @Override
    public AtomicReference<Object> getContext() {
        return context;
    }

    @Override
    public String getType() {
        return "simple";
    }




}
