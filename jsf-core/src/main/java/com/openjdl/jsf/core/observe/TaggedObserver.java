package com.openjdl.jsf.core.observe;

import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-09-10 14:56:18
 *
 * @author kidal
 * @since 0.3
 */
public interface TaggedObserver {
  /**
   *
   */
  void onObservableChanged(@NotNull TaggedObservable o, @NotNull String tag, @NotNull Object arg);
}
