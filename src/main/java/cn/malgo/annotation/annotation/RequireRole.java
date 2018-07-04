package cn.malgo.annotation.annotation;

import cn.malgo.annotation.enums.AnnotationRoleStateEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
  AnnotationRoleStateEnum[] value() default {};
}
