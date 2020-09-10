package io.tdi.jsf.settings.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

/**
 * Created at 2020-09-10 14:13:56
 *
 * @author kidal
 * @since 0.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SettingsIndex {
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

  /**
   *
   */
  boolean unique() default false;

  /**
   *
   */
  Class<? extends Comparator> comparatorType() default Comparator.class;
}
