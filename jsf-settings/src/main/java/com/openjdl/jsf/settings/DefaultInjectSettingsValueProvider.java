package com.openjdl.jsf.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created at 2020-09-11 17:08:12
 *
 * @author kidal
 * @since 0.3
 */
public class DefaultInjectSettingsValueProvider implements InjectSettingsValueProvider {
  private String storageId;
  private String key;
  private boolean required;
  private boolean applyDefaultsResolver;

  /**
   *
   */
  @Override
  public void init(@Nullable String storageId, @NotNull String key, boolean required, boolean applyDefaultsResolver) {
    this.storageId = storageId;
    this.key = key;
    this.required = required;
    this.applyDefaultsResolver = applyDefaultsResolver;
  }

  /**
   *
   */
  @Nullable
  @Override
  public String getStorageId() {
    return storageId;
  }

  /**
   *
   */
  @NotNull
  @Override
  public String getKey() {
    return key;
  }

  /**
   *
   */
  @Override
  public boolean isRequired() {
    return required;
  }

  /**
   *
   */
  @Override
  public boolean isApplyDefaultsResolver() {
    return applyDefaultsResolver;
  }
}
