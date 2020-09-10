package io.tdi.jsf.settings;

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
  public static final String INITIALIZING = "Initializing";

  /**
   * Before {@link ConfigurationManager#refresh(Storage[])}.
   */
  public static final String BEFORE_REFRESH_ALL = "BeforeRefreshAll";

  /**
   * Before {@link Storage#refresh()}
   */
  public static final String BEFORE_REFRESH = "BeforeRefresh";

  /**
   * After {@link Storage#refresh()} when any property changed.
   */
  public static final String PROPERTY_CHANGED = "PropertyChanged";

  /**
   * After {@link Storage#refresh()}
   */
  public static final String AFTER_REFRESH = "AfterRefresh";

  /**
   * After {@link ConfigurationManager#refresh(Storage[])}
   */
  public static final String AFTER_REFRESH_ALL = "AfterRefreshAll";

  //
  private SettingsObserveTags() {
    throw new IllegalStateException();
  }
}
