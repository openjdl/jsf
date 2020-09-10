package io.tdi.jsf.settings.annotation;

import io.tdi.jsf.settings.SettingsObserveTags;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2020-09-10 14:20:54
 *
 * @author kidal
 * @since 0.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SettingsStorageObserver {
  /**
   *
   */
  @AliasFor("ids")
  String[] value() default {};

  /**
   *
   */
  @AliasFor("value")
  String[] ids() default {};

  /**
   *
   */
  Class<?>[] types() default {};

  /**
   * configuration key.
   * only support java.lang.String key type.
   * valid when tags contains {@link SettingsObserveTags#AFTER_REFRESH}.
   */
  String[] keys() default {};

  /**
   *
   */
  String[] tags() default {SettingsObserveTags.INITIALIZING, SettingsObserveTags.AFTER_REFRESH_ALL};

  /**
   * @see org.springframework.core.Ordered
   */
  int order() default 0;
}
