package com.openjdl.jsf.webflux.socket;

import com.google.protobuf.MessageLite;
import com.openjdl.jsf.webflux.socket.payload.SocketPayload;
import com.openjdl.jsf.webflux.socket.payload.SocketPayloadBody;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    byte[] body = (byte[]) payload.getBody();

    for (int i = 0; i < method.getParameterCount(); i++) {
      Class<?> type = method.getParameterTypes()[i];

      if (type == SocketSession.class) {
        parameters[i] = session;
      } else if (type == SocketPayload.class) {
        parameters[i] = payload;
      } else if (type.isAssignableFrom(byte[].class)) {
        parameters[i] = body;
      } else if (MessageLite.class.isAssignableFrom(type)) {
        try {
          Method method = type.getDeclaredMethod("parseFrom", byte[].class);
          parameters[i] = method.invoke(null, (Object) body);
        } catch (NoSuchMethodException e) {
          ExceptionUtils.rethrow(e);
        }
      } else if (SocketPayloadBody.class.isAssignableFrom(type)) {
        try {
          SocketPayloadBody instance = (SocketPayloadBody) type.newInstance();
          instance.deserialize(body);
          parameters[i] = instance;
        } catch (InstantiationException e) {
          ExceptionUtils.rethrow(e);
        }
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
