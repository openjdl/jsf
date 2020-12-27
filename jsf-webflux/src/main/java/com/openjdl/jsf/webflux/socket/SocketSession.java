package com.openjdl.jsf.webflux.socket;

import com.google.common.collect.Maps;
import com.openjdl.jsf.core.cipher.UserIdentificationNumber;
import com.openjdl.jsf.core.utils.DateUtils;
import com.openjdl.jsf.webflux.socket.exception.SocketPayloadTypeNotFoundException;
import com.openjdl.jsf.webflux.socket.payload.SocketPayload;
import com.openjdl.jsf.webflux.socket.payload.SocketPayloadBody;
import com.openjdl.jsf.webflux.socket.payload.SocketPayloadHeader;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created at 2020-12-23 17:39:29
 *
 * @author zink
 * @since 0.0.1
 */
public class SocketSession {
  /**
   * 日志
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * 管理器
   */
  @NotNull
  private final SocketSessionManager sessionManager;

  /**
   * Netty 会话
   */
  @NotNull
  private final Channel channel;

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
   * 发送锁
   */
  @NotNull
  private final Object sendingSync = new Object();

  /**
   * 当前正在发送的载荷
   */
  @Nullable
  private SocketPayload sendingPayload;

  /**
   *
   */
  private final AtomicLong id = new AtomicLong(1);

  /**
   *
   */
  public SocketSession(@NotNull SocketSessionManager sessionManager, @NotNull Channel channel) {
    this.sessionManager = sessionManager;
    this.channel = channel;
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
    // 登出-登录的其他账号
    if (isSignedIn()) {
      if (uin.equals(this.uin)) {
        return;
      }
      signOut(SocketSignOutReason.SWITCH);
    }

    // 登出其他人登录的该账号
    SocketSession prevSession = sessionManager.getAuthenticatedSessionByUin(uin);
    if (prevSession != null) {
      prevSession.signOut(SocketSignOutReason.ELSEWHERE);
    }

    // 登录
    setUin(uin);
    sessionManager.onSignIn(this);

    // log
    if (log.isDebugEnabled()) {
      log.debug("{} sign in uin={}", this, uin);
    }
  }

  /**
   * 用户登出
   */
  public void signOut(@NotNull SocketSignOutReason reason) {
    if (!isSignedIn()) {
      return;
    }

    UserIdentificationNumber uin = this.getUin();
    sessionManager.onSignOut(this);
    setUin(uin);

    // log
    if (log.isDebugEnabled()) {
      log.debug("{} sign out uin={}", this, uin);
    }
  }


  /**
   * 关闭
   */
  public void close() {
    UserIdentificationNumber uin = this.uin;
    signOut(SocketSignOutReason.CLOSE);
    channel.close();
    closedAt = new Date(System.currentTimeMillis());

    if (log.isDebugEnabled()) {
      log.debug("{} close uin={}", this, uin);
    }
  }

  /**
   * 延迟关闭
   */
  public void close(Date startTime) {
    sessionManager.getTaskScheduler().schedule(this::close, startTime);
  }

  /**
   * 发送载荷
   */
  public void send(@NotNull SocketPayloadBody body) throws SocketPayloadTypeNotFoundException {
    // id
    long id = this.id.getAndIncrement();

    // 获取消息体对应的类型
    Long type = sessionManager.getSocketPayloadTypeByClass(body.getClass());
    if (type == null) {
      throw new SocketPayloadTypeNotFoundException("Payload type for class " + body.getClass() + " not found");
    }

    SocketPayloadHeader header = new SocketPayloadHeader(id, type);
    SocketPayload payload = new SocketPayload(header, body);

    channel.write(payload);
  }

  @Override
  public String toString() {
    return "SocketSession{" +
      "channel=" + getChannel() +
      ", id=" + getId() +
      ", uin=" + uin +
      ", createdAt=" + DateUtils.toStringSafely(createdAt) +
      ", signInAt=" + DateUtils.toStringSafely(signInAt) +
      ", closedAt=" + DateUtils.toStringSafely(closedAt) +
      '}';
  }

  //--------------------------------------------------------------------------
  // Getters & Setters
  //--------------------------------------------------------------------------
  @NotNull
  public String getId() {
    return channel.id().asShortText();
  }

  @NotNull
  public Channel getChannel() {
    return channel;
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
}
