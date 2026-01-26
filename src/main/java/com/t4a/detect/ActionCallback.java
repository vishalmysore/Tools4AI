package com.t4a.detect;

import java.util.concurrent.atomic.AtomicReference;

/**
 * ActionCallback interface for handling callbacks during AI processing actions.
 * Implementations of this interface should ensure thread-safety, as methods may be invoked
 * concurrently from multiple threads (e.g., during long-running tasks like database queries).
 * Use thread-safe constructs such as AtomicReference for mutable state to avoid race conditions.
 *
 * Interface Contract: By requiring AtomicReference as parameters, the interface mandates that implementations handle context and type updates in a thread-safe way. AtomicReference provides atomic (uninterruptible) operations, preventing race conditions during concurrent access.
 * Implementation Requirement: Any class implementing ActionCallback  must use AtomicReference internally for storage. This ensures that setContext, getContext, setType, and getType are thread-safe without additional synchronization.
 * Benefits:
 * No Race Conditions: Multiple threads can call these methods simultaneously without corrupting state.
 * Consistency: All implementations are forced to be thread-safe for context/type, reducing bugs.
 * Flexibility: sendtStatus can still be customized, but the core state management is safe.
 * If an implementation tries to ignore the AtomicReference , it won't be thread-safe for context/type—but the interface design encourages proper usage. . This makes the entire callback thread-safe for long-running actions.
 */
public interface ActionCallback {

    /**
     * Sets the context object for this callback using an AtomicReference for thread-safety.
     * This ensures atomic updates and prevents race conditions during concurrent access.
     *
     * @param context the AtomicReference containing the context object to set
     */
    void setContext(AtomicReference<Object> context);



    /**
     * Retrieves the context object.
     * Implementations must ensure thread-safe access, as this may be called concurrently.
     *
     * @return the current context object, or null if not set
     */
    AtomicReference<Object>  getContext();

    /**
     * Retrieves the type of this callback.
     * Implementations should ensure thread-safe access if the type can change.
     *
     * @return the type as a string
     */
      String getType();



    /**
     * Sends a status update during action processing.
     * This method may be called from multiple threads concurrently, so implementations should
     * use thread-safe logging and avoid mutable shared state unless properly synchronized.
     *
     * @param status the status message
     * @param state the current action state
     */
    void sendtStatus(String status, ActionState state);
}