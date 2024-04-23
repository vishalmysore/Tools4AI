package com.t4a.api;

import com.t4a.annotations.Action;
import com.t4a.annotations.Predict;
import com.t4a.processor.AIProcessingException;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

public class GenericJavaMethodAction implements JavaMethodAction{
    private String actionClassName;
    private String actionName;
    private String description;
    private ActionRisk riskLevel = ActionRisk.LOW;
    private String groupName = ToolsConstants.GROUP_NAME;
    private String groupDescription = ToolsConstants.GROUP_DESCRIPTION;
    private Class clazz;

    @Setter
    @Getter
    private Object actionInstance = null;
    @Getter
   private Method actionMethod = null;
    public GenericJavaMethodAction(Object actionInstance, String actionName) throws AIProcessingException {
        this(actionInstance.getClass(),actionName);
        this.actionInstance = actionInstance;
    }
    public GenericJavaMethodAction(Object actionInstance) throws AIProcessingException {
        this.actionInstance= actionInstance;
        init(actionInstance.getClass(),getAnnotatedMethods(actionInstance.getClass())) ;
    }
    public GenericJavaMethodAction(Class clazz, String actionName) throws AIProcessingException{
        Predict predict = (Predict)clazz.getAnnotation(Predict.class);
        Method[] methods = clazz.getMethods();
        for (Method m1 : methods
        ) {
            if (m1.getName().equals(actionName)) {
                actionMethod = m1;
                break;
            }
        }
        if(actionMethod== null) {
            throw new AIProcessingException(actionName+" method not found in class "+clazz.getName());
        }
        this.clazz = clazz;
        this.actionName = actionName;
        if(predict != null) {
            this.groupDescription = predict.groupDescription();
            this.groupName = predict.groupName();
        }
        this.actionClassName = clazz.getName();
        initAction(actionMethod);
    }
    public GenericJavaMethodAction(Class clazz, Method actionMethod) throws AIProcessingException{
        init(clazz, actionMethod);
    }

    private void init(Class clazz, Method actionMethod) throws AIProcessingException {
        Predict predict = (Predict) clazz.getAnnotation(Predict.class);
               this.clazz = clazz;
        this.actionName = actionMethod.getName();
        this.actionMethod = actionMethod;
        if(predict != null) {
            this.groupDescription = predict.groupDescription();
            this.groupName = predict.groupName();
        }
        this.actionClassName = clazz.getName();
        initAction(actionMethod);
    }

    public Method getAnnotatedMethods(Class<?> clazz) {
        Method firstAnnotatedMethod = null;

        // Get all methods in the class
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Action.class)) {  // Check for Action annotation
                firstAnnotatedMethod = method;  // Store the first annotated method
                break;
            }
        }

        return firstAnnotatedMethod;  // Return the list of annotated methods
    }

    private void initAction(Method actionMethod){
        Action action = (Action)actionMethod.getAnnotation(Action.class);
        this.riskLevel = action.riskLevel();
        this.description = action.description();
        if(this.description == null || this.description.isEmpty()) {
            this.description = actionMethod.getName();
        }
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public String getActionGroup() {
        return groupName;
    }

    @Override
    public ActionRisk getActionRisk() {
        return riskLevel;
    }

    @Override
    public String getActionClassName() {
        return actionClassName;
    }

    @Override
    public Class getActionClass() {
        return clazz;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
