package com.t4a.predict;

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
    String groupName() default "default";
    String groupDescription() default "group for all the tasks";
}
