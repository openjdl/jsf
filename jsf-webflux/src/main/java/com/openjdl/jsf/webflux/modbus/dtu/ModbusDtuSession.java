package com.openjdl.jsf.webflux.modbus.dtu;

import com.google.common.collect.Maps;
import com.openjdl.jsf.core.cipher.UserIdentificationNumber;
import com.openjdl.jsf.core.utils.DateUtils;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * Created at 2020-12-07 11:46:35
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuSession {
  /**
   * 日志
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * 管理器
   */
  @NotNull
  private final ModbusDtuSessionManager sessionManager;

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
   *
   */
  public ModbusDtuSession(@NotNull ModbusDtuSessionManager sessionManager,
                          @NotNull Channel channel) {
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
    // 登出一登录的其他账号
    if (isSignedIn()) {
      if (uin.equals(this.uin)) {
        return;
      }
      signOut(ModbusDtuSignOutReason.SWITCH);
    }

    // 登出其他人登录的该账号
    ModbusDtuSession prevSession = sessionManager.getAuthenticatedSessionByUin(uin);
    if (prevSession != null) {
      prevSession.signOut(ModbusDtuSignOutReason.ELSEWHERE);
    }

    // 登录
    setUin(uin);
    sessionManager.onSignIn(this);

    // log
    if (log.isDebugEnabled()) {
      log.debug("Session " + getId() + " sign in uin: " + uin.toString());
    }
  }

  /**
   * 用户登出
   */
  public void signOut(@NotNull ModbusDtuSignOutReason reason) {
    if (!isSignedIn()) {
      return;
    }

    UserIdentificationNumber uin = this.getUin();
    sessionManager.onSignOut(this);
    setUin(null);

    // log
    if (log.isDebugEnabled()) {
      log.debug("Session " + getId() + " sign out uin: " + uin.toString());
    }
  }

  /**
   * 关闭
   */
  public void close() {
    UserIdentificationNumber uin = this.uin;
    signOut(ModbusDtuSignOutReason.CLOSE);
    channel.close();
    closedAt = new Date(System.currentTimeMillis());

    // log
    if (log.isDebugEnabled()) {
      log.debug("Session " + getId() + " close uin: " + uin);
    }
  }

  /**
   * 发送载荷
   */
  public void send(@NotNull byte[] data) {
    channel.write(data);
  }

  //--------------------------------------------------------------------------------------------------------------
  // Object
  //--------------------------------------------------------------------------------------------------------------
  //region

  /**
   *
   */
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

  //endregion

  //--------------------------------------------------------------------------------------------------------------
  // Getters & Setters
  //--------------------------------------------------------------------------------------------------------------
  //region
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

  //endregion
}
