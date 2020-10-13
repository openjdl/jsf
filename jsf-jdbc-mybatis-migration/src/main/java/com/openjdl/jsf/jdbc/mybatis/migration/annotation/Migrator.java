package com.openjdl.jsf.jdbc.mybatis.migration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2020-10-11 19:20:02
 *
 * @author kidal
 * @since 0.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Migrator {
  /**
   * 顺序
   */
  String order() default "";
}
