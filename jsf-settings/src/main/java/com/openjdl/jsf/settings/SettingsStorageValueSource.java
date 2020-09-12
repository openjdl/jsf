package com.openjdl.jsf.settings;

import com.openjdl.jsf.settings.definition.SettingsDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created at 2020-09-10 14:43:08
 *
 * @author kidal
 * @since 0.3
 */
public interface SettingsStorageValueSource {
  /**
   *
   */
  void initialize();

  /**
   *
   */
  SettingsMetadata loadMetadata(@NotNull SettingsDefinition definition);

  /**
   *
   */
  <E> Collection<E> loadAll(@NotNull SettingsDefinition definition);
}
