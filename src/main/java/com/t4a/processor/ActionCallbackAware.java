package com.t4a.processor;

import com.t4a.detect.ActionCallback;

public interface ActionCallbackAware {

    // Default implementation stores it in the thread-safe context
    default void setCallback(ActionCallback callback) {
        ActionContext.setCallback(callback);
    }

    // Default implementation retrieves it
    default ActionCallback getCallback() {
        return ActionContext.getCallback();
    }
}