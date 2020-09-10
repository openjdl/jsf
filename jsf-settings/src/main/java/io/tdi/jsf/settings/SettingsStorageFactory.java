package io.tdi.jsf.settings;

import io.tdi.jsf.settings.definition.SettingsDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-09-10 15:16:31
 *
 * @author kidal
 * @since 0.3
 */
public interface SettingsStorageFactory {
  /**
   *
   */
  @NotNull
  <K, V> SettingsStorage<K, V> createStorage(@NotNull SettingsService service, @NotNull SettingsDefinition definition);
}
