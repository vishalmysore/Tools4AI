package com.t4a.processor;
/**
 * ActionContext provides thread-safe storage for ActionCallback and AIProcessor instances
 * using ThreadLocal. This allows each thread to maintain its own context without interference
 * from other threads, which is essential in multi-threaded environments like long-running AI actions.
 *
 * Thread-safety is achieved through ThreadLocal, ensuring that set/get operations are isolated
 * per thread. Always call clear() after use to prevent memory leaks in long-running applications.
 */
import com.t4a.detect.ActionCallback;
import com.t4a.processor.AIProcessor;

public class ActionContext {
    private static final ThreadLocal<ActionCallback> callbackHolder = new ThreadLocal<>();
    private static final ThreadLocal<AIProcessor> processorHolder = new ThreadLocal<>();

    public static void setCallback(ActionCallback callback) {
        callbackHolder.set(callback);
    }

    public static ActionCallback getCallback() {
        return callbackHolder.get();
    }

    public static void setProcessor(AIProcessor processor) {
        processorHolder.set(processor);
    }

    public static AIProcessor getProcessor() {
        return processorHolder.get();
    }

    /**
     * Clears the ThreadLocal storage for both callback and processor in the current thread.
     * This should be called after processing to avoid memory leaks, especially in application servers.
     */
    public static void clear() {
        callbackHolder.remove();
        processorHolder.remove();
    }
}
