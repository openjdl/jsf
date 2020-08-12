package org.kidal.jsf.webflux.websocket;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created at 2020-08-12 10:39:04
 *
 * @author kidal
 * @since 0.1.0
 */
public class MessageHandler {
  /**
   * 目标
   */
  private final Object bean;

  /**
   * 方法
   */
  private final Method method;

  /**
   *
   */
  public MessageHandler(Object bean, Method method) {
    this.bean = bean;
    this.method = method;
  }

  /**
   * 处理消息
   */
  public Object handle(@NotNull WebSocketMessageHandlingContext context) throws InvocationTargetException, IllegalAccessException {
    return method.invoke(bean, context);
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public Object getBean() {
    return bean;
  }

  public Method getMethod() {
    return method;
  }
}
