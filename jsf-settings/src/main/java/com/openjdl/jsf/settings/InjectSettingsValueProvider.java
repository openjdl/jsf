package com.openjdl.jsf.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created at 2020-09-10 14:18:26
 *
 * @author kidal
 * @since 3.0
 */
public interface InjectSettingsValueProvider {
  /**
   *
   */
  void init(@Nullable String storageId, @NotNull String key, boolean required, boolean applyDefaultsResolver);

  /**
   *
   */
  @Nullable
  String getStorageId();

  /**
   *
   */
  @NotNull
  String getKey();

  /**
   *
   */
  boolean isRequired();

  /**
   *
   */
  boolean isApplyDefaultsResolver();
}
