package io.tdi.jsf.graphql.boot;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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

  /**
   * 扫描的目录
   */
  @NotNull
  private List<String> pathsToScan = Lists.newArrayList();

  //--------------------------------------------------------------------------
  // Getters & Setters
  //--------------------------------------------------------------------------

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @NotNull
  public List<String> getPathsToScan() {
    return pathsToScan;
  }

  public void setPathsToScan(@NotNull List<String> pathsToScan) {
    this.pathsToScan = pathsToScan;
  }
}
