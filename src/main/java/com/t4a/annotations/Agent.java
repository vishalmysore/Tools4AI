package com.t4a.annotations;

import com.t4a.api.ToolsConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to mark the class as a Agent group
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Agent {
    String groupName() default ToolsConstants.GROUP_NAME;
    String groupDescription() default ToolsConstants.GROUP_DESCRIPTION;
}