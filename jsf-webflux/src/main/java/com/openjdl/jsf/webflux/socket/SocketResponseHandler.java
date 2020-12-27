package com.openjdl.jsf.webflux.socket;

import com.openjdl.jsf.webflux.socket.payload.SocketPayload;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created at 2020-12-23 17:31:31
 *
 * @author zink
 * @since 2.0.0
 */
public class SocketResponseHandler {
  @NotNull
  private final Object bean;

  @NotNull
  private final Method method;

  /**
   * Ctor.
   */
  public SocketResponseHandler(@NotNull Object bean, @NotNull Method method) {
    this.bean = bean;
    this.method = method;
  }

  /**
   * 处理消息
   */
  public void handle(@NotNull SocketSession session, @NotNull SocketPayload payload) throws InvocationTargetException, IllegalAccessException {
    Object[] parameters = new Object[method.getParameterCount()];

    for (int i = 0; i < method.getParameterCount(); i++) {
      Class<?> type = method.getParameterTypes()[i];

      if (type == SocketSession.class) {
        parameters[i] = session;
      } else if (type == SocketPayload.class) {
        parameters[i] = payload;
      } else if (type.isAssignableFrom(payload.getBody().getClass())) {
        parameters[i] = payload.getBody();
      } else {
        parameters[i] = null;
      }
    }

    method.invoke(bean, parameters);
  }

  //--------------------------------------------------------------------------
  // Getters & setters
  //--------------------------------------------------------------------------
  //region


  @NotNull
  public Object getBean() {
    return bean;
  }

  @NotNull
  public Method getMethod() {
    return method;
  }

  //endRegion
}
