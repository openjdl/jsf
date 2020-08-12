package org.kidal.jsf.webflux.handler;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kidal.jsf.core.utils.JsonUtils;
import org.kidal.jsf.core.utils.Jsonable;
import org.kidal.jsf.core.utils.ReflectionUtils;
import org.kidal.jsf.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.reactive.socket.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Created at 2020-08-11 16:28:25
 *
 * @author kidal
 * @since 0.1.0
 */
public abstract class BaseSimpleWebSocketHandler implements WebSocketHandler, CorsConfigurationSource {
  /**
   * 日志
   */
  protected final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * 会话
   */
  private final ConcurrentMap<String, WebSocketSession> sessionMap = Maps.newConcurrentMap();

  /**
   * 应答的数据
   */
  private final ConcurrentMap<String, Request> requestMap = Maps.newConcurrentMap();

  /**
   * 消息处理器
   */
  private final Object typeHandlersLock = new Object();

  /**
   * 消息处理器
   */
  private Map<String, Method> typeHandlers;

  /**
   *
   */
  protected BaseSimpleWebSocketHandler() {
  }

  /**
   * 获取会话ID
   */
  @Nullable
  protected abstract String getSessionId(@NotNull WebSocketSession session, @NotNull Map<String, String> queryParameters);

  /**
   *
   */
  @Nullable
  public <T> T sendDataToSession(
    @NotNull String sessionId,
    @NotNull String type,
    @Nullable Object requestData,
    @Nullable Class<T> responseClass,
    @Nullable Integer timeoutMillis
  ) {
    // 获取会话
    final WebSocketSession session = sessionMap.get(sessionId);
    if (session == null) {
      return null;
    }

    // 准备请求
    final Request request = Request.of(nextMessageId(), requestData, responseClass);
    requestMap.put(request.getId(), request);

    // 准备载荷
    Payload payload = new Payload();
    payload.setId(request.getId());
    payload.setType(type);
    payload.setDirection(Payload.SC);
    payload.setData(requestData != null ? JsonUtils.toPrettyString(requestData) : null);

    // 发送消息
    WebSocketMessage message = session.textMessage(payload.toRawPayload());
    session.send(Mono.just(message));

    // 不需要答复
    if (responseClass == null) {
      requestMap.remove(request.getId());
      return null;
    }

    // 等待答复
    try {
      payload.wait(timeoutMillis != null ? timeoutMillis : 30 * 1000);
    } catch (InterruptedException e) {
      return null;
    } finally {
      requestMap.remove(request.getId());
    }

    // 获取答复
    Object responseData = request.getResponseData();
    if (responseData == null) {
      return null;
    }

    //noinspection unchecked
    return (T) responseData;
  }

  /**
   * 广播消息
   */
  public void broadcast(
    @NotNull String type,
    @Nullable Object requestData
  ) {
    // 准备载荷
    final Payload payload = new Payload();
    payload.setId(nextMessageId());
    payload.setType(type);
    payload.setDirection(Payload.SC);
    payload.setData(requestData != null ? JsonUtils.toPrettyString(requestData) : null);
    final String message = payload.toRawPayload();

    // 发送
    for (WebSocketSession session : sessionMap.values()) {
      try {
        session.textMessage(message);
      } catch (Exception e) {
        log.warn("", e);
      }
    }
  }

  /**
   * 获取下一个消息号
   */
  @NotNull
  private String nextMessageId() {
    return UUID.randomUUID().toString().replace("-", "").toLowerCase();
  }

  /**
   * 分发消息
   */
  @NotNull
  private Payload dispatch(String sessionId, @NotNull Payload payload) {
    if (typeHandlers == null) {
      synchronized (typeHandlersLock) {
        if (typeHandlers == null) {
          HashMap<String, Method> map = Maps.newHashMap();

          ReflectionUtils.doWithMethods(
            getClass(),
            method -> {
              OnClientMessage annotation = method.getAnnotation(OnClientMessage.class);
              String type = StringUtils.isBlank(annotation.value()) ? method.getName() : annotation.value();
              map.put(type, method);
            },
            method -> method.isAnnotationPresent(OnClientMessage.class)
          );

          typeHandlers = map;
        }
      }
    }

    Method method = typeHandlers.get(payload.getType());
    if (method == null) {
      return payload.toResponse(null);
    }

    try {
      return (Payload) method.invoke(this, sessionId, payload);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("", e);
      return payload.toResponse(null);
    }
  }

  /**
   * 处理客户端消息
   */
  @NotNull
  @Override
  public Mono<Void> handle(@NotNull WebSocketSession session) {
    // 获取 QueryString
    HandshakeInfo handshakeInfo = session.getHandshakeInfo();
    String query = handshakeInfo.getUri().getQuery();
    if (query == null) {
      log.warn("Incorrect incoming query: `EMPTY STRING`");
      return session.close(CloseStatus.REQUIRED_EXTENSION);
    }

    // 获取查询参数
    String[] parts = query.split("&");
    List<String[]> pairs = Arrays.stream(parts).map(pair -> pair.split("=")).collect(Collectors.toList());
    Map<String, String> queryParameters = pairs.stream().collect(Collectors.toMap(it -> it[0], it -> it.length > 1 ? it[1] : ""));
    String sessionId = getSessionId(session, queryParameters);
    if (sessionId == null) {
      return session.close();
    }

    // 添加会话
    WebSocketSession prev = sessionMap.put(sessionId, session);

    // 关闭老会话
    if (prev != null) {
      prev.close();
    }

    // 开始收发消息
    Flux<WebSocketMessage> output =
      session
        .receive()
        .map(message -> Payload.of(message.getPayloadAsText()))
        .map(payload -> session.textMessage(onReceivePayload(sessionId, payload).toRawPayload()))
        .doFinally(signalType -> {
          log.warn("doFinally: {}, {}", signalType, sessionId);
          session.close();
          sessionMap.remove(sessionId);
        });
    return session.send(output);
  }

  @NotNull
  private Payload onReceivePayload(@NotNull String sessionId, @NotNull Payload payload) {
    // 检查
    if (payload.getId().equals("")) {
      return payload.toResponse(null);
    } else if (payload.getDirection().equals(Payload.SC)) {
      Request request = requestMap.get(payload.getId());
      if (request != null) {
        request.onResponse(payload.getData());
        request.notify();
      }
      return payload.toResponse(null);
    } else if (payload.getDirection().equals(Payload.CS)) {
      return dispatch(sessionId, payload);
    } else {
      return payload.toResponse(null);
    }
  }

  /**
   * 跨域
   */
  @Override
  public CorsConfiguration getCorsConfiguration(@NotNull ServerWebExchange exchange) {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("*");
    return configuration;
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  /**
   * 处理器
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface OnClientMessage {
    String value() default "";
  }

  /**
   * 载荷
   */
  public static class Payload {
    public static final String SC = "sc";
    public static final String CS = "cs";

    @NotNull
    private String id = "";
    @NotNull
    private String direction = "";
    @NotNull
    private String type = "";
    @Nullable
    private String data;

    /**
     *
     */
    @NotNull
    public static Payload of(@NotNull String rawPayload) {
      String[] lines = rawPayload.split("\n", 4);
      Payload payload = new Payload();

      if (lines.length >= 1) {
        payload.setId(lines[0]);
      }
      if (lines.length >= 2) {
        payload.setDirection(lines[1]);
      }
      if (lines.length >= 3) {
        payload.setType(lines[2]);
      }
      if (lines.length >= 4) {
        payload.setData(lines[3]);
      }

      return payload;
    }

    /**
     *
     */
    @NotNull
    public String toRawPayload() {
      StringBuilder builder = new StringBuilder();
      builder.append(id).append("\n");
      builder.append(direction).append("\n");
      builder.append(type).append("\n");
      if (data != null) {
        builder.append(data);
      }
      return builder.toString();
    }

    /**
     *
     */
    @NotNull
    public Payload toResponse(@Nullable String data) {
      Payload payload = new Payload();
      payload.setId(id);
      payload.setDirection(direction);
      payload.setType(type);
      payload.setData(data);
      return payload;
    }

    //--------------------------------------------------------------------------
    //
    //--------------------------------------------------------------------------

    @NotNull
    public String getId() {
      return id;
    }

    public void setId(@NotNull String id) {
      this.id = id;
    }

    @NotNull
    public String getDirection() {
      return direction;
    }

    public void setDirection(@NotNull String direction) {
      this.direction = direction;
    }

    @NotNull
    public String getType() {
      return type;
    }

    public void setType(@NotNull String type) {
      this.type = type;
    }

    @Nullable
    public String getData() {
      return data;
    }

    public void setData(@Nullable String data) {
      this.data = data;
    }
  }

  /**
   * 请求
   */
  private static class Request implements Jsonable {
    @NotNull
    private String id = "";
    @Nullable
    private Object requestData;
    @NotNull
    private Date requestAt = new Date(System.currentTimeMillis());

    @Nullable
    private Class<?> responseClass;
    @Nullable
    private Object responseData;
    @Nullable
    private Date responseAt = null;

    /**
     *
     */
    @NotNull
    public static Request of(@NotNull String id, @Nullable Object requestData, @Nullable Class<?> responseClass) {
      Request request = new Request();
      request.setId(id);
      request.setRequestData(requestData);
      request.setResponseClass(responseClass);
      return request;
    }


    /**
     *
     */
    void onResponse(@Nullable String json) {
      if (json == null) {
        responseData = null;
      } else if (responseClass == null) {
        responseData = null;
      } else {
        responseData = JsonUtils.toObject(json, responseClass);
      }
      responseAt = new Date(System.currentTimeMillis());
    }

    //--------------------------------------------------------------------------
    //
    //--------------------------------------------------------------------------

    @NotNull
    public String getId() {
      return id;
    }

    public void setId(@NotNull String id) {
      this.id = id;
    }

    @Nullable
    public Object getRequestData() {
      return requestData;
    }

    public void setRequestData(@Nullable Object requestData) {
      this.requestData = requestData;
    }

    @NotNull
    public Date getRequestAt() {
      return requestAt;
    }

    public void setRequestAt(@NotNull Date requestAt) {
      this.requestAt = requestAt;
    }

    @Nullable
    public Class<?> getResponseClass() {
      return responseClass;
    }

    public void setResponseClass(@Nullable Class<?> responseClass) {
      this.responseClass = responseClass;
    }

    @Nullable
    public Object getResponseData() {
      return responseData;
    }

    public void setResponseData(@Nullable Object responseData) {
      this.responseData = responseData;
    }

    @Nullable
    public Date getResponseAt() {
      return responseAt;
    }

    public void setResponseAt(@Nullable Date responseAt) {
      this.responseAt = responseAt;
    }
  }
}
