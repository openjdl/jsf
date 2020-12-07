package com.openjdl.jsf.webflux.modbus.dtu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.openjdl.jsf.core.JsfMicroServiceListener;
import com.openjdl.jsf.core.utils.SpringUtils;
import com.openjdl.jsf.webflux.boot.JsfWebFluxProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created at 2020-12-07 09:39:58
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuSessionManager implements JsfMicroServiceListener {
  /**
   * 日志
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * Spring工具
   */
  @NotNull
  private final SpringUtils springUtils;

  /**
   * 转换服务
   */
  @NotNull
  private final ConversionService conversionService;

  /**
   * 属性
   */
  @NotNull
  private final JsfWebFluxProperties.ModbusDtu properties;

  /**
   * 服务器
   */
  @NotNull
  private final List<ModbusDtuServer> servers = new ArrayList<>();

  /**
   * 匿名会话
   */
  private final ConcurrentMap<String, ModbusDtuSession> anonymousSessionMap = Maps.newConcurrentMap();

  /**
   * 已认证会话锁
   */
  private final ReentrantReadWriteLock authenticatedSessionMapLock = new ReentrantReadWriteLock(true);

  /**
   * 已认证会话(SessionId -> Session)
   */
  private final Map<String, ModbusDtuSession> authenticatedSessionMapById = Maps.newHashMap();

  /**
   * 已认证会话(UIN -> Session)
   */
  private final Map<Object, ModbusDtuSession> authenticatedSessionMapByUin = Maps.newHashMap();

  /**
   *
   */
  public ModbusDtuSessionManager(@NotNull SpringUtils springUtils,
                                 @NotNull ConversionService conversionService,
                                 @NotNull JsfWebFluxProperties.ModbusDtu properties) {
    this.springUtils = springUtils;
    this.conversionService = conversionService;
    this.properties = properties;
    this.registerSelf();
  }

  /**
   *
   */
  @Override
  public void onMicroServiceInitialized() throws InterruptedException {
    servers.addAll(
      properties
        .getServers()
        .stream()
        .map(it -> new ModbusDtuServer(it, this))
        .collect(Collectors.toList())
    );

    for (ModbusDtuServer server : servers) {
      server.initNettyServer();
    }
  }

  /**
   *
   */
  @Override
  public void onMicroServiceClosed() {
    for (ModbusDtuServer server : servers) {
      try {
        server.shutNettyServer();
      } catch (Exception e) {
        log.error("", e);
      }
    }
  }

  /**
   *
   */
  @NotNull
  public ImmutableList<ModbusDtuSession> getAnonymousSessions() {
    return ImmutableList.copyOf(anonymousSessionMap.values());
  }

  /**
   *
   */
  @NotNull
  public ImmutableList<ModbusDtuSession> getAuthenticatedSessions() {
    return ImmutableList.copyOf(authenticatedSessionMapByUin.values());
  }

  /**
   *
   */
  @Nullable
  public ModbusDtuSession getAnonymousSessionById(@NotNull String id) {
    return anonymousSessionMap.get(id);
  }

  /**
   *
   */
  @Nullable
  public ModbusDtuSession getAuthenticatedSessionById(@NotNull String id) {
    authenticatedSessionMapLock.readLock().lock();
    try {
      return authenticatedSessionMapById.get(id);
    } finally {
      authenticatedSessionMapLock.readLock().unlock();
    }
  }

  /**
   *
   */
  @Nullable
  public ModbusDtuSession getAuthenticatedSessionByUin(@NotNull Object uin) {
    authenticatedSessionMapLock.readLock().lock();
    try {
      return authenticatedSessionMapByUin.get(uin);
    } finally {
      authenticatedSessionMapLock.readLock().unlock();
    }
  }

  /**
   * 链接
   */
  void onConnect(@NotNull ModbusDtuSession session) {
    if (log.isTraceEnabled()) {
      log.trace("{} 链接", session);
    }

    anonymousSessionMap.put(session.getId(), session);
  }

  /**
   * 登录
   */
  void onSignIn(@NotNull ModbusDtuSession session) {
    if (log.isTraceEnabled()) {
      log.trace("{} 登录", session);
    }

    anonymousSessionMap.remove(session.getId());

    authenticatedSessionMapLock.writeLock().lock();
    try {
      authenticatedSessionMapByUin.put(session.getUin(), session);
      authenticatedSessionMapById.put(session.getId(), session);
    } finally {
      authenticatedSessionMapLock.writeLock().unlock();
    }
  }

  /**
   * 登出
   */
  void onSignOut(@NotNull ModbusDtuSession session) {
    if (log.isTraceEnabled()) {
      log.trace("{} 登出", session);
    }

    authenticatedSessionMapLock.writeLock().lock();
    try {
      if (session.isSignedIn()) {
        authenticatedSessionMapByUin.remove(session.getUin());
      }
      authenticatedSessionMapById.remove(session.getId());
    } finally {
      authenticatedSessionMapLock.writeLock().unlock();
    }

    anonymousSessionMap.put(session.getId(), session);
  }
}
