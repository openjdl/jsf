package io.tdi.jsf.settings;

import io.tdi.jsf.settings.definition.SettingsDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  Object resolveSettingsDefaults(@NotNull SettingsDefinition definition, @NotNull Object originalKey);
}
