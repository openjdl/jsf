package io.tdi.jsf.graphql.webflux.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created at 2020-08-06 10:46:53
 *
 * @author kidal
 * @since 0.1.0
 */
@ConfigurationProperties(JsfGraphqlWebFluxProperties.P_PATH)
public class JsfGraphqlWebFluxProperties {
  /**
   * 配置参数路径
   */
  public static final String P_PATH = "jsf.graphql.webflux";

  /**
   * 豆子路径
   */
  public static final String B_PATH = "jsf-graphql-webflux";

  /**
   *
   */
  public static final String P_ENABLED = P_PATH + ".enabled";

  /**
   *
   */
  public static final String B_GRAPHQL_WEBFLUX_SERVICE = B_PATH + "-GraphqlWebfluxService";

  /**
   *
   */
  private boolean enabled;

  /**
   *
   */
  private String endpoint = "/graphql";

  /**
   *
   */
  private String subscriptionEndpoint = "/graphql-subscription";

  //--------------------------------------------------------------------------
  // Getters & Setters
  //--------------------------------------------------------------------------

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getSubscriptionEndpoint() {
    return subscriptionEndpoint;
  }

  public void setSubscriptionEndpoint(String subscriptionEndpoint) {
    this.subscriptionEndpoint = subscriptionEndpoint;
  }
}
