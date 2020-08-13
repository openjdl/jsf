package org.kidal.jsf.webflux.websocket;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.unify.UnifiedApiContext;

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
  @NotNull
  private final Object bean;

  /**
   * 方法
   */
  @NotNull
  private final Method method;

  /**
   *
   */
  private final boolean unified;

  /**
   *
   */
  public MessageHandler(@NotNull Object bean, @NotNull Method method) {
    this.bean = bean;
    this.method = method;
    this.unified = method.getParameterTypes()[0] == UnifiedApiContext.class;
  }

  /**
   * 处理消息
   */
  public Object handle(@NotNull WebSocketMessageHandlingContext context) throws InvocationTargetException, IllegalAccessException {
    if (unified) {
      return method.invoke(bean, new UnifiedApiContext(context, context.getParameters()));
    } else {
      return method.invoke(bean, context);
    }
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  @NotNull
  public Object getBean() {
    return bean;
  }

  @NotNull
  public Method getMethod() {
    return method;
  }
}
