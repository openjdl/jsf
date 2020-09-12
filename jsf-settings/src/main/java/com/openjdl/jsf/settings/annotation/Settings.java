package com.openjdl.jsf.settings.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2020-09-10 12:30:02
 *
 * @author kidal
 * @since 0.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Settings {
  /**
   * {@link #id()}
   */
  @AliasFor("id")
  String value() default "";

  /**
   * {@link #value()}
   */
  @AliasFor("value")
  String id() default "";
}
