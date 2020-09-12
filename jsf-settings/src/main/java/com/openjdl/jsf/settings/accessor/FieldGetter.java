package com.openjdl.jsf.settings.accessor;

import com.openjdl.jsf.core.utils.ReflectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Created at 2020-09-10 15:28:37
 *
 * @author kidal
 * @since 0.3
 */
public class FieldGetter implements Getter {
  @NotNull
  private final Field field;

  /**
   *
   */
  public FieldGetter(@NotNull Field field) {
    ReflectionUtils.makeAccessible(field);

    this.field = field;
  }

  /**
   *
   */
  @Nullable
  @Override
  public Object getValue(@NotNull Object target) {
    try {
      return field.get(target);
    } catch (IllegalAccessException e) {
      ExceptionUtils.rethrow(e);
      return null;
    }
  }

  /**
   *
   */
  @NotNull
  public Field getField() {
    return field;
  }
}
