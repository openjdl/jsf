package io.tdi.jsf.settings;

import org.jetbrains.annotations.Nullable;

/**
 * Created at 2020-09-10 14:16:06
 *
 * @author kidal
 * @since 0.3
 */
public interface InjectSettingsStorageProvider {
  /**
   *
   */
  void init(@Nullable String storageId, boolean required);

  /**
   *
   */
  @Nullable
  String getStorageId();

  /**
   *
   */
  boolean isRequired();
}
