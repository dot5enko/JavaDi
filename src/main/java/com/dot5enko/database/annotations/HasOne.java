package com.dot5enko.database.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(HasOneArray.class)
public @interface HasOne {

    String from();

    String to() default "id";

    Class<?> value();

    String alias() default "";

}
