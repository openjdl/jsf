package com.openjdl.jsf.webflux.boot;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
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
  public static final String P_MODBUS_DTU_ENABLED = P_PATH + ".modbus-dtu.enabled";
  public static final String P_WEBSOCKET_ENABLED = P_PATH + ".websocket.enabled";
  public static final String P_SOCKET_ENABLED = P_PATH + ".socket.enabled";

  public static final String B_WEBFLUX_SERVICE = B_PATH + "-WebFluxService";
  public static final String B_MODBUS_DTU_SESSION_MANAGER = B_PATH + "-ModbusDtuSessionManager";
  public static final String B_WEBSOCKET_HANDLER_BEAN_PREFIX = B_PATH + "-WebSocketHandler-";
  public static final String B_WEBSOCKET_SESSION_MANAGER = B_PATH + "-WebSocketSessionManager";
  public static final String B_SOCKET_SESSION_MANAGER = B_PATH + "-SocketSessionManager";

  public static String makeWebSocketHandlerBeanName(@NotNull String name) {
    return String.format("%s-%s", B_WEBSOCKET_HANDLER_BEAN_PREFIX, name);
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  private boolean enabled = true;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  //--------------------------------------------------------------------------------------------------------------
  // Cors
  //--------------------------------------------------------------------------------------------------------------
  //region

  private Cors cors = new Cors();

  public Cors getCors() {
    return cors;
  }

  public void setCors(Cors cors) {
    this.cors = cors;
  }

  public static class Cors {
    private boolean enabled = true;
    private String pathPattern = "/**";
    private boolean allowCredentials = true;
    private List<String> allowedOrigins = Lists.newArrayList("*");
    private List<String> allowedHeaders = Lists.newArrayList("*");
    private List<String> allowedMethods = Lists.newArrayList("*");
    private List<String> exposedHeaders = Lists.newArrayList(HttpHeaders.SET_COOKIE);

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

  //endregion

  //--------------------------------------------------------------------------------------------------------------
  // Modbus
  //--------------------------------------------------------------------------------------------------------------
  //region

  private ModbusDtu modbusDtu = new ModbusDtu();

  public ModbusDtu getModbusDtu() {
    return modbusDtu;
  }

  public void setModbusDtu(ModbusDtu modbusDtu) {
    this.modbusDtu = modbusDtu;
  }

  public static class ModbusDtu {
    private boolean enabled = false;
    private List<ModbusDtuServer> servers = new ArrayList<>();

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public List<ModbusDtuServer> getServers() {
      return servers;
    }

    public void setServers(List<ModbusDtuServer> servers) {
      this.servers = servers;
    }
  }

  public static class ModbusDtuServer {
    private int port = 0;
    private int bossThreads = 1;
    private int workerThreads = 0;
    private int backlog = 128;

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public int getBossThreads() {
      return bossThreads;
    }

    public void setBossThreads(int bossThreads) {
      this.bossThreads = bossThreads;
    }

    public int getWorkerThreads() {
      return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
      this.workerThreads = workerThreads;
    }

    public int getBacklog() {
      return backlog;
    }

    public void setBacklog(int backlog) {
      this.backlog = backlog;
    }
  }

  //endregion

  //--------------------------------------------------------------------------------------------------------------
  // WebSocket
  //--------------------------------------------------------------------------------------------------------------
  //region

  private WebSocket websocket = new WebSocket();

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

  public WebSocket getWebsocket() {
    return websocket;
  }

  public void setWebsocket(WebSocket websocket) {
    this.websocket = websocket;
  }

  //endregion

  //--------------------------------------------------------------------------------------------------------------
  // Socket
  //--------------------------------------------------------------------------------------------------------------
  //region

  private Socket socket = new Socket();

  public Socket getSocket() {
    return socket;
  }

  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  public static class Socket {
    private boolean enabled = false;
    private List<String> packagesToScan = new ArrayList<>();
    private List<SocketServer> servers = new ArrayList<>();

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public List<String> getPackagesToScan() {
      return packagesToScan;
    }

    public void setPackagesToScan(List<String> packagesToScan) {
      this.packagesToScan = packagesToScan;
    }

    public List<SocketServer> getServers() {
      return servers;
    }

    public void setServers(List<SocketServer> servers) {
      this.servers = servers;
    }
  }

  public static class SocketServer {
    private int port = 0;
    private int bossThreads = 1;
    private int workerThreads = 0;
    private int backlog = 128;

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public int getBossThreads() {
      return bossThreads;
    }

    public void setBossThreads(int bossThreads) {
      this.bossThreads = bossThreads;
    }

    public int getWorkerThreads() {
      return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
      this.workerThreads = workerThreads;
    }

    public int getBacklog() {
      return backlog;
    }

    public void setBacklog(int backlog) {
      this.backlog = backlog;
    }
  }

  //endregion
}
