package com.t4a.predict;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Tools {
    String describe() default ""; // Optional field for describing the field
    String format() default "";  // Optional field for specifying the format
}