package com.t4a.annotations;

import com.t4a.api.ActionRisk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {
    String description() default "";
    ActionRisk riskLevel() default ActionRisk.LOW;

}