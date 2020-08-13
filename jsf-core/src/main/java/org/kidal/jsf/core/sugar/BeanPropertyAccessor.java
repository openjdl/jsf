package org.kidal.jsf.core.sugar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created at 2020-08-13 14:26:44
 *
 * @author kidal
 * @since 0.1.0
 */
public interface BeanPropertyAccessor {
  /**
   *
   */
  @Nullable
  <T> T getProperty(@NotNull String propertyName);
}
