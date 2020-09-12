package com.openjdl.jsf.settings;

import org.jetbrains.annotations.Nullable;

/**
 * Created at 2020-09-11 17:09:03
 *
 * @author kidal
 * @since 0.3
 */
public class DefaultInjectSettingsStorageProvider implements InjectSettingsStorageProvider {
  private String storageId;
  private boolean required;

  /**
   *
   */
  @Override
  public void init(@Nullable String storageId, boolean required) {
    this.storageId = storageId;
    this.required = required;
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
  @Override
  public boolean isRequired() {
    return required;
  }
}
