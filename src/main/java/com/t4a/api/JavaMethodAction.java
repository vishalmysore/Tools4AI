package com.t4a.api;

import com.t4a.predict.Predict;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


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

    public default boolean isComplexMethod(){
        boolean iscomplex = false;
        Method[] met = this.getClass().getMethods();
        for (Method methood:met
             ) {
            if(methood.getName().equals(getActionName())){
                Parameter[] params = methood.getParameters();
                for (Parameter param:params
                     ) {
                    Class type = param.getType();
                    if ((type == String.class) || (type == int.class || type == Integer.class) || (type == double.class || type == Double.class) || (type == boolean.class || type == Boolean.class) || (type.isArray())) {
                     iscomplex = false;
                    } else {
                        iscomplex = true;
                    }

                }
            }
        }
        return iscomplex;
    }




}
