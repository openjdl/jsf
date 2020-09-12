package com.openjdl.jsf.settings;

import com.openjdl.jsf.settings.definition.SettingsDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-09-10 14:48:06
 *
 * @author kidal
 * @since 0.3
 */
public interface SettingsDefaultsResolver {
  /**
   *
   */
  Object resolveSettingsDefaults(@NotNull SettingsDefinition definition, @NotNull Object originalKey, @NotNull Object key);
}
