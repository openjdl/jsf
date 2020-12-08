package com.openjdl.jsf.webflux.modbus.dtu;

import com.google.common.collect.Maps;
import com.openjdl.jsf.core.cipher.UserIdentificationNumber;
import com.openjdl.jsf.core.utils.DateUtils;
import com.openjdl.jsf.webflux.modbus.dtu.exception.ModbusDtuSendTimeoutException;
import com.openjdl.jsf.webflux.modbus.dtu.payload.ModbusDtuPayload;
import com.openjdl.jsf.webflux.modbus.dtu.payload.request.ModbusDtuRequest;
import com.openjdl.jsf.webflux.modbus.dtu.payload.response.ModbusDtuResponse;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

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
   * 发送锁
   */
  @NotNull
  private final Object sendingSync = new Object();

  /**
   * 当前正在发送的载荷
   */
  @Nullable
  private ModbusDtuPayload sendingPayload;

  /**
   *
   */
  private int transactionId = 1;

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
      log.debug("{} sign in uin={}", this, uin);
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
      log.debug("{} sign out uin={}", this, uin);
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
  @NotNull
  public ModbusDtuPayload send(short address, @NotNull ModbusDtuRequest request) throws ModbusDtuSendTimeoutException {
    // 准备载荷
    ModbusDtuPayload sendingPayload = ModbusDtuPayload.of(ModbusDtuPayload.Type.MESSAGE);

    synchronized (sendingSync) {
      if (transactionId > 0xFFFF) {
        transactionId = 1;
      }

      sendingPayload.setId(transactionId++);
      sendingPayload.setProtocol(0x0000);
      sendingPayload.setLength(request.getByteCount() + 1);
      sendingPayload.setAddress(address);
      sendingPayload.setRequest(request);

      // 缓存
      this.sendingPayload = sendingPayload;

      // 发送
      this.channel.writeAndFlush(sendingPayload);

      // 等待载荷完成
      try {
        sendingSync.wait(TimeUnit.SECONDS.toMillis(15));
      } catch (InterruptedException ignored) {
      }

      if (sendingPayload.getResponse().equals(ModbusDtuResponse.EMPTY)) {
        // 关闭会话，让DTU重新连接，保证数据包不会错位
        close();

        // 超时异常
        throw new ModbusDtuSendTimeoutException("Send payload timeout " + this + " " + sendingPayload);
      }
    }

    // done
    return sendingPayload;
  }

  /**
   * 收到答复
   */
  boolean onResponse(@NotNull ModbusDtuPayload payload) {
    synchronized (sendingSync) {
      final ModbusDtuPayload sendingPayload = this.sendingPayload;

      // 检查匹配度
      if (sendingPayload == null) {
        return false;
      } else if (!sendingPayload.getType().equals(ModbusDtuPayload.Type.MESSAGE)) {
        return false;
      } else if (sendingPayload.getAddress() != payload.getAddress()) {
        return false;
      }

      // 设置应答
      sendingPayload.setResponse(payload.getResponse());

      // 通知载荷完成
      sendingSync.notify();
    }

    // done
    return true;
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
    return "ModbusDtuSession{" +
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
