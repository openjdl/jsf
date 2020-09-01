package io.tdi.jsf.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2020-08-06 17:20:16
 *
 * @author kidal
 * @since 0.1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlaveDataSourceMapping {
  /**
   * 数据源分组ID.
   */
  String value() default "";
}
