package com.t4a.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Prompt {
    String describe() default ""; // Optional field for describing the field
    String dateFormat() default "";  // Optional field for specifying the format
    boolean ignore() default false;  // Optional field to indicate if the field/parameter should be ignored
}