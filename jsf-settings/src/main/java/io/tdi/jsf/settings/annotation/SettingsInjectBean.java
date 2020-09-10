package io.tdi.jsf.settings.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2020-09-10 14:14:43
 *
 * @author kidal
 * @since 0.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SettingsInjectBean {
  /**
   * {@link #beanName()}
   */
  @AliasFor("beanName")
  String value() default "";

  /**
   *
   */
  @AliasFor("value")
  String beanName() default "";
}
