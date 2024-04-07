package com.t4a.predict;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Prompt {
    String describe() default ""; // Optional field for describing the field
    String dateFormat() default "";  // Optional field for specifying the format
}