package com.openjdl.jsf.settings;

/**
 * Created at 2020-09-10 14:20:04
 *
 * @author kidal
 * @since 0.3
 */
public class SettingsObserveTags {
  /**
   * On initializing.
   */
  public static final String INITIALIZED = "Initialized";

  /**
   * Before {@link SettingsService#refresh(SettingsStorage[])}.
   */
  public static final String BEFORE_REFRESH_ALL = "BeforeRefreshAll";

  /**
   * Before {@link SettingsStorage#refresh()}
   */
  public static final String BEFORE_REFRESH = "BeforeRefresh";

  /**
   * After {@link SettingsStorage#refresh()} when any property changed.
   */
  public static final String PROPERTY_CHANGED = "PropertyChanged";

  /**
   * After {@link SettingsStorage#refresh()}
   */
  public static final String AFTER_REFRESH = "AfterRefresh";

  /**
   * After {@link SettingsService#refresh(SettingsStorage[])}
   */
  public static final String AFTER_REFRESH_ALL = "AfterRefreshAll";

  //
  private SettingsObserveTags() {
    throw new IllegalStateException();
  }
}
