package com.openjdl.jsf.webflux.modbus.dtu;

import com.openjdl.jsf.webflux.modbus.dtu.payload.ModbusDtuPayload;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * DTU 应答消息处理器
 * <p>
 * Created at 2020-12-08 09:28:31
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuResponseHandler {
  @NotNull
  private final Object bean;

  @NotNull
  private final Method method;

  /**
   * Ctor.
   */
  public ModbusDtuResponseHandler(@NotNull Object bean, @NotNull Method method) {
    this.bean = bean;
    this.method = method;
  }

  /**
   * 处理消息
   */
  public void handle(@NotNull ModbusDtuSession session, @NotNull ModbusDtuPayload payload) throws InvocationTargetException, IllegalAccessException {
    Object[] parameters = new Object[method.getParameterCount()];

    for (int i = 0; i < method.getParameterCount(); i++) {
      Class<?> type = method.getParameterTypes()[i];

      if (type == ModbusDtuSession.class) {
        parameters[i] = session;
      } else if (type == ModbusDtuPayload.class) {
        parameters[i] = payload;
      } else if (type.isAssignableFrom(payload.getResponse().getClass())) {
        parameters[i] = payload.getResponse();
      }
    }

    method.invoke(bean, parameters);
  }

  //--------------------------------------------------------------------------------------------------------------
  // Getters & Setters
  //--------------------------------------------------------------------------------------------------------------
  //region

  @NotNull
  public Object getBean() {
    return bean;
  }

  @NotNull
  public Method getMethod() {
    return method;
  }

  //endregion
}
