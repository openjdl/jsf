package com.openjdl.jsf.settings;

import com.openjdl.jsf.settings.definition.SettingsDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * Created at 2020-10-13 09:51:03
 *
 * @author kidal
 * @since 0.4
 */
public class DefaultSettingsStorageValueSource implements SettingsStorageValueSource {
  @Override
  public void initialize() {

  }

  @Override
  public SettingsMetadata loadMetadata(@NotNull SettingsDefinition definition) {
    return new SettingsMetadata(definition.getId(), -1, new Date(System.currentTimeMillis()));
  }

  @Override
  public <E> Collection<E> loadAll(@NotNull SettingsDefinition definition) {
    return Collections.emptySet();
  }
}
