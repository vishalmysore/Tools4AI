package com.t4a.processor;

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

    public static void clear() {
        callbackHolder.remove();
        processorHolder.remove();
    }
}
