package com.openjdl.jsf.webflux.socket;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.openjdl.jsf.core.JsfMicroServiceListener;
import com.openjdl.jsf.core.utils.ReflectionUtils;
import com.openjdl.jsf.core.utils.SpringUtils;
import com.openjdl.jsf.webflux.boot.JsfWebFluxProperties;
import com.openjdl.jsf.webflux.socket.annotation.SocketPayloadTypeDef;
import com.openjdl.jsf.webflux.socket.annotation.SocketRequestMapping;
import com.openjdl.jsf.webflux.socket.payload.SocketPayloadBodyExternalizable;
import com.openjdl.jsf.webflux.socket.payload.response.SocketResponse;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
 * Created at 2020-12-23 15:51:54
 *
 * @author zink
 * @since 0.0.1
 */
public class SocketSessionManager implements JsfMicroServiceListener {
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
  private final JsfWebFluxProperties.Socket properties;

  /**
   * 服务器
   */
  @NotNull
  private final List<SocketServer> servers = new ArrayList<>();

  /**
   * 匿名会话
   */
  private final ConcurrentMap<String, SocketSession> anonymousSessionMap = Maps.newConcurrentMap();

  /**
   * 已认证会话锁
   */
  private final ReentrantReadWriteLock authenticatedSessionMapLock = new ReentrantReadWriteLock(true);

  /**
   * 已认证会话(SessionId -> Session)
   */
  private final Map<String, SocketSession> authenticatedSessionMapById = Maps.newHashMap();

  /**
   * 已认证会话(UIN -> Session)
   */
  private final Map<Object, SocketSession> authenticatedSessionMapByUin = Maps.newHashMap();

  /**
   * 消息处理器
   */
  private final Map<Long, SocketResponseHandler> responseHandlerMap = Maps.newHashMap();

  /**
   * TODO
   */
  private final BiMap<Long, Class<?>> typeClassBiMap = HashBiMap.create();

  /**
   *
   */
  public SocketSessionManager(@NotNull SpringUtils springUtils,
                              @NotNull TaskScheduler taskScheduler,
                              @NotNull JsfWebFluxProperties.Socket properties) {
    this.springUtils = springUtils;
    this.taskScheduler = taskScheduler;
    this.properties = properties;
    this.registerSelf();
  }

  @Override
  public void onMicroServiceInitialized() throws InterruptedException {
    springUtils
      .getAllBeans(true)
      .forEach(bean -> {
        if (bean.getClass().isAnnotationPresent(SocketPayloadTypeDef.class)) {
          SocketPayloadTypeDef def = bean.getClass().getAnnotation(SocketPayloadTypeDef.class);

          if (!SocketPayloadBodyExternalizable.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(
              String.format("Class `%s` not implements `%s`",
                bean.getClass().getName(), SocketPayloadBodyExternalizable.class.getName()
              )
            );
          }

          if (typeClassBiMap.put(def.value(), bean.getClass()) != null) {
            throw new IllegalStateException(
              String.format("Bind class `%s` to socket payload type `%d` failed: type already exits",
                bean.getClass().getName(), def.value()
              )
            );
          }
        }
      });
    springUtils
      .getAllBeans(true)
      .forEach(bean -> {
        if (bean.getClass().isAnnotationPresent(SocketRequestMapping.class)) {

          ReflectionUtils.doWithMethods(
            bean.getClass(),
            method -> {
              SocketRequestMapping mapping = method.getAnnotation(SocketRequestMapping.class);

              long type = mapping.value();
              if (type != 0) {
                SocketResponseHandler prev = responseHandlerMap.put(type, new SocketResponseHandler(bean, method));
                if (prev != null) {
                  throw new IllegalStateException(
                    String.format("Socket message handler `%d` duplicated: %s.%s or %s.%s",
                      type,
                      prev.getBean().getClass().getSimpleName(),
                      prev.getMethod().getName(),
                      bean.getClass().getSimpleName(),
                      method.getName()
                    )
                  );
                } else {
                  log.debug("Registered Socket message handler `{}.{}` for type `{}`",
                    bean.getClass().getSimpleName(), method.getName(), type);
                }
              } else {
                List<Class<? extends SocketPayloadBodyExternalizable>> bodyTypes = new ArrayList<>();

                for (Class<?> parameterType : method.getParameterTypes()) {
                  if (SocketPayloadBodyExternalizable.class.isAssignableFrom(parameterType)) {
                    //noinspection unchecked
                    bodyTypes.add((Class<? extends SocketPayloadBodyExternalizable>) parameterType);
                  }
                }

                for (Class<? extends SocketPayloadBodyExternalizable> bodyType : bodyTypes) {
                  Long found = typeClassBiMap.inverse().get(bodyType);
                  if (found != null) {
                    SocketResponseHandler prev = responseHandlerMap.put(found, new SocketResponseHandler(bean, method));

                    if (prev != null) {
                      throw new IllegalStateException(
                        String.format("Socket message handler `%s` duplicated: %s.%s or %s.%s",
                          bodyType.getName(),
                          prev.getBean().getClass().getSimpleName(),
                          prev.getMethod().getName(),
                          bean.getClass().getSimpleName(),
                          method.getName()
                        )
                      );
                    } else {
                      log.debug("Registered Socket message handler `{}.{}` for message `{}`",
                        bean.getClass().getSimpleName(), method.getName(), bodyType.getName());
                    }
                  } else {
                    throw new IllegalStateException(
                      String.format("Socket message handler `%s` not bind to type", bodyType.getName())
                    );
                  }
                }
              }
            },
            method -> method.isAnnotationPresent(SocketRequestMapping.class)
          );
        }
      });

    servers.addAll(
      properties
        .getServers()
        .stream()
        .map(it -> new SocketServer(it, this))
        .collect(Collectors.toList())
    );

    for (SocketServer server : servers) {
      server.initNettyServer();
    }
  }

  /**
   *
   */
  @Override
  public void onMicroServiceClosed() {
    for (SocketServer server : servers) {
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
  public ImmutableList<SocketSession> getAnonymousSessions() {
    return ImmutableList.copyOf(anonymousSessionMap.values());
  }

  /**
   *
   */
  @NotNull
  public ImmutableList<SocketSession> getAuthenticatedSessions() {
    return ImmutableList.copyOf(authenticatedSessionMapByUin.values());
  }

  /**
   *
   */
  @NotNull
  public SocketSession getAnonymousSessionById(@NotNull String id) {
    return anonymousSessionMap.get(id);
  }

  /**
   *
   */
  @NotNull
  public SocketSession getAuthenticatedSessionById(@NotNull String id) {
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
  @NotNull
  public SocketSession getAuthenticatedSessionByUin(@NotNull Object uin) {
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
  @NotNull
  public SocketResponseHandler getRequestHandler(long type) {
    return responseHandlerMap.get(type);
  }

  //--------------------------------------------------------------------------------------------------------------
  // Internal
  //--------------------------------------------------------------------------------------------------------------
  //region

  /**
   * 链接
   */
  void onConnect(@NotNull SocketSession session) {
    if (log.isTraceEnabled()) {
      log.trace("{} connect", session);
    }

    anonymousSessionMap.put(session.getId(), session);
  }

  /**
   * 登录
   */
  void onSignIn(@NotNull SocketSession session) {
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
  void onSignOut(@NotNull SocketSession session) {
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

  @Nullable
  public SocketPayloadBodyExternalizable createPayloadBody(long type, ByteBuf in) {
    Class<?> bodyType = typeClassBiMap.get(type);
    if (bodyType == null) {
      return null;
    }
    try {
      SocketPayloadBodyExternalizable body = (SocketPayloadBodyExternalizable)bodyType.newInstance();
      body.deserialize(in);
      return body;
    } catch (InstantiationException | IllegalAccessException e) {
      return ExceptionUtils.rethrow(e);
    }
  }

  @Nullable
  public Long GetSocketPayloadTypeByClass(Class<?> bodyClass) {
    return typeClassBiMap.inverse().get(bodyClass);
  }

  //endregion

  //--------------------------------------------------------------------------------------------------------------
  // Getter & Setter
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
  public JsfWebFluxProperties.Socket getProperties() {
    return properties;
  }

  //endregion

}
