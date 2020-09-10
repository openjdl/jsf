package io.tdi.jsf.settings;

import org.jetbrains.annotations.NotNull;

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
  void init(@NotNull String id, @NotNull String key, boolean required, boolean applyDefaults);

  /**
   *
   */
  @NotNull
  String getId();

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
  boolean isApplyDefaults();
}
