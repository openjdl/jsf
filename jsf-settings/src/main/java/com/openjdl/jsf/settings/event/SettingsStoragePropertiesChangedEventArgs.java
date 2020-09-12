package com.openjdl.jsf.settings.event;

import java.util.Collections;
import java.util.Map;

/**
 * Created at 2020-09-10 14:41:24
 *
 * @author kidal
 * @since 0.3
 */
public class SettingsStoragePropertiesChangedEventArgs<K, V> implements SettingsObserverEventArgs {
  private final Map<K, V> addedProperties;
  private final Map<K, V> removedProperties;
  private final Map<K, V> changedProperties;

  /**
   *
   */
  public SettingsStoragePropertiesChangedEventArgs(Map<K, V> addedProperties, Map<K, V> removedProperties, Map<K, V> changedProperties) {
    this.addedProperties = Collections.unmodifiableMap(addedProperties);
    this.removedProperties = Collections.unmodifiableMap(removedProperties);
    this.changedProperties = Collections.unmodifiableMap(changedProperties);
  }

  /**
   *
   */
  public boolean hasAdded() {
    return addedProperties.size() > 0;
  }

  /**
   *
   */
  public boolean hasRemoved() {
    return removedProperties.size() > 0;
  }

  /**
   *
   */
  public boolean hasChanged() {
    return changedProperties.size() > 0;
  }

  /**
   *
   */
  public Map<K, V> getAddedProperties() {
    return addedProperties;
  }

  /**
   *
   */
  public Map<K, V> getRemovedProperties() {
    return removedProperties;
  }

  /**
   *
   */
  public Map<K, V> getChangedProperties() {
    return changedProperties;
  }
}
