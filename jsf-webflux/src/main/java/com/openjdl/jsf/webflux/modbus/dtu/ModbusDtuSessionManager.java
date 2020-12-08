package com.openjdl.jsf.webflux.modbus.dtu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.openjdl.jsf.core.JsfMicroServiceListener;
import com.openjdl.jsf.core.utils.ReflectionUtils;
import com.openjdl.jsf.core.utils.SpringUtils;
import com.openjdl.jsf.webflux.boot.JsfWebFluxProperties;
import com.openjdl.jsf.webflux.modbus.dtu.annotation.ModbusDtuResponseMapping;
import com.openjdl.jsf.webflux.modbus.dtu.payload.response.ModbusDtuResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

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
   * 任务
   */
  @NotNull
  private final TaskScheduler taskScheduler;

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
   * 消息处理器
   */
  private final Map<Class<? extends ModbusDtuResponse>, ModbusDtuResponseHandler> responseHandlerMap = Maps.newHashMap();

  /**
   *
   */
  public ModbusDtuSessionManager(@NotNull SpringUtils springUtils,
                                 @NotNull TaskScheduler taskScheduler,
                                 @NotNull JsfWebFluxProperties.ModbusDtu properties) {
    this.springUtils = springUtils;
    this.taskScheduler = taskScheduler;
    this.properties = properties;
    this.registerSelf();
  }

  /**
   *
   */
  @Override
  public void onMicroServiceInitialized() throws InterruptedException {
    springUtils
      .getAllBeans(true)
      .forEach(bean -> {
        if (!bean.getClass().isAnnotationPresent(ModbusDtuResponseMapping.class)) {
          return;
        }

        ReflectionUtils.doWithMethods(
          bean.getClass(),
          method -> {
            List<Class<? extends ModbusDtuResponse>> types = new ArrayList<>();

            for (Class<?> type : method.getParameterTypes()) {
              if (ModbusDtuResponse.class.isAssignableFrom(type)) {
                //noinspection unchecked
                types.add((Class<? extends ModbusDtuResponse>) type);
              }
            }

            for (Class<? extends ModbusDtuResponse> type : types) {
              ModbusDtuResponseHandler prev = responseHandlerMap.put(type, new ModbusDtuResponseHandler(bean, method));

              if (prev != null) {
                throw new IllegalStateException(
                  String.format("ModbusDtu message handler `%s` duplicated: %s.%s or %s.%s",
                    type.getName(),
                    prev.getBean().getClass().getSimpleName(),
                    prev.getMethod().getName(),
                    bean.getClass().getSimpleName(),
                    method.getName()
                  )
                );
              } else {
                log.debug("Registered ModbusDtu message handler `{}.{}` for message `{}`",
                  bean.getClass().getSimpleName(), method.getName(), type.getName());
              }
            }
          },
          method ->
            method.isAnnotationPresent(ModbusDtuResponseMapping.class)
              && method.getParameterCount() > 0
        );
      });

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
   * 获取应答处理器
   */
  @Nullable
  public ModbusDtuResponseHandler getResponseHandler(Class<?> type) {
    return responseHandlerMap.get(type);
  }

  //--------------------------------------------------------------------------------------------------------------
  // Internal
  //--------------------------------------------------------------------------------------------------------------
  //region

  /**
   * 链接
   */
  void onConnect(@NotNull ModbusDtuSession session) {
    if (log.isTraceEnabled()) {
      log.trace("{} connect", session);
    }

    anonymousSessionMap.put(session.getId(), session);
  }

  /**
   * 登录
   */
  void onSignIn(@NotNull ModbusDtuSession session) {
    if (log.isTraceEnabled()) {
      log.trace("{} sign in", session);
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
      log.trace("{} sign out", session);
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

  //endregion

  //--------------------------------------------------------------------------------------------------------------
  // Getters & Setters
  //--------------------------------------------------------------------------------------------------------------
  //region

  @NotNull
  public SpringUtils getSpringUtils() {
    return springUtils;
  }

  @NotNull
  public TaskScheduler getTaskScheduler() {
    return taskScheduler;
  }

  @NotNull
  public JsfWebFluxProperties.ModbusDtu getProperties() {
    return properties;
  }

  //endregion
}
