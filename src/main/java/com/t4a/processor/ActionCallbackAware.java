package com.t4a.processor;

import com.t4a.detect.ActionCallback;

/**
 * An interface for components that are aware of ActionCallback.
 * Provides default implementations to set and get the ActionCallback
 * using a thread-safe context.
 * This is required as action classes might need to access the callback and send back status updates
 * during long-running operations.
 * In addition it is also a mechanism to differentiate different types of actions based on the callback type.
 * @see ActionCallback
 */
public interface ActionCallbackAware extends Aware{

    // Default implementation stores it in the thread-safe context
    default void setCallback(ActionCallback callback) {
        ActionContext.setCallback(callback);
    }

    // Default implementation retrieves it
    default ActionCallback getCallback() {
        return ActionContext.getCallback();
    }
}