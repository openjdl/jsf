package com.openjdl.jsf.jdbc.mybatis.migration.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 2020-10-11 19:32:06
 *
 * @author kidal
 * @since 0.4
 */
@ConfigurationProperties(JsfJdbcMybatisMigrationProperties.P_PATH)
public class JsfJdbcMybatisMigrationProperties {
  public static final String P_PATH = "jsf.jdbc.mybatis.migration";
  public static final String B_PATH = "jsf-jdbc-mybatis-migration";

  public static final String P_ENABLED = P_PATH + ".enabled";

  public static final String B_JDBC_MYBATIS_MIGRATION_SERVICE = B_PATH + "-JdbcMybatisMigrationService";

  //--------------------------------------------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------------------------------------------

  private boolean enabled;
  private String tablePrefix = "jsf_";
  private List<String> cleanupGroups = new ArrayList<>();

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getTablePrefix() {
    return tablePrefix;
  }

  public void setTablePrefix(String tablePrefix) {
    this.tablePrefix = tablePrefix;
  }

  public List<String> getCleanupGroups() {
    return cleanupGroups;
  }

  public void setCleanupGroups(List<String> cleanupGroups) {
    this.cleanupGroups = cleanupGroups;
  }
}
