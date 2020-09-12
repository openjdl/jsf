package com.openjdl.jsf.settings.accessor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created at 2020-09-10 15:26:27
 *
 * @author kidal
 * @since 0.3
 */
public interface Getter {
  /**
   *
   */
  @Nullable
  Object getValue(@NotNull Object target);
}
