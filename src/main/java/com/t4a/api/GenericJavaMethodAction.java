package com.t4a.api;

import com.t4a.JsonUtils;
import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.processor.AIProcessingException;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

public class GenericJavaMethodAction implements JavaMethodAction{
    private String actionClassName;
    private String actionName;
    private String description;
    private String prompt;
    private String subprompt;
    private ActionRisk riskLevel = ActionRisk.LOW;
    private String groupName = ToolsConstants.GROUP_NAME;
    private String groupDescription = ToolsConstants.GROUP_DESCRIPTION;
    private Class clazz;

    private String jsonStr = null;

    private MethodFinder methodFinder = new DefaultMethodFinder();


    private JsonUtils jsonUtils = new JsonUtils();

    @Setter
    @Getter
    private Object actionInstance = null;
    @Getter
   private Method actionMethod = null;

    @Override
    public String getActionParameters() {
        return jsonStr;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getSubprompt() {
        return subprompt;
    }

    public GenericJavaMethodAction(Object actionInstance, String actionName) throws AIProcessingException {
        this(actionInstance.getClass(),actionName);
        this.actionInstance = actionInstance;
    }
    public GenericJavaMethodAction(Object actionInstance) throws AIProcessingException {
        this.actionInstance= actionInstance;
        init(actionInstance.getClass(),getAnnotatedMethods(actionInstance.getClass())) ;
    }
    public GenericJavaMethodAction(Class<?> clazz, String actionName) throws AIProcessingException {
        this.clazz = clazz;
        this.actionName = actionName;
        this.actionMethod = methodFinder.findMethod(clazz, actionName); // use internal MethodFinder

        Agent predict = clazz.getAnnotation(Agent.class);
        if (predict != null) {
            this.groupDescription = predict.groupDescription();
            this.groupName = predict.groupName();
        }
        this.actionClassName = clazz.getName();
        initAction(this.actionMethod);
    }
    public GenericJavaMethodAction(Class clazz, Method actionMethod) throws AIProcessingException{
        init(clazz, actionMethod);
    }

    private void init(Class clazz, Method actionMethod) throws AIProcessingException {
        Agent predict = (Agent) clazz.getAnnotation(Agent.class);
               this.clazz = clazz;
        this.actionName = actionMethod.getName();
        this.actionMethod = actionMethod;
        if(predict != null) {
            this.groupDescription = predict.groupDescription();
            this.groupName = predict.groupName();
            this.prompt = predict.prompt();
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
        jsonStr = jsonUtils.convertMethodTOJsonString(actionMethod);
        Action action = (Action)actionMethod.getAnnotation(Action.class);

        this.riskLevel = action.riskLevel();
        this.description = action.description();
        this.subprompt = action.prompt();
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
