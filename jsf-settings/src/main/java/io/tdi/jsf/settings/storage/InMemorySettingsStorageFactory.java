package io.tdi.jsf.settings.storage;

import io.tdi.jsf.settings.SettingsService;
import io.tdi.jsf.settings.SettingsServiceImpl;
import io.tdi.jsf.settings.SettingsStorage;
import io.tdi.jsf.settings.SettingsStorageFactory;
import io.tdi.jsf.settings.definition.SettingsDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-09-10 16:38:26
 *
 * @author kidal
 * @since 0.3
 */
public class InMemorySettingsStorageFactory implements SettingsStorageFactory {
  /**
   *
   */
  @NotNull
  @Override
  public <K, V> SettingsStorage<K, V> createStorage(@NotNull SettingsService service, @NotNull SettingsDefinition definition) {
    return new InMemorySettingsStorage<>((SettingsServiceImpl) service, definition);
  }
}
