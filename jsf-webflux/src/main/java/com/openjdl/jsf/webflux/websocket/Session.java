package com.openjdl.jsf.webflux.websocket;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.openjdl.jsf.core.cipher.UserIdentificationNumber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * Created at 2020-08-12 12:17:56
 *
 * @author kidal
 * @since 0.1.0
 */
public class Session {
  /**
   * 日志器
   */
  private static final Logger LOG = LoggerFactory.getLogger(Session.class);

  /**
   * 会话管理器
   */
  @NotNull
  private final SessionManager sessionManager;

  /**
   * 会话
   */
  @NotNull
  private final WebSocketSession webSocketSession;

  /**
   * 创建于
   */
  @NotNull
  private final Date createdAt = new Date(System.currentTimeMillis());

  /**
   * 上下文
   */
  @NotNull
  private final ConcurrentMap<String, Object> context = Maps.newConcurrentMap();

  /**
   * 用户身份识别码
   */
  @Nullable
  private UserIdentificationNumber uin;

  /**
   * 认证于
   */
  @Nullable
  private Date signInAt;

  /**
   * 关闭于
   */
  @Nullable
  private Date closedAt;

  /**
   * Flux
   */
  @Nullable
  private FluxSink<WebSocketMessage> sink;

  /**
   *
   */
  public Session(@NotNull SessionManager sessionManager,
                 @NotNull WebSocketSession webSocketSession) {
    this.sessionManager = sessionManager;
    this.webSocketSession = webSocketSession;
  }

  /**
   * 是否已认证
   */
  public boolean isSignedIn() {
    return uin != null;
  }

  /**
   * 是否已关闭
   */
  public boolean isClosed() {
    return closedAt != null;
  }

  /**
   * 设置UIN
   */
  private void setUin(@Nullable UserIdentificationNumber uin) {
    this.uin = uin;
    this.signInAt = uin != null ? new Date(System.currentTimeMillis()) : null;
  }

  /**
   * 用户登录
   */
  public void signIn(@NotNull UserIdentificationNumber uin) {
    // 登出一登录的其他账号
    if (isSignedIn()) {
      if (uin.equals(this.uin)) {
        return;
      }
      signOut(SignOutReason.SWITCH);
    }

    // 登出其他人登录的该账号
    Session prevSession = sessionManager.getAuthenticatedSessionByUin(uin);
    if (prevSession != null) {
      prevSession.signOut(SignOutReason.ELSEWHERE);
    }

    // 登录
    setUin(uin);
    sessionManager.onSignIn(this);

    // 发送登录载荷
    sendData("$signIn", ImmutableMap.of("uin", uin.toString()));

    // log
    if (LOG.isDebugEnabled()) {
      LOG.debug("Session " + webSocketSession.getId() + " sign in uin: " + uin.toString());
    }
  }

  /**
   * 用户登出
   */
  public void signOut(@NotNull SignOutReason reason) {
    if (!isSignedIn()) {
      return;
    }

    UserIdentificationNumber uin = this.getUin();
    sessionManager.onSignOut(this);
    setUin(null);

    // 发送登出载荷
    sendData("$signOut", ImmutableMap.of("reason", reason.getValue()));

    // log
    if (LOG.isDebugEnabled()) {
      LOG.debug("Session " + webSocketSession.getId() + " sign out uin: " + uin.toString());
    }
  }

  /**
   * 关闭
   */
  public void close() {
    UserIdentificationNumber uin = this.uin;
    signOut(SignOutReason.CLOSE);
    webSocketSession.close();
    closedAt = new Date(System.currentTimeMillis());

    // log
    if (LOG.isDebugEnabled()) {
      LOG.debug("Session " + webSocketSession.getId() + " close uin: " + uin);
    }
  }

  /**
   * 发送载荷
   */
  public void sendPayload(@NotNull Payload payload) {
    if (sink != null) {
      String rawPayload = payload.toRawPayload();
      WebSocketMessage message = webSocketSession.textMessage(rawPayload);

      sink.next(message);
    }
  }

  /**
   * 发送载荷
   */
  public void sendPayload(@NotNull String type, @Nullable Payload.Error error, @Nullable Object data) {
    sendPayload(Payload.of(type, error, data));
  }

  /**
   * 发送错误
   */
  public void sendError(@NotNull String type, @Nullable Payload.Error error) {
    sendPayload(type, error, null);
  }

  /**
   * 发送数据
   */
  public void sendData(@NotNull String type, @Nullable Object data) {
    sendPayload(type, null, data);
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  @NotNull
  public String getId() {
    return getWebSocketSession().getId();
  }

  @NotNull
  public WebSocketSession getWebSocketSession() {
    return webSocketSession;
  }

  @NotNull
  public Date getCreatedAt() {
    return createdAt;
  }

  @NotNull
  public ConcurrentMap<String, Object> getContext() {
    return context;
  }

  @NotNull
  public UserIdentificationNumber getUin() {
    return Objects.requireNonNull(uin);
  }

  @NotNull
  public Date getSignInAt() {
    return Objects.requireNonNull(signInAt);
  }

  @Nullable
  public Date getClosedAt() {
    return closedAt;
  }

  public void setSink(@Nullable FluxSink<WebSocketMessage> sink) {
    this.sink = sink;
  }

  @Nullable
  public FluxSink<WebSocketMessage> getSink() {
    return sink;
  }
}
