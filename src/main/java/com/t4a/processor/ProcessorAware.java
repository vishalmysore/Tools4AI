package com.t4a.processor;

public interface ProcessorAware {
    default void setProcessor(AIProcessor processor) {
        ActionContext.setProcessor(processor);
    }

    default AIProcessor getProcessor() {
        return ActionContext.getProcessor();
    }
}