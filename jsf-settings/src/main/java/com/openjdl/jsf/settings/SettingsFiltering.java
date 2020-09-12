package com.openjdl.jsf.settings;

/**
 * Created at 2020-09-10 14:53:21
 *
 * @author kidal
 * @since 0.3
 */
public interface SettingsFiltering {
  /**
   *
   */
  default boolean filterByMicroServiceId(int microServiceId) {
    return true;
  }
}
