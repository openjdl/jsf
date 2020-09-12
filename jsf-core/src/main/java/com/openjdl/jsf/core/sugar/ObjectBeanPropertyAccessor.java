package com.openjdl.jsf.core.sugar;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

/**
 * Created at 2020-08-13 15:52:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class ObjectBeanPropertyAccessor implements BeanPropertyAccessor {
  /**
   *
   */
  @NotNull
  private final Object bean;

  /**
   *
   */
  public ObjectBeanPropertyAccessor(@NotNull Object bean) {
    this.bean = bean;
  }

  /**
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getProperty(@NotNull String propertyName) {
    try {
      return (T) BeanUtils.getProperty(bean, propertyName);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      ExceptionUtils.rethrow(e);
      return null;
    }
  }
}
