package com.openjdl.jsf.settings.accessor;

import com.openjdl.jsf.core.utils.ReflectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created at 2020-09-10 15:53:48
 *
 * @author kidal
 * @since 0.3
 */
public class MethodGetter implements Getter {
  @NotNull
  private final Method method;

  /**
   *
   */
  public MethodGetter(@NotNull Method method) {
    ReflectionUtils.makeAccessible(method);

    this.method = method;
  }

  /**
   *
   */
  @Nullable
  @Override
  public Object getValue(@NotNull Object target) {
    try {
      return method.invoke(target);
    } catch (IllegalAccessException | InvocationTargetException e) {
      ExceptionUtils.rethrow(e);
      return null;
    }
  }

  /**
   *
   */
  @NotNull
  public Method getMethod() {
    return method;
  }
}
