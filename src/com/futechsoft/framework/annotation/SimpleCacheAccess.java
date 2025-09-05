package com.futechsoft.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleCacheAccess {
    /**
     * 캐시 이름
     */
    String value() default "";

    /**
     * TTL (Time To Live) - 초 단위
     * 기본값: 300초 (5분)
     */
    long ttl() default 300;


    boolean isCode() default false;
}