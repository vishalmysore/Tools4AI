package com.t4a.processor;

/**
 * An interface to be implemented by classes that need to be aware of the AIProcessor instance.
 * It provides default methods to set and get the AIProcessor using the ActionContext.
 * This allows for easy access to the AIProcessor within the implementing classes without creating new AIProcessor instances.
 * So all the action classes can implement this interface to get access to the current AIProcessor.
 */
public interface ProcessorAware extends Aware{
    default void setProcessor(AIProcessor processor) {
        ActionContext.setProcessor(processor);
    }

    default AIProcessor getProcessor() {
        return ActionContext.getProcessor();
    }
}