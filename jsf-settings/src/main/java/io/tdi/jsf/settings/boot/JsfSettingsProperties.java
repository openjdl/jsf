package io.tdi.jsf.settings.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created at 2020-09-10 16:40:34
 *
 * @author kidal
 * @since 0.3
 */
@ConfigurationProperties(JsfSettingsProperties.P_PATH)
public class JsfSettingsProperties {
  public static final String P_PATH = "jsf.settings";
  public static final String B_PATH = "jsf-settings";

  public static final String P_ENABLED = P_PATH + ".enabled";
  public static final String B_SETTINGS_SERVICE = B_PATH + "-SettingsService";

  //--------------------------------------------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------------------------------------------

  private boolean enabled = true;
  private String packagesToScan;
  private String autoRefreshCron = "0 0/1 * * * ?";

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getPackagesToScan() {
    return packagesToScan;
  }

  public void setPackagesToScan(String packagesToScan) {
    this.packagesToScan = packagesToScan;
  }

  public String getAutoRefreshCron() {
    return autoRefreshCron;
  }

  public void setAutoRefreshCron(String autoRefreshCron) {
    this.autoRefreshCron = autoRefreshCron;
  }
}
