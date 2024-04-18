package com.t4a.annotations;

import com.t4a.api.ActionRisk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Predict {
    String actionName();
    String description();

    ActionRisk riskLevel() default ActionRisk.LOW;
    String groupName() default "No Group";
    String groupDescription() default "tasks which are not categorized";
}
