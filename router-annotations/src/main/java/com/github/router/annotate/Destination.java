package com.github.router.annotate;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解作用域
 */
@Target(ElementType.TYPE)
/**
 * 注解生命周期，即作用在字节码中
 */
@Retention(RetentionPolicy.CLASS)
public @interface Destination {
    String url();

    String description() default "";
}

