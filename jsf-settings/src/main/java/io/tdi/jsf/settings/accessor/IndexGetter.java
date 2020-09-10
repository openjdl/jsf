package io.tdi.jsf.settings.accessor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * Created at 2020-09-10 15:29:32
 *
 * @author kidal
 * @since 0.3
 */
public interface IndexGetter {
  /**
   *
   */
  @NotNull
  String getIndexName();

  /**
   *
   */
  boolean isUnique();

  /**
   *
   */
  Comparator<?> getComparator();

  /**
   *
   */
  @Nullable
  Object getValue(@NotNull Object target);
}
