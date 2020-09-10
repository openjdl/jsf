package io.tdi.jsf.core.observe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created at 2020-09-10 14:56:02
 *
 * @author kidal
 * @since 0.3
 */
public interface TaggedObservable {
  /**
   *
   */
  void addObserver(@NotNull String tag, @NotNull TaggedObserver observer);

  /**
   *
   */
  void deleteObserver(@NotNull String tag, @NotNull TaggedObserver observer);

  /**
   *
   */
  void notifyObservers(@NotNull String tag);

  /**
   *
   */
  void notifyObservers(@NotNull String tag, @Nullable Object arg);

  /**
   *
   */
  void deleteObservers(@NotNull String tag);
}
