package com.t4a.action;

import com.t4a.api.ActionType;
import com.t4a.api.PredictedAIAction;
import com.t4a.predict.LoaderException;

import java.util.List;
import java.util.Map;

/**
 * Shell, HTTP and Java Methods are supported by default out of the box most of the tasks could be accomplished by the same
 * however there might be a situation or use case to create custom set of actions *Please note* this is different from Custom actions.
 * Custom actions are the one you can create by implementing AIAciton class, whereas ExtendedPredictedAction have their own loading mechanism
 * ExtendedPredictedAction are already in the prediction list by default and they cannot be predicted again
 *
 * You will need to create custom implementation of ExtendedPredictionLoader and then annotate it with
 * \@ActivateLoader annotation to load automatically . Prediction loader  will pick up all the classes with
 * ActivateLoader annotation and call the getExtendedActions which will then return the action name and ExtendedPredictOptions
 *
 *

 * @see com.t4a.predict.ExtendedPredictionLoader
 */
public abstract class ExtendedPredictedAction implements PredictedAIAction {

    public abstract List<ExtendedInputParameter> getInputParameters();
    @Override
    public final ActionType getActionType() {
        return ActionType.EXTEND;
    }




    /**
     * This will be last method called during the invocation of function, we already have all the data
     * @param params
     * @return
     * @throws LoaderException
     */
    public abstract Object extendedExecute(Map<String,Object> params) throws LoaderException;

   


}
