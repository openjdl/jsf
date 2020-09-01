package io.tdi.jsf.webflux.boot;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * Created at 2020-08-11 15:32:34
 *
 * @author kidal
 * @since 0.1.0
 */
@ConfigurationProperties(JsfWebFluxProperties.P_PATH)
public class JsfWebFluxProperties {
  public static final String P_PATH = "jsf.webflux";
  public static final String B_PATH = "jsf-webflux";

  public static final String P_ENABLED = P_PATH + ".enabled";
  public static final String P_CORS_ENABLED = P_PATH + ".cors.enabled";
  public static final String P_WEBSOCKET_ENABLED = P_PATH + ".websocket.enabled";

  public static final String B_WEBFLUX_SERVICE = B_PATH + "-WebFluxService";
  public static final String B_WEBSOCKET_HANDLER_BEAN_PREFIX = B_PATH + "-WebSocketHandler-";
  public static final String B_SESSION_MANAGER = B_PATH + "-SessionManager";

  public static String makeWebSocketHandlerBeanName(@NotNull String name) {
    return String.format("%s-%s", B_WEBSOCKET_HANDLER_BEAN_PREFIX, name);
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  private boolean enabled = true;
  private Cors cors = new Cors();
  private WebSocket websocket = new WebSocket();

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public static class Cors {
    private boolean enabled = true;
    private String pathPattern = "/**";
    private boolean allowCredentials = true;
    private List<String> allowedOrigins = Lists.newArrayList("*");
    private List<String> allowedHeaders = Lists.newArrayList("*");
    private List<String> allowedMethods = Lists.newArrayList("*");
    private List<String> exposedHeaders = Lists.newArrayList(HttpHeaders.SET_COOKIE);

    //--------------------------------------------------------------------------
    //
    //--------------------------------------------------------------------------

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public String getPathPattern() {
      return pathPattern;
    }

    public void setPathPattern(String pathPattern) {
      this.pathPattern = pathPattern;
    }

    public boolean isAllowCredentials() {
      return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
      this.allowCredentials = allowCredentials;
    }

    public List<String> getAllowedOrigins() {
      return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
      this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedHeaders() {
      return allowedHeaders;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
      this.allowedHeaders = allowedHeaders;
    }

    public List<String> getAllowedMethods() {
      return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
      this.allowedMethods = allowedMethods;
    }

    public List<String> getExposedHeaders() {
      return exposedHeaders;
    }

    public void setExposedHeaders(List<String> exposedHeaders) {
      this.exposedHeaders = exposedHeaders;
    }
  }

  public static class WebSocket {
    private boolean enabled = false;
    private String path = "";

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Cors getCors() {
    return cors;
  }

  public void setCors(Cors cors) {
    this.cors = cors;
  }

  public WebSocket getWebsocket() {
    return websocket;
  }

  public void setWebsocket(WebSocket websocket) {
    this.websocket = websocket;
  }
}
