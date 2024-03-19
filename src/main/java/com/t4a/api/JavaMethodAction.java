package com.t4a.api;

import com.t4a.predict.Predict;


public interface JavaMethodAction extends AIAction{
    public default ActionType getActionType (){
        return ActionType.JAVAMETHOD;
    }

    @Override
    public default  String getActionName() {
      String actionName =  this.getClass().getAnnotation(Predict.class).actionName();
      return actionName;
    }

    @Override
    public default String getDescription(){
       return this.getClass().getAnnotation(Predict.class).description();
    }
}
