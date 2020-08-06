package org.kidal.jsf.graphql.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created at 2020-08-06 10:26:03
 *
 * @author kidal
 * @since 0.1.0
 */
@ConfigurationProperties(JsfGraphqlProperties.P_PATH)
public class JsfGraphqlProperties {
  /**
   * 配置参数路径
   */
  public static final String P_PATH = "jsf.graphql";

  /**
   * 豆子路径
   */
  public static final String B_PATH = "jsf-graphql";

  /**
   * 启用Graphql
   */
  public static final String P_ENABLED = P_PATH + ".enabled";

  /**
   * 1
   */
  public static final String B_GRAPHQL_SERVICE = B_PATH + "-GraphqlService";

  /**
   * 是否启用Graphql模块
   */
  private boolean enabled;

  //--------------------------------------------------------------------------
  // Getters & Setters
  //--------------------------------------------------------------------------

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
