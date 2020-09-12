package com.openjdl.jsf.settings.storage;

import com.openjdl.jsf.settings.SettingsService;
import com.openjdl.jsf.settings.SettingsServiceImpl;
import com.openjdl.jsf.settings.SettingsStorage;
import com.openjdl.jsf.settings.SettingsStorageFactory;
import com.openjdl.jsf.settings.definition.SettingsDefinition;
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
