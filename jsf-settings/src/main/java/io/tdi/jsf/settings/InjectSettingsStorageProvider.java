package io.tdi.jsf.settings;

import org.jetbrains.annotations.NotNull;

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
  void init(@NotNull String id, boolean required);

  /**
   *
   */
  @NotNull
  String getId();

  /**
   *
   */
  boolean isRequired();
}
